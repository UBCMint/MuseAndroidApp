package ca.ubc.best.mint.museandroidapp.vm;

import android.databinding.BaseObservable;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

import com.choosemuse.libmuse.Muse;

import java.util.ArrayList;
import java.util.Collections;
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

  /** How many currCue-stimulus pairs to perform. */
  private static final int FLANKER_TRIAL_RUNS = 30; // TODO For testing: Limited FLANKER_TRIAL_RUNS TO 10

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

  /** Logic for performing all the live data recording we need. */
  private final FlankerLiveRecorder recorder = new FlankerLiveRecorder(liveDevice);

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

  /** Cue/Stim for current flanker stage. */
  private FlankerStimulus currStimulus = null;
  private FlankerCue currCue = null;

  /** All possible stimuli in the order to show them. */
  private List<FlankerStimulus> neutralstimuliList = new ArrayList<>();
  private List<FlankerStimulus> congstimuliList = new ArrayList<>();
  private List<FlankerStimulus> incongstimuliList = new ArrayList<>();

  private List<StimulusCue> stimulusCueArray = new ArrayList<>();

  private int numNeut = Math.round(0.289f * FLANKER_TRIAL_RUNS); // 28.9%
  private int numCong = Math.round(0.289f * FLANKER_TRIAL_RUNS); // 28.9%, same as cong
  private int numIncong = FLANKER_TRIAL_RUNS - numNeut - numCong; // the rest, ~42.2%

  public FlankerViewModel(CompletionHandler completionHandler) {
    this.completionHandler = completionHandler;
    stage = null;
    runAt = -1;

    stimulusIndex = 0;
    createStimulusCuePairList(); //create and link stimuli with its cues
  }

  public void createStimulusCuePairList() {

    neutralstimuliList = FlankerStimulus.createNeutralStimuli(numNeut);
    Log.d("neutralstimuliList", String.valueOf(neutralstimuliList));
    congstimuliList = FlankerStimulus.createCongStimuli(numCong);
    Log.d("congstimuliList", String.valueOf(congstimuliList));
    incongstimuliList = FlankerStimulus.createIncongStimuli(numIncong);
    Log.d("incongstimuliList", String.valueOf(incongstimuliList));

    //add neutral stimuli and its cues
    stimulusCueArray.addAll(StimulusCue.createNeutralStimuliCueList(neutralstimuliList));
    Log.d("stimulusCueArray",String.valueOf(stimulusCueArray));
    //add congruent stimuli and its cues
    stimulusCueArray.addAll(StimulusCue.createCongStimuliCueList(congstimuliList));
    Log.d("stimulusCueArray",String.valueOf(stimulusCueArray));
    //add incongruent stimuli and its cues
    stimulusCueArray.addAll(StimulusCue.createIncongStimuliCueList(incongstimuliList));
    Log.d("stimulusCueArray",String.valueOf(stimulusCueArray));
    //randomize the stimulus cue array
    Collections.shuffle(stimulusCueArray);
    Log.d("stimulusCueArray",String.valueOf(stimulusCueArray));
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
      recorder.onRecordTap(currCue, currStimulus, stageTap);
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

      //arrows are determined at PRE_CUE stage in order for CUE to correlate to a corresponding arrow
      this.currStimulus = stimulusCueArray.get(stimulusIndex).stimulus;
      this.currCue = stimulusCueArray.get(stimulusIndex).cue;
      this.stimulusIndex++;

    } else if (this.stage == FlankerStage.CUE) {
      recorder.onShowCue(this.currCue);

    }

    // Schedule the next transition.
    timingHandler.postDelayed(new Runnable() {
      @Override public void run() {
        beginStage(newStage.next());
      }
    }, newStage.getDurationMs());

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

  /** @return What text to show on the pre-start splash. */
  public String getConnectingText() {
    if (showConnecting()) {
      return isConnected() ? "Device connected" : "Connecting to device...";
    } else {
      return "";
    }
  }

  /** @return Whether the 500ms short white border UI should be shown. */
  public boolean showWHTBDR() {return this.stage != FlankerStage.RELAX;}

  /** @return Whether the currCue UI should be shown. */
  public boolean showCue() {
    return this.stage == FlankerStage.CUE;
  }
    //TODO rename ARROWS TO STIMULUS

  /** @return Whether the arrow stimulus should be shown. */
  public boolean showArrows() {
    return this.stage == FlankerStage.ARROWS;
  }

  /** @return Whether the green border should be shown. */
  public boolean showGRNBDR() {return this.stage == FlankerStage.RELAX; }


  /** @return The color for the left pointer currCue. */
  public int leftPointerColor() {
    if (!showCue()) {
      return 0;
    }

    switch(this.currCue) {
        case NULL:
            return COLOR_CUE_OFF;
        case RRP:
            return COLOR_CUE_OFF;
        case LRP:
            return COLOR_CUE_ON;
        case WARN:
            return COLOR_CUE_ON;
        default:
            return COLOR_CUE_OFF;
    }

  }

  /** @return The color for the right pointer currCue. */
  public int rightPointerColor() {
    if (!showCue()) {
      return 0;
    }
    switch(this.currCue) {
      case NULL:
          return COLOR_CUE_OFF;
      case RRP:
          return COLOR_CUE_ON;
      case LRP:
          return COLOR_CUE_OFF;
      case WARN:
          return COLOR_CUE_ON;
      default:
          return COLOR_CUE_OFF;
    }

  }

  /** @return The string to display for the arrows, either left or right. */
  public String getArrowText() {
    if (!showArrows()) {
      return "";
    }

    Log.d("MINT", "Flanker Stimulus: " + this.currStimulus.asText());
    return this.currStimulus.asText();
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

  public FlankerLiveRecorder getRecorder() {
    return this.recorder;
  }

  public int counter() {
    return runAt;
  }
}
