package ca.ubc.best.mint.museandroidapp.vm;

import android.databinding.BaseObservable;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import java.util.Random;

/**
 * View Model for the flanker task - contains the order of tests,
 * the stage we're in, and the results of everything recorded so far.
 */
public class FlankerViewModel extends BaseObservable {
  public static interface CompletionHandler {
    void onComplete(FlankerViewModel viewModel);
  }

  /** How many cue-stimulus pairs to perform. */
  private static final int FLANKER_TRIAL_RUNS = 3; // HACK - works for now.

  // TODO - remove once we have ordering done.
  private static Random rand = new Random();
  private static int COLOR_CUE_ON = Color.rgb(255, 255, 50);
  private static int COLOR_CUE_OFF = Color.rgb(30, 50, 255);

  /** Called when the experiment is over. */
  private final CompletionHandler completionHandler;

  /** Used for scheduling timed progression between states. */
  private final Handler timingHandler = new Handler();

  /** Stage we're currently in. */
  private FlankerStage stage;

  /** Which run through the stimulus we're at. */
  private int runAt;

  public FlankerViewModel(CompletionHandler completionHandler) {
    this.completionHandler = completionHandler;
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
        completionHandler.onComplete(this);
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

  /** @return The color for the left pointer cue. */
  public int leftPointerColor() {
    if (!showCue()) {
      return 0;
    }

    boolean isActive = rand.nextBoolean(); // TODO - not random.
    return isActive ? COLOR_CUE_ON : COLOR_CUE_OFF;
  }

  /** @return The color for the right pointer cue. */
  public int rightPointerColor() {
    if (!showCue()) {
      return 0;
    }
    boolean isActive = rand.nextBoolean(); // TODO - not random.
    return isActive ? COLOR_CUE_ON : COLOR_CUE_OFF;
  }

  /** @return The string to display for the arrows, either left or right. */
  public String arrowText() {
    if (!showArrows()) {
      return "";
    }
    boolean showLeft = rand.nextBoolean(); // TODO - not random.
    return showLeft ? ">><>>" : ">>>>>";
  }
}
