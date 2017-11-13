package ca.ubc.best.mint.museandroidapp.vm;

import android.databinding.BaseObservable;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.choosemuse.libmuse.Muse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import eeg.useit.today.eegtoolkit.vm.ConnectionStrengthViewModel;
import eeg.useit.today.eegtoolkit.vm.StreamingDeviceViewModel;

/**
 * View Model for the flanker task - contains the order of tests,
 * the stage we're in, and the results of everything recorded so far.
 */
public class FlankerViewModel extends BaseObservable {
  public interface CompletionHandler {
    void onComplete(FlankerViewModel viewModel);
  }

  /** How many cue-stimulus pairs to perform. */
  private static final int FLANKER_TRIAL_RUNS = 10; // HACK - works for now.

  // TODO - remove once we have ordering done.
  private static Random rand = new Random();
  private static int COLOR_CUE_ON = Color.rgb(255, 255, 50);
  private static int COLOR_CUE_OFF = Color.rgb(30, 50, 255);

  /** ViewModel for the liveDevice we're attached to. */
  private final StreamingDeviceViewModel liveDevice = new StreamingDeviceViewModel();

  /** ViewModel for the live connection strength values. */
  private final ConnectionStrengthViewModel connectionVM = new ConnectionStrengthViewModel(liveDevice);

  /** Called when the experiment is over. */
  private final CompletionHandler completionHandler;

  /** Used for scheduling timed progression between states. */
  private final Handler timingHandler = new Handler();

  /** Stage we're currently in. */
  private FlankerStage stage;

  /** Time we started the current stage. */
  private long stageStartMillis = -1;

  /** Store the delay in this stage for when a tap was made. */
  private TapDetails stageTap = null;

  /** Stores all the delays for each run, or null if no tap was made. */
  private final List<TapDetails> allTaps = new ArrayList<>();

  /** Which run through the stimulus we're at. */
  private int runAt;

  /** Which stimulus we're up to. */
  private int stimulusIndex = 0;

  /** All possible stimuli in the order to show them. */
  private final List<FlankerStimulus> stimulusArray;

  public FlankerViewModel(CompletionHandler completionHandler) {
    this.completionHandler = completionHandler;
    stage = null;
    runAt = -1;

    stimulusIndex = 0;
    stimulusArray = FlankerStimulus.createStimuli(FLANKER_TRIAL_RUNS);
  }

  /** When connected, set the live device up with a muse. */
  public void attachMuse(Muse muse) {
    this.liveDevice.setMuse(muse);
    this.notifyChange();
  }

  /** Start at the very beginning, it's the very best place to start. */
  public void start() {
    assert stage == null;
    assert isConnected();
    this.beginStage(FlankerStage.PRE_CUE);
  }

  /** Run on transition to a  new stage. Calls itself to progress. */
  public void beginStage(final FlankerStage newStage) {
    // First record stuff from the previous stage if required...
    if (this.stage == FlankerStage.ARROWS) {
      allTaps.add(stageTap);
      this.stimulusIndex++;
    }

    Log.i("MINT", "Going to stage " + newStage.name());
    this.stage = newStage;
    this.stageTap = null;
    this.stageStartMillis = System.currentTimeMillis();

    if (this.stage == FlankerStage.PRE_CUE) {
      runAt++;
      if (runAt == FLANKER_TRIAL_RUNS) {
        completionHandler.onComplete(this);
        return;
      }
    }

    // Schedule the next transition.
    timingHandler.postDelayed(new Runnable() {
      @Override public void run() {
        beginStage(newStage.next());
      }
    }, newStage.durationMs);

    // And finally update the viewmodel, which causes the view to be redrawn.
    this.notifyChange();
  }

  /** @return Whether a device has connected. */
  public boolean isConnected() {
    return this.liveDevice.getMacAddress() != null;
  }

  /** @return Whether the pre-experiement connecting message is shown. */
  public boolean showConnecting() {
    return !isConnected() || (isConnected() && this.stage == null);
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

    String arrowText = stimulusArray.get(stimulusIndex).asText();
    Log.d("MINT", "Flanker Stimulus: " + arrowText);
    return arrowText;
  }

  /** @return ViewModel for connection to device. */
  public ConnectionStrengthViewModel getConnectionStrength() {
    return this.connectionVM;
  }


  /** Update after user tap, return true if we recorded it. */
  public boolean handleScreenTap(boolean isOnLeft) {
    if (stage != FlankerStage.ARROWS) {
      return false; // Not in the part of the experiment where we care about taps.
    } else if (stageTap != null) {
      return false; // Tap time already recorded, so ignore.
    }
    long reactionMs = System.currentTimeMillis() - stageStartMillis;
    Log.i("MINT", "Tapped on the " + (isOnLeft ? "left" : "right") + " after " + reactionMs + "ms");
    stageTap = new TapDetails(isOnLeft, reactionMs);
    return true;
  }
}