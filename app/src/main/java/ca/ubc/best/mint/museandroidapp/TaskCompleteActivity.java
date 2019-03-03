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
import eeg.useit.today.eegtoolkit.model.TimeSeries;
import eeg.useit.today.eegtoolkit.model.TimeSeriesSnapshot;

import static ca.ubc.best.mint.museandroidapp.Constants.ALPHA_RESULTS_FILE;

public class TaskCompleteActivity extends AppCompatActivity {
  // TODO: Just for testing, remove for final version.
  public static final String FAKE_RESULTS_FILE = "fakeResults.dat";
  public ArrayList<TimeSeriesSnapshot<Double>> tempHold = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ParcelableResults results = getIntent().getParcelableExtra("results");

    /*for(int i = 0; i < results.alphaEpochs.size(); i++) {
      for(Map.Entry<String, TimeSeriesSnapshot<Double>> test : results.alphaEpochs.get(i).entrySet()) {
        tempHold.add(test.getValue());
        this.saveFile(tempHold, i);
      }
    }*/

    this.saveFile(results);

    ActivityTaskCompleteBinding binding =
        DataBindingUtil.setContentView(this, R.layout.activity_task_complete);
    binding.setActivity(this);
    binding.setResults(results);
  }

  public void handleHomeClicked() {
    Intent intent = new Intent(this, InitialActivity.class);
    startActivity(intent);
  }

  private void saveFile(ParcelableResults results) {
    try {
      Log.i(Constants.TAG, "Saving to " + ALPHA_RESULTS_FILE  + "...");
      File file = new File(
              this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
              ALPHA_RESULTS_FILE
      );

      FileWriter writer = new FileWriter(file);
      for(int i = 0; i < results.alphaEpochs.size(); i++) {
        writer.append("Muse Probe: " + Integer.toString(i) + "\n");
        Log.i(Constants.TAG, "This Muse Probe is: " + Integer.toString(i));
        for(Map.Entry<String, TimeSeriesSnapshot<Double>> test : results.alphaEpochs.get(i).entrySet()) {
          Double[] outputData = new Double[test.getValue().length];
          outputData = test.getValue().values;
          for (int j = 0; j < outputData.length; j++) {
            writer.append(Double.toString(outputData[j]) + "\n");
            Log.i(Constants.TAG, "These are the Double Data " + Double.toString(outputData[j]));
          }
        }
      }
      writer.flush();
      writer.close();

    } catch (Exception e){
      e.printStackTrace();

    }
      /*ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
      os.writeObject(data);
      os.close();
      Log.i(Constants.TAG, "Saved!");
      Toast.makeText(this, "Saved to " + ALPHA_RESULTS_FILE, Toast.LENGTH_LONG).show();
    } catch (Exception e) {
      Log.i(Constants.TAG, e.getMessage());
      e.printStackTrace();
      Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
    }*/
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
