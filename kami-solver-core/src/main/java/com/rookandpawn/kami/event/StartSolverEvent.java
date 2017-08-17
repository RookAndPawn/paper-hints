package com.rookandpawn.kami.event;

/**
 *
 */
public class StartSolverEvent {

  private final int solutionLength;

  public StartSolverEvent(int solutionLength) {
    this.solutionLength = solutionLength;
  }

  /**
   * @return the solutionLength
   */
  public int getSolutionLength() {
    return solutionLength;
  }

  

}
