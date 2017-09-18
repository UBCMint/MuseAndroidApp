package ca.ubc.best.mint.museandroidapp.vm;

import android.databinding.BaseObservable;
import android.os.Handler;
import android.util.Log;

/**
 * View Model for the flanker task - contains the order of tests,
 * the stage we're in, and the results of everything recorded so far.
 */
public class FlankerViewModel extends BaseObservable {
  /** How many cue-stimulus pairs to perform. */
  private static final int FLANKER_TRIAL_RUNS = 3; // HACK - works for now.

  /** Used for scheduling timed progression between states. */
  private final Handler timingHandler = new Handler();

  /** Stage we're currently in. */
  private FlankerStage stage;

  /** Which run through the stimulus we're at. */
  private int runAt;

  public FlankerViewModel() {
    stage = null;
    runAt = -1;
  }

  /** Run on transition to a  new stage. Calls itself to progress. */
  public void beginStage(final FlankerStage stage) {
    Log.i("MINT", "Going to stage " + stage.name());
    this.stage = stage;
    if (stage == FlankerStage.PRE_CUE) {
      runAt++;
      if (runAt == FLANKER_TRIAL_RUNS) {
        // TODO: call some finish handler with results.
        return;
      }
    }

    // Schedule the next transition.
    timingHandler.postDelayed(new Runnable() {
      @Override public void run() {
        beginStage(stage.next());
      }
    }, stage.durationMs);

    // And finally update the viewmodel, which causes the view to be redrawn.
    this.notifyChange();
  }

  /** @return Whether the cue UI should be shown. */
  public boolean showCue() {
    return this.stage == FlankerStage.CUE;
  }

  /** @return Whether the arrow stimulus should be shown. */
  public boolean showArrows() {
    return this.stage == FlankerStage.ARROWS;
  }
}
