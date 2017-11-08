package ca.ubc.best.mint.museandroidapp.vm;

/** Bundle of information about a single tap. */
public class TapDetails {
  /** Whether the tap was on the left half of the screen, or the right. */
  public final boolean wasOnLeftSide;
  /** How long after the cue was displayed that the tap was made. */
  public final long reactionTimeMs;

  public TapDetails(boolean wasOnLeftSide, long reactionTimeMs) {
    this.wasOnLeftSide = wasOnLeftSide;
    this.reactionTimeMs = reactionTimeMs;
  }
}
