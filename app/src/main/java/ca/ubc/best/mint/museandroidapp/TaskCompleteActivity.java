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
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;

import ca.ubc.best.mint.museandroidapp.databinding.ActivityTaskCompleteBinding;
import eeg.useit.today.eegtoolkit.Constants;
import eeg.useit.today.eegtoolkit.model.TimeSeriesSnapshot;

import static ca.ubc.best.mint.museandroidapp.Constants.ALPHA_RESULTS_FILE;
import static ca.ubc.best.mint.museandroidapp.Constants.BETA_RESULTS_FILE;

public class TaskCompleteActivity extends AppCompatActivity {
  // TODO: Just for testing, remove for final version.
  public static final String FAKE_RESULTS_FILE = "fakeResults.dat";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ParcelableResults results = getIntent().getParcelableExtra("results");

    this.saveFileAlpha(results);
    this.saveFileBeta(results);

    ActivityTaskCompleteBinding binding =
        DataBindingUtil.setContentView(this, R.layout.activity_task_complete);
    binding.setActivity(this);
    binding.setResults(results);
  }

  public void handleHomeClicked() {
    Intent intent = new Intent(this, InitialActivity.class);
    startActivity(intent);
  }


  // Saves the Alpha data and the corresponding time stamp into a text file
  private void saveFileAlpha(ParcelableResults results) {
    try {
      Log.i(Constants.TAG, "Saving to " + ALPHA_RESULTS_FILE  + "...");
      File file = new File(
              this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
              ALPHA_RESULTS_FILE
      );
      FileWriter writer = new FileWriter(file);
      for(int i = 0; i < results.alphaEpochs.size(); i++) {
        for(Map.Entry<String, TimeSeriesSnapshot<Double>> test : results.alphaEpochs.get(i).entrySet()) {
          Double[] outputData = new Double[test.getValue().values.length];
          long[] timeStamps = new long[test.getValue().timestamps.length];
          long firstTime = results.alphaEpochs.get(0).get("alpha").timestamps[0];
          System.arraycopy(test.getValue().values, 0, outputData, 0, test.getValue().values.length);
          System.arraycopy(test.getValue().timestamps, 0, timeStamps, 0, test.getValue().timestamps.length);
          for (int j = 0; j < outputData.length; j++) {
            writer.append(Long.toString((timeStamps[j] - firstTime)/(long)(Math.pow(10, 3))) + "      "
                    + Double.toString(outputData[j]) + "\n");
          }
        }
      }
      writer.flush();
      writer.close();

    } catch (Exception e){
      e.printStackTrace();
    }
  }

  // Saves the Beta data and the timestamp into a text file
  private void saveFileBeta(ParcelableResults results) {
    try {
      Log.i(Constants.TAG, "Saving to " + BETA_RESULTS_FILE  + "...");
      File file = new File(
              this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
              BETA_RESULTS_FILE
      );

      FileWriter writer = new FileWriter(file);
      for(int i = 0; i < results.betaEpochs.size(); i++) {
        for(Map.Entry<String, TimeSeriesSnapshot<Double>> test : results. betaEpochs.get(i).entrySet()) {
          Double[] outputData = new Double[test.getValue().values.length];
          long[] timeStamps = new long[test.getValue().timestamps.length];
          long firstTime = results.betaEpochs.get(0).get("alpha").timestamps[0];
          System.arraycopy(test.getValue().values, 0, outputData, 0, test.getValue().values.length);
          System.arraycopy(test.getValue().timestamps, 0, timeStamps, 0, test.getValue().timestamps.length);
          for (int j = 0; j < outputData.length; j++) {
            writer.append(Long.toString((timeStamps[j] - firstTime)/(long)(Math.pow(10, 3))) + "      " + Double.toString(outputData[j]) + "\n");
          }
        }
      }
      writer.flush();
      writer.close();

    } catch (Exception e){
      e.printStackTrace();

    }
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
}
