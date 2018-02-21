package ca.ubc.best.mint.museandroidapp.analysis;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ca.ubc.best.mint.museandroidapp.ParcelableResults;
import ca.ubc.best.mint.museandroidapp.TaskCompleteActivity;
import eeg.useit.today.eegtoolkit.Constants;

import static ca.ubc.best.mint.museandroidapp.Constants.HISTORIC_RESULTS_FILE;

/** List of all past results. */
public class HistoricResults implements Serializable {
  private static final long serialVersionUID = 9328427L;

  private final List<ParcelableResults> results;

  public HistoricResults(List<ParcelableResults> results) {
    this.results = results;
  }

  /** Add a new result to the history (not saved yet...) */
  public void addResult(ParcelableResults result) {
    this.results.add(result);
  }

  /** @return Result at a given position. */
  public ParcelableResults getResult(int position) {
    return results.get(position);
  }

  /** @return Number of recorded results. */
  public int size() {
    return results.size();
  }

  /** Handles a result being selected from the list. */
  public void onSelect(Context ctx, ParcelableResults results) {
    Intent intent = new Intent(ctx, TaskCompleteActivity.class);
    intent.putExtra("results", (Parcelable) results);
    ctx.startActivity(intent);
  }

  /** Save the results to file. */
  public void save(Context ctx) {
    try {
      Log.i(Constants.TAG, "Saving " + results.size() + " results to "
          + HISTORIC_RESULTS_FILE + "...");
      File file = new File(
          ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
          HISTORIC_RESULTS_FILE
      );
      ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(file));
      os.writeObject(this);
      os.close();
      Log.i(Constants.TAG, "Saved!");
    } catch (Exception e) {
      Log.i(Constants.TAG, e.getMessage());
      e.printStackTrace();
    }
  }

  /** @return The saved past results, or an empty result list if none exists yet. */
  public static HistoricResults loadFromFile(Context ctx) {
    try {
      Log.i("MINT", "Loading from " + HISTORIC_RESULTS_FILE + "...");
      File file = new File(
          ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
          HISTORIC_RESULTS_FILE
      );
      ObjectInputStream is = new ObjectInputStream(new FileInputStream(file));
      HistoricResults results = (HistoricResults) is.readObject();
      is.close();
      Log.i("MINT", "Loaded " + results.results.size() + " historic results!");
      return results;
    } catch (Exception e) {
      Log.i("MINT", e.getMessage());
      e.printStackTrace();
      return new HistoricResults(new ArrayList<ParcelableResults>());
    }
  }
}
