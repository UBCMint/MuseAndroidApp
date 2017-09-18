package ca.ubc.best.mint.museandroidapp.vm;

/** Which part of a single flanker trial we're in. */
public enum FlankerStage {
     PRE_CUE(3000),  // Waiting to show the fingers cue.
         CUE(3000),  // Showing the fingers cue.
  PRE_ARROWS(3000),  // Waiting to show the arrows command.
      ARROWS(3000);  // Showing the arrows command.

  /** How long we stay in this stage for. */
  public final int durationMs;

  FlankerStage(int durationMs) {
    this.durationMs = durationMs;
  }

  /** @return the stage to be in after this one. */
  public FlankerStage next() {
    switch (this) {
      case PRE_CUE:    return CUE;
      case CUE:        return PRE_ARROWS;
      case PRE_ARROWS: return ARROWS;
      case ARROWS:     return PRE_CUE;
    }
    throw new IllegalStateException("Missing a stage in the next() calculation.");
  }
}
