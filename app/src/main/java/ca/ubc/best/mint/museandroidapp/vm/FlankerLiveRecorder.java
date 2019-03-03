package ca.ubc.best.mint.museandroidapp.vm;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.ubc.best.mint.museandroidapp.FlankerActivity;
import ca.ubc.best.mint.museandroidapp.ResultListActivity;
import ca.ubc.best.mint.museandroidapp.Util;
import ca.ubc.best.mint.museandroidapp.analysis.ResultsPostProcessing;
import eeg.useit.today.eegtoolkit.Constants;
import eeg.useit.today.eegtoolkit.common.FrequencyBands.Band;
import eeg.useit.today.eegtoolkit.common.FrequencyBands.ValueType;
import eeg.useit.today.eegtoolkit.model.EpochCollector;
import eeg.useit.today.eegtoolkit.model.TimeSeries;
import eeg.useit.today.eegtoolkit.model.TimeSeriesSnapshot;
import eeg.useit.today.eegtoolkit.vm.FrequencyBandViewModel;
import eeg.useit.today.eegtoolkit.vm.StreamingDeviceViewModel;

import static ca.ubc.best.mint.museandroidapp.Constants.ALPHA_RESULTS_FILE;
import static ca.ubc.best.mint.museandroidapp.Constants.BETA_RESULTS_FILE;

/** Performs all live data recording for the flanker test. */
public class FlankerLiveRecorder {
  /** Used for scheduling timed progression between states. */
  private final Handler timingHandler = new Handler();

  // Alpha recording:
  // -800ms -> -300ms pre-cue for baseline
  // 150ms -> 1250ms post-cue for analysis, peak at 500
  private static final int ALPHA_COLLECTION_START_MS = ResultsPostProcessing.INITIAL_MS;
  private static final int ALPHA_COLLECTION_END_MS = ResultsPostProcessing.ALPHA_END_MS;
  private static final int ALPHA_COLLECTION_SAMPLES =
      Util.msToSamples(ALPHA_COLLECTION_END_MS - ALPHA_COLLECTION_START_MS);

  // Beta recording:
  // -800ms -> -300ms pre-cue for baseline
  // 800 -> 1300ms post-cue for analysis, peak at 500
  private static final int BETA_COLLECTION_START_MS = ResultsPostProcessing.INITIAL_MS;
  private static final int BETA_COLLECTION_END_MS = ResultsPostProcessing.BETA_END_MS;
  private static final int BETA_COLLECTION_SAMPLES =
      Util.msToSamples(BETA_COLLECTION_END_MS - BETA_COLLECTION_START_MS);

  // Create objects for collecting alpha epochs after a set delay.
  private final EpochCollector alphaEpochs = new EpochCollector();
  private final DelayCollector alphaCollector = new DelayCollector(alphaEpochs, "alpha");

  // Create objects for collecting beta epochs after a set delay.
  private final EpochCollector betaEpochs = new EpochCollector();
  private final DelayCollector betaCollector = new DelayCollector(betaEpochs, "beta");

  // Reaction time and accuracy for all recorded taps. (no reaction times for non-taps)
  private final List<Long> tapReactionTimesMs = new ArrayList<>();
  private final List<Boolean> tapAccuracy = new ArrayList<>();

  /** Creates the recorder by initializing all recorder types. */
  public FlankerLiveRecorder(StreamingDeviceViewModel device) {
    FrequencyBandViewModel liveAlpha =
        device.createFrequencyLiveValue(Band.ALPHA, ValueType.ABSOLUTE);
    TimeSeries<Double> alphaTS =
        TimeSeries.fromLiveSeriesAndCount(liveAlpha, ALPHA_COLLECTION_SAMPLES);
    Log.i("MINT", "Alpha #Samples = " + ALPHA_COLLECTION_SAMPLES);
    alphaEpochs.addSource("alpha", alphaTS);

    FrequencyBandViewModel liveBeta =
        device.createFrequencyLiveValue(Band.BETA, ValueType.ABSOLUTE);
    TimeSeries<Double> betaTS =
        TimeSeries.fromLiveSeriesAndCount(liveBeta, BETA_COLLECTION_SAMPLES);
    Log.i("MINT", "Beta #Samples = " + BETA_COLLECTION_SAMPLES);
    betaEpochs.addSource("beta", betaTS);
  }

  // Handle the cue being shown.
  public void onShowCue(FlankerCue cue) {
    // As per the paper, only analyze frequency data on RP cues.
    boolean isRPCue = (cue == FlankerCue.LRP) || (cue == FlankerCue.RRP);
    if (isRPCue) {
      timingHandler.postDelayed(alphaCollector, ALPHA_COLLECTION_END_MS);
      timingHandler.postDelayed(betaCollector, BETA_COLLECTION_END_MS);
    }
  }

  // At the end, check whether a tap happened and when/where.
  public void onRecordTap(FlankerCue cue, FlankerStimulus stim, TapDetails stageTap) {
    char needDirection = stim.asText().charAt(2);

    // When the direction is '+', you need to tap where the cue pointed.
    if (needDirection == '+') {
      if (cue == FlankerCue.LRP) {
        needDirection = '<';
      } else if (cue == FlankerCue.RRP) {
        needDirection = '>';
      } else {
        assert false : "CATCH should only happen after LRP/RRP";
      }
    }

    char gotDirection;
    if (stageTap == null) {
      gotDirection = '+';
    } else {
      gotDirection = (stageTap.wasOnLeftSide ? '<' : '>');
      tapReactionTimesMs.add(stageTap.reactionTimeMs);
    }
    tapAccuracy.add(gotDirection == needDirection);
  }

  public List<Map<String, TimeSeriesSnapshot<Double>>> getAlphaEpochs() {
    return alphaEpochs.getEpochs();
  }

  public List<Map<String, TimeSeriesSnapshot<Double>>> getBetaEpochs() {
    return betaEpochs.getEpochs();
  }

  /** @return The average reaction time, or zero after no taps. Calculates it each call. */
  public double getAverageTapReactionTimeMs() {
    if (tapReactionTimesMs.size() == 0) {
       return 0.0;
    }
    double sum = 0.0;
    for (long timeMs : tapReactionTimesMs) {
      sum += timeMs;
    }
    return sum / tapReactionTimesMs.size();
  }

  /** @return Tap accuracy proportion, from [0, 1]. */
  public double getTapAccuracyProportion() {
    if (tapAccuracy.size() == 0) {
      return 0.0;
    }
    double sum = 0.0;
    for (boolean correct : tapAccuracy) {
      sum += correct ? 1.0 : 0.0;
    }
    return sum / tapAccuracy.size();
  }

  /** Inner class to trigger an epoch collection after a delay.  */
  private static class DelayCollector implements Runnable {
    private final EpochCollector collector;
    private final String dbgName;

    public DelayCollector(EpochCollector collector, String dbgName) {
      this.collector = collector;
      this.dbgName = dbgName;
    }

    @Override
    public void run() {
      collector.collectEpoch();
   }
  }
}

