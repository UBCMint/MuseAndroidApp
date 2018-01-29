package ca.ubc.best.mint.museandroidapp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ubc.best.mint.museandroidapp.databinding.ActivityTaskCompleteBinding;
import eeg.useit.today.eegtoolkit.Constants;
import eeg.useit.today.eegtoolkit.model.TimeSeriesSnapshot;

public class TaskCompleteActivity extends AppCompatActivity {
  // TODO: Just for testing, remove for final version.
  public static final String FAKE_RESULTS_FILE = "fakeResults.dat";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ParcelableResults oldResults = null;
    if (getIntent().hasExtra("debug")) {
      oldResults = hackLoadResults();
    } else {
      oldResults = getIntent().getParcelableExtra("results");
    }
    // hackSaveResults(oldResults); re-enable to save debug results if needed.

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

  /** Debugging code: Save parcel of results to file. */
  private void hackSaveResults(ParcelableResults results) {
    try {
      Log.i(Constants.TAG, "Saving to " + FAKE_RESULTS_FILE + "...");
      File file = new File(
          this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
          FAKE_RESULTS_FILE
      );
      ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
      os.writeObject(results);
      os.close();
      Log.i(Constants.TAG, "Saved!");
      Toast.makeText(this, "Saved to " + FAKE_RESULTS_FILE, Toast.LENGTH_LONG).show();
    } catch (Exception e) {
      Log.i(Constants.TAG, e.getMessage());
      e.printStackTrace();
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }
  }

  /** Debugging code: load parcel of results written by hackSaveResults. */
  private ParcelableResults hackLoadResults() {
    try {
      Log.i(Constants.TAG, "Loading from " + FAKE_RESULTS_FILE + "...");
      File file = new File(
          this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
          FAKE_RESULTS_FILE
      );
      ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
      ParcelableResults results = (ParcelableResults) is.readObject();
      is.close();
      Log.i(Constants.TAG, "Loaded!");
      Toast.makeText(this, "Using fake data...", Toast.LENGTH_LONG).show();
      return results;
    } catch (Exception e) {
      Log.i(Constants.TAG, e.getMessage());
      e.printStackTrace();
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
      return null;
    }
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
