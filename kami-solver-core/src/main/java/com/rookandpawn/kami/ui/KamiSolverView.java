package com.rookandpawn.kami.ui;

/**
 *
 */
public interface KamiSolverView {

  HasClickHandlers getOpenButton();

  HasClickHandlers getSolveButton();

  void showImageSelectionView();

  HasImageSelectedHandlers getImageSelector();

  void showImage(KamiImage image);

  void indicateWorking();

  void indicateNotWorking();

  void setStatus(String status, double progress);

  HasInteger getSolutionLengthController();

  void setSolution(KamiImage baseImage
      , SolutionGeometryList solution
      , PaletteList palette);

  void showSolution();
}
