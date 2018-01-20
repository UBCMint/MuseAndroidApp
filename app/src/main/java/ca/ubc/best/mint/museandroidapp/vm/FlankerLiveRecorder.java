package ca.ubc.best.mint.museandroidapp.vm;

import android.os.Handler;
import android.util.Log;

import java.util.List;
import java.util.Map;

import eeg.useit.today.eegtoolkit.common.FrequencyBands.Band;
import eeg.useit.today.eegtoolkit.common.FrequencyBands.ValueType;
import eeg.useit.today.eegtoolkit.model.EpochCollector;
import eeg.useit.today.eegtoolkit.model.TimeSeries;
import eeg.useit.today.eegtoolkit.model.TimeSeriesSnapshot;
import eeg.useit.today.eegtoolkit.vm.FrequencyBandViewModel;
import eeg.useit.today.eegtoolkit.vm.StreamingDeviceViewModel;

/**
Performs all live data recording for the flanker test. From the paper:
===
 For the changes
 in theta and alpha power, the factors used were Group (TD, IA, CB) and Time (0–500 ms,
 500–1000 ms and 1000–1500 ms after cue). We did not perform analyses 1500 ms after cue
 onset in order to avoid spectral leakage from target processing (the targets arrived 1800 ms
 after the cue). For the beta power we used the time intervals of 800 to 1300 ms and 1300 to
 1800 ms after cue onset, to exclude overlap from alpha activity and movement artifact from
 subject response.
"""
*/
public class FlankerLiveRecorder {
  /** Used for scheduling timed progression between states. */
  private final Handler timingHandler = new Handler();

  // Alpha recording = 0 -> 1500ms after cue shown.
  private static final int ALPHA_START_MS = 0;
  private static final int ALPHA_END_MS = 1500;
  // Beta recording = 800 -> 1800 ms after cue shown.
  private static final int BETA_START_MS = 1000;
  private static final int BETA_END_MS = 1800;

  // Create objects for collecting alpha epochs after a set delay.
  private final EpochCollector alphaEpochs = new EpochCollector();
  private final DelayCollector alphaCollector = new DelayCollector(alphaEpochs, "alpha");

  // Create objects for collecting beta epochs after a set delay.
  private final EpochCollector betaEpochs = new EpochCollector();
  private final DelayCollector betaCollector = new DelayCollector(betaEpochs, "beta");

  /** Creates the recorder by initializing all recorder types. */
  public FlankerLiveRecorder(StreamingDeviceViewModel device) {
    // TODO: What ValueType to use - relative? absolute? ...
    int alphaDurationMs = ALPHA_END_MS - ALPHA_START_MS;
    FrequencyBandViewModel liveAlpha =
        device.createFrequencyLiveValue(Band.ALPHA, ValueType.RELATIVE);
    TimeSeries<Double> alphaTS = TimeSeries.fromLiveSeries(liveAlpha, alphaDurationMs);
    alphaEpochs.addSource("alpha", alphaTS);

    int betaDurationMs = BETA_END_MS - BETA_START_MS;
    FrequencyBandViewModel liveBeta =
        device.createFrequencyLiveValue(Band.BETA, ValueType.RELATIVE);
    TimeSeries<Double> betaTS = TimeSeries.fromLiveSeries(liveBeta, betaDurationMs);
    betaEpochs.addSource("beta", betaTS);
  }

  // Handle the cue being shown.
  public void onShowCue() {
    // TODO: Double check that beta is after the cue, not after the arrows?
    timingHandler.postDelayed(alphaCollector, ALPHA_END_MS);
    timingHandler.postDelayed(betaCollector, BETA_END_MS);
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
      Log.i("EPOCH", "Collected epoch for " + dbgName);
      collector.collectEpoch();
    }
  }
}

