package com.rookandpawn.kami;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.Uninterruptibles;
import com.rookandpawn.kami.event.ImageSelectedEvent;
import com.rookandpawn.kami.event.StartSolverEvent;
import com.rookandpawn.kami.parser.ParseException;
import com.rookandpawn.kami.parser.PuzzleContext;
import com.rookandpawn.kami.parser.PuzzleParser;
import com.rookandpawn.kami.post.SolutionGeometryMaker;
import com.rookandpawn.kami.solver.Move;
import com.rookandpawn.kami.solver.Search;
import com.rookandpawn.kami.ui.KamiImage;
import com.rookandpawn.kami.ui.KamiSolverView;
import com.rookandpawn.kami.ui.PaletteList;
import com.rookandpawn.kami.ui.SolutionGeometryList;

/**
 *
 */
public class KamiSolverPresenter {

  private final PuzzleParser puzzleParser = new PuzzleParser();

  private KamiSolverView view;

  private EventPump eventPump;

  private KamiImage currentImage;

  private final SolutionGeometryMaker solutionGeometryMaker
      = new SolutionGeometryMaker();

  public void bind(KamiSolverView view, EventBus eventBus) {
    this.view = view;
    this.eventPump = new EventPump(view);

    view.getOpenButton().addHandler(() -> {
      view.showImageSelectionView();
    });

    view.getImageSelector().addHandler(image -> {
      eventBus.post(new ImageSelectedEvent(image));
    });

    getView().getSolveButton().addHandler(() -> {

      Integer solutionLength
          = getView().getSolutionLengthController().getValue();

      if (solutionLength == null) {
        eventPump.setStatus("Please select a solution length");
        return;
      }

      eventBus.post(new StartSolverEvent(solutionLength));

    });

    eventBus.register(this);
  }

  @Subscribe
  public void onImageSelected(ImageSelectedEvent e) {
    currentImage = e.getImage();
    view.showImage(currentImage);

    if (currentImage.getWidth() > 1300) {
      currentImage = currentImage.reduceSizeByHalf();
    }

    view.indicateNotWorking();
  }

  @Subscribe
  public void onSolverStart(StartSolverEvent e) {

    if (currentImage == null) {
      return;
    }

    view.indicateWorking();
    eventPump.setStatus("Detecting Puzzle");

    PuzzleContext puzzle;

    try {
      puzzle = puzzleParser.parse(currentImage, eventPump);
    }
    catch (ParseException ex) {
      eventPump.error("Failed to detect puzzle :( -> " + ex.getMessage());
      return;
    }

    if (puzzle == null) {
      return;
    }

    eventPump.setStatus("Solving");

    Search solver = new Search(puzzle.getGraph());

    Optional<List<Move>> result = solver.go(e.getSolutionLength());

    if (result.isPresent()) {
      eventPump.setStatus("Solution Found");

      SolutionGeometryList solutionGeometries
          = solutionGeometryMaker.generateSolutionGeometries(puzzle
              , result.get(), eventPump);

      PaletteList palette = puzzle.getPalette().toPaletteList();

      eventPump.setStatus("Rendering Solution");

      getView().setSolution(puzzle.getPlayAreaImage()
          , solutionGeometries
          , palette);

      view.indicateNotWorking();
      view.showSolution();
    }
    else {
      eventPump.setStatus("No Solution Found");

      Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);

      view.indicateNotWorking();
    }
  }

  public KamiSolverView getView() {
    return view;
  }
}
