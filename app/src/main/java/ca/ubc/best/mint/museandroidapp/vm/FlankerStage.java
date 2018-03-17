package ca.ubc.best.mint.museandroidapp.vm;

import java.util.Random;

/** Which part of a single flanker trial we're in. */
public enum FlankerStage {
     PRE_CUE( 500),  // Waiting to show the fingers cue.
         CUE(1000),  // Showing the fingers cue.
  PRE_ARROWS( 800),  // Waiting to show the arrows command.
      ARROWS(1300),  // Showing the arrows command.
      RSP_WAIT(0), //TODO Wait for the user to tap in response to the stimuli
      WAIT_DISP_STIMULI(0), //TODO Display additional 700 msecs to
      WAIT_RCD_RSP(400), //Wait for additional 400 msec to record user response
      RELAX(randomRelaxTime()); // Showing the Green Border for the subject to relax their eyes

  /** How long we stay in this stage for. */
  private final int durationMs;

  FlankerStage(int durationMs) {
    this.durationMs = durationMs;
  }

  /** @return How long it lasts, or a random time if it changes per trial. */
  public int getDurationMs() {
    if (durationMs == -1) {
      return randomRelaxTime();
    } else {
      return durationMs;
    }
  }

  /** @return the stage to be in after this one. */
  public FlankerStage next() {
    switch (this) {
      case PRE_CUE:             return CUE;
      case CUE:                 return PRE_ARROWS;
      case PRE_ARROWS:          return ARROWS;
      case ARROWS:              return RSP_WAIT;
      case RSP_WAIT:            return WAIT_DISP_STIMULI; //TODO format properly
      case WAIT_DISP_STIMULI:   return WAIT_RCD_RSP;
      case WAIT_RCD_RSP:        return RELAX;
      case RELAX:               return PRE_CUE;
    }
    throw new IllegalStateException("Missing a stage in the next() calculation.");
  }

  static final int maxRelaxTime = 10400; //msec
  static final int minRelaxTime = 2400; //msec
  static public int randomRelaxTime () {
    Random r = new Random();
    int relaxTime = r.nextInt(maxRelaxTime - minRelaxTime) + minRelaxTime;
    return relaxTime;
  }

}
