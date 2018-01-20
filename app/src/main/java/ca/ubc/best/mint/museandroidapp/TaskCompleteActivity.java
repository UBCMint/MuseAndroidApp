package ca.ubc.best.mint.museandroidapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ubc.best.mint.museandroidapp.databinding.ActivityTaskCompleteBinding;
import eeg.useit.today.eegtoolkit.Constants;
import eeg.useit.today.eegtoolkit.model.TimeSeriesSnapshot;

public class TaskCompleteActivity extends AppCompatActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ParcelableResults oldResults = getIntent().getParcelableExtra("results");
    ParcelableResults results = new ParcelableResults(
        hackNormalize(oldResults.alphaEpochs, 0),
        hackNormalize(oldResults.betaEpochs, 0)
    );

    ActivityTaskCompleteBinding binding =
        DataBindingUtil.setContentView(this, R.layout.activity_task_complete);
    binding.setActivity(this);
    binding.setResults(results);
  }

  public void handleHomeClicked() {
    Intent intent = new Intent(this, InitialActivity.class);
    startActivity(intent);
  }

  ///
  /// HACK - please ignore below for now. This normalizes the epoch snapshot values
  /// so they show up nicely on the epoch viewer.
  /// Some features need to be added to the epoch viewer, then this can be removed.
  ///
  private static List<Map<String, TimeSeriesSnapshot<Double>>> hackNormalize(
      List<Map<String, TimeSeriesSnapshot<Double>>> epoch, int zeroSample) {
    double minDelta = 0.0;
    double maxDelta = 0.0;
    for (int i = 0; i < epoch.size(); i++) {
      for (String key : epoch.get(i).keySet()) {
        Double[] values = epoch.get(i).get(key).values;
        for (int j = 0; j < values.length; j++) {
          double delta = values[j] - values[zeroSample];
          minDelta = Math.min(delta, minDelta);
          maxDelta = Math.max(delta, maxDelta);
        }
      }
    }
    List<Map<String, TimeSeriesSnapshot<Double>>> result = new ArrayList<>();
    for (int i = 0; i < epoch.size(); i++) {
      Map<String, TimeSeriesSnapshot<Double>> normed = new HashMap<>();
      for (String key : epoch.get(i).keySet()) {
        normed.put(key, hackNormalize(epoch.get(i).get(key), zeroSample, minDelta, maxDelta));
      }
      result.add(normed);
    }
    return result;
  }
  private static TimeSeriesSnapshot<Double> hackNormalize(
      TimeSeriesSnapshot<Double> snapshot, int zeroSample, double minDelta, double maxDelta) {
    int n = snapshot.values.length;
    Double[] normValues = new Double[n];
    for (int i = 0; i < n; i++) {
      double delta = snapshot.values[i] - snapshot.values[zeroSample];
      normValues[i] = (delta - minDelta) / (maxDelta - minDelta);
      normValues[i] = Constants.VOLTAGE_MIN + normValues[i] * (Constants.VOLTAGE_MAX - Constants.VOLTAGE_MIN);
    }
    return new TimeSeriesSnapshot<Double>(snapshot.timestamps, normValues);
  }
  ///
  /// End hack :)
  ///
}
