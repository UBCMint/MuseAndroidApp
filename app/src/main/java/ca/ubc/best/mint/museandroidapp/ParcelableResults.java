package ca.ubc.best.mint.museandroidapp;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eeg.useit.today.eegtoolkit.model.TimeSeriesSnapshot;

/**
 * Make the results from the experiments parcelable, so they can be sent
 * from the recording intent to the results intent.
 */
public class ParcelableResults implements Parcelable, Serializable {
  private static final long serialVersionUID = 1720974L;

  /** All results so far - just the epochs for alpha and beta power. */
  public final List<Map<String, TimeSeriesSnapshot<Double>>> alphaEpochs;
  public final List<Map<String, TimeSeriesSnapshot<Double>>> betaEpochs;

  /** Scores calculated from post-processed data. */
  public final double alphaSuppression;
  public final double betaSuppression;

  /** When the experiment these come from was finished. */
  public final Date timeOfExperiment;

  /** Average delay time for tapping. */
  public final double tapReactionMs;

  /** Proportion of times the tap was correct. */
  public final double tapAccuracy;

  public ParcelableResults(
      List<Map<String, TimeSeriesSnapshot<Double>>> alphaEpochs,
      List<Map<String, TimeSeriesSnapshot<Double>>> betaEpochs,
      double alphaSuppression,
      double betaSuppression,
      Date timeOfExperiment,
      double tapReactionMs,
      double tapAccuracy
    ) {
    this.alphaEpochs = alphaEpochs;
    this.betaEpochs = betaEpochs;
    this.alphaSuppression = alphaSuppression;
    this.betaSuppression = betaSuppression;
    this.timeOfExperiment = timeOfExperiment;
    this.tapReactionMs = tapReactionMs;
    this.tapAccuracy = tapAccuracy;
  }

  /** Parcel -> ParcelableResults */
  protected ParcelableResults(Parcel in) {
    alphaEpochs = readEpochs(in);
    betaEpochs = readEpochs(in);
    alphaSuppression = in.readDouble();
    betaSuppression = in.readDouble();
    timeOfExperiment = new Date(in.readLong());
    tapReactionMs = in.readDouble();
    tapAccuracy = in.readDouble();
  }

  /** ParcelableResults -> Parcel */
  @Override
  public void writeToParcel(Parcel dest, int flags) {
    writeEpochs(dest, flags, alphaEpochs);
    writeEpochs(dest, flags, betaEpochs);
    dest.writeDouble(alphaSuppression);
    dest.writeDouble(betaSuppression);
    dest.writeLong(timeOfExperiment.getTime());
    dest.writeDouble(tapReactionMs);
    dest.writeDouble(tapAccuracy);
  }

  //
  // Visual formatting
  //
  public String titleText() {
    return String.format("Results for %s", dateCaption());
  }
  public String alphaCaption() {
    return String.format("Alpha: %.2f", this.alphaSuppression);
  }
  public String betaCaption() {
    return String.format("Beta: %.2f", this.betaSuppression);
  }
  public String alphaEpochCaption() {
    return String.format("Alpha epochs: (suppression = %.2f)", this.alphaSuppression);
  }
  public String betaEpochCaption() {
    return String.format("Beta epochs: (suppression = %.2f)", this.betaSuppression);
  }
  public String dateCaption() {
    return Constants.DATE_FORMATTER.format(this.timeOfExperiment);
  }
  public String reactionTimeCaption() {
    return String.format("%d ms", (int)tapReactionMs); // Round to nearest integer.
  }
  public String accuracyCaption() {
    return String.format("%d%%", (int)(tapAccuracy * 100.0)); // Round to nearest whole percent.
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

  /** @return List of epoch recordings data, read from a parcel. */
  private static List<Map<String, TimeSeriesSnapshot<Double>>> readEpochs(Parcel in) {
    List<Map<String, TimeSeriesSnapshot<Double>>> result = new ArrayList<>();
    int sz = in.readInt();
    for (int i = 0; i < sz; i++) {
      result.add(readEpoch(in));
    }
    return result;
  }

  /** @return Single epoch recording, read from a parcel. */
  private static Map<String, TimeSeriesSnapshot<Double>> readEpoch(Parcel in) {
    Map<String, TimeSeriesSnapshot<Double>> result = new HashMap<>();
    int sz = in.readInt();
    for (int i = 0; i < sz; i++) {
      result.put(in.readString(), readSnapshot(in));
    }
    return result;
  }

  /** @return Snapshot of a time series, read from a parcel. */
  private static TimeSeriesSnapshot<Double> readSnapshot(Parcel in) {
    long[] timestamps = new long[in.readInt()];
    in.readLongArray(timestamps);
    double[] values = new double[in.readInt()];
    in.readDoubleArray(values);
    Double[] boxedValues = new Double[values.length];
    for (int i = 0; i < values.length; i++) {
      boxedValues[i] = values[i];
    }
    return new TimeSeriesSnapshot<>(timestamps, boxedValues);
  }

  /** Write epoch recordings data, reverse of readEpochs. */
  private static void writeEpochs(Parcel out, int flags, List<Map<String, TimeSeriesSnapshot<Double>>> epochs) {
    out.writeInt(epochs.size());
    for (Map<String, TimeSeriesSnapshot<Double>> epoch : epochs) {
      writeEpoch(out, flags, epoch);
    }
  }

  /** Write single epoch recording, reverse of readEpoch. */
  private static void writeEpoch(Parcel out, int flags, Map<String, TimeSeriesSnapshot<Double>> epoch) {
    out.writeInt(epoch.size());
    for (Map.Entry<String, TimeSeriesSnapshot<Double>> entry : epoch.entrySet()) {
      out.writeString(entry.getKey());
      writeSnapshot(out, flags, entry.getValue());
    }
  }

  /** Write snapshot of a time series, reverse of readSnapshot. */
  private static void writeSnapshot(Parcel out, int flags, TimeSeriesSnapshot<Double> snapshot) {
    out.writeInt(snapshot.timestamps.length);
    out.writeLongArray(snapshot.timestamps);
    double[] unboxedValues = new double[snapshot.values.length];
    for (int i = 0; i < unboxedValues.length; i++) {
      unboxedValues[i] = snapshot.values[i];
    }
    out.writeInt(unboxedValues.length);
    out.writeDoubleArray(unboxedValues);
  }
}
