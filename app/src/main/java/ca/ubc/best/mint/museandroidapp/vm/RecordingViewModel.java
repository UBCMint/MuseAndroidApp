package ca.ubc.best.mint.museandroidapp.vm;

import android.content.Context;
import android.databinding.BaseObservable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseDataPacketType;

import java.util.HashSet;
import java.util.Set;

import eeg.useit.today.eegtoolkit.io.StreamingDeviceRecorder;
import eeg.useit.today.eegtoolkit.vm.StreamingDeviceViewModel;

/**
 * ViewModel for all the data displayed by the recording activity.
 * Wraps a selected device, and options for what to record.
 */
public class RecordingViewModel extends BaseObservable {
  /** ViewModel for single device we're using. */
  private final StreamingDeviceViewModel device = new StreamingDeviceViewModel();

  /** Which channels to record: */
  private final Set<MuseDataPacketType> channelsSelected = new HashSet<>();

  /** Object performing the recording, or null if no recording is taking place. */
  private StreamingDeviceRecorder recorder = null;

  /** @return A new recorder for the device, based on what is selected. */
  public StreamingDeviceRecorder startRecording(Context ctx) {
    assert canRecord(); // Make sure you can record first...
    Log.i("MINT", "Recording " + channelsSelected.size() + " channels");
    recorder = new StreamingDeviceRecorder(ctx, "data", device, channelsSelected);
    recorder.start();
    notifyChange();
    return recorder;
  }

  /** Stop recording, and return the path it was written to. */
  public String stopRecordingAndSave() {
    String path = recorder.stopAndSave();
    recorder = null;
    notifyChange();
    return path;
  }

  // GETTERS

  // You can start a scan if there's no device, i.e. if the device address is null. */
  public boolean canScanStart() {
    return device.getMacAddress() == null;
  }

  // You can only record if there's a device to measure, and at least one selected channel. */
  public boolean canRecord() {
    return device.getMacAddress() != null && !channelsSelected.isEmpty() && recorder == null;
  }

  public boolean canStopRecording() {
    return recorder != null && recorder.isRunning();
  }

  public boolean isRawSelected() {
    return channelsSelected.contains(MuseDataPacketType.EEG);
  }

  public boolean isConnectionSelected() {
    return channelsSelected.contains(MuseDataPacketType.HSI_PRECISION);
  }

  public boolean isAlphaSelected() {
    return channelsSelected.contains(MuseDataPacketType.ALPHA_RELATIVE);
  }

  // SETTERS

  public void attachMuse(Muse muse) {
    this.device.setMuse(muse);
    notifyChange();
  }

  public void onClickRaw(View view) {
    updateFromView((CheckBox) view, MuseDataPacketType.EEG);
  }

  public void onClickConnection(View view) {
    updateFromView((CheckBox) view, MuseDataPacketType.HSI_PRECISION);
  }

  public void onClickAlpha(View view) {
    updateFromView((CheckBox) view, MuseDataPacketType.ALPHA_RELATIVE);
  }

  // Utility to do all the common updates from a checkbox:
  private void updateFromView(CheckBox view, MuseDataPacketType type) {
    if (view.isChecked()) {
      channelsSelected.add(type);
    } else {
      channelsSelected.remove(type);
    }
    notifyChange();
  }
}
