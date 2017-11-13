package ca.ubc.best.mint.museandroidapp.vm;

import android.databinding.BaseObservable;

import com.choosemuse.libmuse.Muse;

import eeg.useit.today.eegtoolkit.vm.MuseListViewModel;
import eeg.useit.today.eegtoolkit.vm.MuseListViewModel.MuseListListener;
import eeg.useit.today.eegtoolkit.vm.StreamingDeviceViewModel;

/**
 * ViewModel for all the data displayed by the recording activity.
 * Wraps a selected device, and options for what to record.
 */
public class InitialViewModel extends BaseObservable {
  /** ViewModel for single device we're using. */
  private final StreamingDeviceViewModel device = new StreamingDeviceViewModel();

  /** Object performing the device scan, or null if no scan is taking place. */
  private MuseListViewModel listViewModel = null;

  /** Scan for devices for some time, call onDeviceSelected when done. */
  public void scanForDevice(int scanLengthSec, final MuseListListener listener) {
    assert this.canStartScan();
    listViewModel = new MuseListViewModel();
    listViewModel.setListener(new MuseListListener() {
      @Override public void onScanForDevicesFinished() {
        if (listViewModel.getDevices().isEmpty()) {
          // No devices found :(
          listener.onDeviceSelected(null);
        } else {
          // Default to first device, for convenience.
          Muse muse = listViewModel.getDevices().get(0);
          listener.onDeviceSelected(muse);
        }
        listViewModel = null;
        notifyChange();
      }
      @Override public void onDeviceSelected(Muse muse) { /* ignore */ }
    });
    listViewModel.scan(scanLengthSec);
    notifyChange();
  }

  // GETTERS

  /** @return Whether the streaming device is attached to a muse. */
  public boolean hasDevice() {
    // TODO: Add this to StreamingDeviceViewModel, rather than using mac address.
    return device.getMacAddress() != null;
  }

  // You can only start scanning if there's no devices and we're not already scanning. */
  public boolean canStartScan() {
    boolean isScanning = listViewModel != null;
    return !hasDevice() && !isScanning;
  }
}
