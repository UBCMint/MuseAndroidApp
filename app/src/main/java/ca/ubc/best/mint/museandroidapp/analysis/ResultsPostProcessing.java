package ca.ubc.best.mint.museandroidapp.analysis;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ubc.best.mint.museandroidapp.ParcelableResults;
import eeg.useit.today.eegtoolkit.model.TimeSeriesSnapshot;

import static ca.ubc.best.mint.museandroidapp.Util.msToSamples;

/** Wraps results with post-processing logic. */
public class ResultsPostProcessing {
  // NOTE: These should be multiples of 100, as each sample covers 100ms.
  public static final int INITIAL_MS = -800; // Start recording 800ms *before* stimulus for baseline
  public static final int ALPHA_END_MS = 1200; // Alpha suppression ends around 1200ms post stimulus
  public static final int BETA_END_MS = 1300; // Beta suppression ends around 1300ms post stimulus

  private static final int BASELINE_SAMPLES = msToSamples(500); // -800ms to -300ms.
  private static final int TRIM_SAMPLES = msToSamples(Math.abs(INITIAL_MS));

  private static final int ALPHA_SUPPRESSION_START_SAMPLES = msToSamples(200);
  private static final int ALPHA_SUPPRESSION_END_SAMPLES = msToSamples(ALPHA_END_MS);

  private static final int BETA_SUPPRESSION_START_SAMPLES = msToSamples(800);
  private static final int BETA_SUPPRESSION_END_SAMPLES = msToSamples(BETA_END_MS);

  private ResultsPostProcessing() { /* Don't create. */ }

  /**
   * Post-processing of results:
   *   1) For each epoch, subtract baseline and trim to simulus time.
   *   2) Calculate average suppression over the desired time periods.
   */
  public static ParcelableResults process(
      List<Map<String, TimeSeriesSnapshot<Double>>> rawAlpha,
      List<Map<String, TimeSeriesSnapshot<Double>>> rawBeta
  ) {
    List<Map<String, TimeSeriesSnapshot<Double>>> alpha = processAllAlpha(rawAlpha);
    List<Map<String, TimeSeriesSnapshot<Double>>> beta = processAllBeta(rawBeta);
    return new ParcelableResults(
        alpha, beta,
        calcAlphaSuppression(alpha),
        calcBetaSuppression(beta)
    );
  }

  // Process all alpha epochs by processing each individial snapshot separately.
  public static List<Map<String, TimeSeriesSnapshot<Double>>> processAllAlpha(
      List<Map<String, TimeSeriesSnapshot<Double>>> alphas) {
    List<Map<String, TimeSeriesSnapshot<Double>>> results = new ArrayList<>();
    for (Map<String, TimeSeriesSnapshot<Double>> alpha : alphas) {
      Map<String, TimeSeriesSnapshot<Double>> snapshots = new HashMap<>();
      for (String snapshotKey : alpha.keySet()) {
        snapshots.put(snapshotKey, baselineCorrectAndTrim(alpha.get(snapshotKey)));
      }
      results.add(snapshots);
    }
    return results;
  }

  // Process all beta epochs by processing each individial snapshot separately.
  public static List<Map<String, TimeSeriesSnapshot<Double>>> processAllBeta(
      List<Map<String, TimeSeriesSnapshot<Double>>> betas) {
    List<Map<String, TimeSeriesSnapshot<Double>>> results = new ArrayList<>();
    for (Map<String, TimeSeriesSnapshot<Double>> beta : betas) {
      Map<String, TimeSeriesSnapshot<Double>> snapshots = new HashMap<>();
      for (String snapshotKey : beta.keySet()) {
        snapshots.put(snapshotKey, baselineCorrectAndTrim(beta.get(snapshotKey)));
      }
      results.add(snapshots);
    }
    return results;
  }

  /** @return Alpha suppression, calculated as the average drop in the desired time frame. */
  private static double calcAlphaSuppression(List<Map<String, TimeSeriesSnapshot<Double>>> alphas) {
    double sum = 0.0;
    int count = 0;
    for (Map<String, TimeSeriesSnapshot<Double>> epochs : alphas) {
      for (TimeSeriesSnapshot<Double> snapshot : epochs.values()) {
        sum += averageInRange(
            snapshot.values,
            ALPHA_SUPPRESSION_START_SAMPLES,
            ALPHA_SUPPRESSION_END_SAMPLES
        ) * -1.0; // Negate, as higher suppression = lower values.
        count++;
      }
    }
    return sum / count;
  }

  /** @return Beta suppression, calculated as the average drop in the desired time frame. */
  private static double calcBetaSuppression(List<Map<String, TimeSeriesSnapshot<Double>>> betas) {
    double sum = 0.0;
    int count = 0;
    for (Map<String, TimeSeriesSnapshot<Double>> epochs : betas) {
      for (TimeSeriesSnapshot<Double> snapshot : epochs.values()) {
        sum += averageInRange(
            snapshot.values,
            BETA_SUPPRESSION_START_SAMPLES,
            BETA_SUPPRESSION_END_SAMPLES
        ) * -1.0; // Negate, as higher suppression = lower values.
        count++;
      }
    }
    return sum / count;
  }

  /**
   * Given a snapshot, first calculate a baseline rate using the average of the first samples.
   * Apply baseline correction by subtracting this from everything.
   * Finally, skip the initial values as they are before the stimuli actually happened, and only
   * used for the baseline calculation
   */
  private static TimeSeriesSnapshot<Double> baselineCorrectAndTrim(
      TimeSeriesSnapshot<Double> snapshot
  ) {
    Log.i("MINT", "Base and Trim, " + snapshot.length + " values," +
        " base = " + BASELINE_SAMPLES + " trim = " + TRIM_SAMPLES);
    double baseline = averageInRange(snapshot.values, 0, BASELINE_SAMPLES);

    int newCount = snapshot.length - TRIM_SAMPLES;
    long[] newTimes = new long[newCount];
    Double[] newValues = new Double[newCount];
    for (int i = 0; i < newCount; i++) {
      newTimes[i] = snapshot.timestamps[i + TRIM_SAMPLES];
      newValues[i] = snapshot.values[i + TRIM_SAMPLES] - baseline;
    }
    return new TimeSeriesSnapshot<>(newTimes, newValues);
  }

  /** @return The mean of all values[start..end). */
  private static double averageInRange(Double[] values, int start, int end) {
    double sum = 0;
    for (int i = start; i < end; i++) {
      sum += values[i];
    }
    return sum / (end - start);
  }
}


