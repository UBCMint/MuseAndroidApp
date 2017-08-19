package ca.ubc.best.mint.museandroidapp.vm;

import android.content.Context;
import android.databinding.BaseObservable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.choosemuse.libmuse.MuseDataPacketType;

import java.util.HashSet;
import java.util.Set;

import eeg.useit.today.eegtoolkit.common.FrequencyBands;
import eeg.useit.today.eegtoolkit.io.StreamingDeviceRecorder;
import eeg.useit.today.eegtoolkit.vm.StreamingDeviceViewModel;

/**
 * ViewModel for all the data displayed by the recording activity.
 * Wraps a selected device, and options for what to record.
 */
public class RecordingViewModel extends BaseObservable {
  /** ViewModel for single device we're using. */
  public StreamingDeviceViewModel device = new StreamingDeviceViewModel();

  // Which channels to record:
  private boolean rawSelected;
  private boolean connectionSelected;
  private Set<FrequencyBands.Band> bandsSelected = new HashSet<>();

  /** @return A new recorder for the device, based on what is selected. */
  public StreamingDeviceRecorder createRecorder(Context ctx) {
    Set<MuseDataPacketType> types = new HashSet<>();
    if (isRawSelected()) {
      types.add(MuseDataPacketType.EEG);
    }
    if (isConnectionSelected()) {
      types.add(MuseDataPacketType.HSI_PRECISION);
    }
    if (isAlphaSelected()) {
      types.add(MuseDataPacketType.ALPHA_RELATIVE);
    }
    Log.i("MINT", "Recording " + types.size() + " channels");
    return new StreamingDeviceRecorder(ctx, "data", device, types);
  }

  // GETTERS

  public StreamingDeviceViewModel getDevice() {
    return device;
  }

  public boolean isRawSelected() {
    return rawSelected;
  }

  public boolean isConnectionSelected() {
    return connectionSelected;
  }

  public boolean isAlphaSelected() {
    return isFrequencySelected(FrequencyBands.Band.ALPHA);
  }

  // Utility to reuse for all the frequencies.
  private boolean isFrequencySelected(FrequencyBands.Band band) {
    return bandsSelected.contains(band);
  }

  // SETTERS

  public void onClickRaw(View view) {
    boolean newValue = ((CheckBox)view).isChecked();
    rawSelected = newValue;
    notifyChange();
  }

  public void onClickConnection(View view) {
    boolean newValue = ((CheckBox)view).isChecked();
    connectionSelected = newValue;
    notifyChange();
  }

  public void onClickAlpha(View view) {
    setFrequencySelected(FrequencyBands.Band.ALPHA, ((CheckBox)view).isChecked());
  }

  // Utility to reuse for all the frequencies.
  private void setFrequencySelected(FrequencyBands.Band band, boolean newValue) {
    if (newValue) {
      bandsSelected.add(band);
    } else {
      bandsSelected.remove(band);
    }
    notifyChange();
  }
}
