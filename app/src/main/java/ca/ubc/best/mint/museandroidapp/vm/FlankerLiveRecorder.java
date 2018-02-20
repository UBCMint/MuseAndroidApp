package ca.ubc.best.mint.museandroidapp.vm;

import android.os.Handler;
import android.util.Log;

import java.util.List;
import java.util.Map;

import ca.ubc.best.mint.museandroidapp.Util;
import ca.ubc.best.mint.museandroidapp.analysis.ResultsPostProcessing;
import eeg.useit.today.eegtoolkit.common.FrequencyBands.Band;
import eeg.useit.today.eegtoolkit.common.FrequencyBands.ValueType;
import eeg.useit.today.eegtoolkit.model.EpochCollector;
import eeg.useit.today.eegtoolkit.model.TimeSeries;
import eeg.useit.today.eegtoolkit.model.TimeSeriesSnapshot;
import eeg.useit.today.eegtoolkit.vm.FrequencyBandViewModel;
import eeg.useit.today.eegtoolkit.vm.StreamingDeviceViewModel;

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
  public void onShowCue() {
    timingHandler.postDelayed(alphaCollector, ALPHA_COLLECTION_END_MS);
    timingHandler.postDelayed(betaCollector, BETA_COLLECTION_END_MS);
  }

  public List<Map<String, TimeSeriesSnapshot<Double>>> getAlphaEpochs() {
    return alphaEpochs.getEpochs();
  }

  public List<Map<String, TimeSeriesSnapshot<Double>>> getBetaEpochs() {
    return betaEpochs.getEpochs();
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

