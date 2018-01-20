package ca.ubc.best.mint.museandroidapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eeg.useit.today.eegtoolkit.model.TimeSeriesSnapshot;

// HACK
public class ParcelableResults implements Parcelable {
  public final List<Map<String, TimeSeriesSnapshot<Double>>> alphaEpochs;
  public final List<Map<String, TimeSeriesSnapshot<Double>>> betaEpochs;

  public ParcelableResults(
      List<Map<String, TimeSeriesSnapshot<Double>>> alphaEpochs,
      List<Map<String, TimeSeriesSnapshot<Double>>> betaEpochs
  ) {
    this.alphaEpochs = alphaEpochs;
    this.betaEpochs = betaEpochs;
  }

  protected ParcelableResults(Parcel in) {
    alphaEpochs = readEpochs(in);
    betaEpochs = readEpochs(in);
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    writeEpochs(dest, flags, alphaEpochs);
    writeEpochs(dest, flags, betaEpochs);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  public static final Creator<ParcelableResults> CREATOR = new Creator<ParcelableResults>() {
    @Override
    public ParcelableResults createFromParcel(Parcel in) {
      return new ParcelableResults(in);
    }

    @Override
    public ParcelableResults[] newArray(int size) {
      return new ParcelableResults[size];
    }
  };

  // Parcel to objects
  private static List<Map<String, TimeSeriesSnapshot<Double>>> readEpochs(Parcel in) {
    List<Map<String, TimeSeriesSnapshot<Double>>> result = new ArrayList<>();
    int sz = in.readInt();
    Log.i("MINT IN", "nE = " + sz);
    for (int i = 0; i < sz; i++) {
      result.add(readEpoch(in));
    }
    return result;
  }
  private static Map<String, TimeSeriesSnapshot<Double>> readEpoch(Parcel in) {
    Map<String, TimeSeriesSnapshot<Double>> result = new HashMap<>();
    int sz = in.readInt();
    Log.i("MINT IN", "nK = " + sz);
    for (int i = 0; i < sz; i++) {
      result.put(in.readString(), readSnapshot(in));
    }
    return result;
  }
  private static TimeSeriesSnapshot<Double> readSnapshot(Parcel in) {
    long[] timestamps = new long[in.readInt()];
    Log.i("MINT IN", "nT = " + timestamps.length);
    in.readLongArray(timestamps);
    double[] values = new double[in.readInt()];
    Log.i("MINT IN", "nV = " + values.length);
    in.readDoubleArray(values);
    Double[] boxedValues = new Double[values.length];

    for (int i = 0; i < values.length; i++) {
      boxedValues[i] = values[i];
    }
    return new TimeSeriesSnapshot<>(timestamps, boxedValues);
  }

  // Objects to parcel
  private static void writeEpochs(Parcel out, int flags, List<Map<String, TimeSeriesSnapshot<Double>>> epochs) {
    Log.i("MINT OUT", "nE = " + epochs.size());
    out.writeInt(epochs.size());
    for (Map<String, TimeSeriesSnapshot<Double>> epoch : epochs) {
      writeEpoch(out, flags, epoch);
    }
  }
  private static void writeEpoch(Parcel out, int flags, Map<String, TimeSeriesSnapshot<Double>> epoch) {
    Log.i("MINT OUT", "nK = " + epoch.size());
    out.writeInt(epoch.size());
    for (Map.Entry<String, TimeSeriesSnapshot<Double>> entry : epoch.entrySet()) {
      out.writeString(entry.getKey());
      writeSnapshot(out, flags, entry.getValue());
    }
  }
  private static void writeSnapshot(Parcel out, int flags, TimeSeriesSnapshot<Double> snapshot) {
    out.writeInt(snapshot.timestamps.length);
    Log.i("MINT OUT", "nT = " + snapshot.timestamps.length);
    out.writeLongArray(snapshot.timestamps);
    double[] unboxedValues = new double[snapshot.values.length];
    for (int i = 0; i < unboxedValues.length; i++) {
      unboxedValues[i] = snapshot.values[i];
    }
    Log.i("MINT OUT", "nV = " + unboxedValues.length);
    out.writeInt(unboxedValues.length);
    out.writeDoubleArray(unboxedValues);
  }
}
