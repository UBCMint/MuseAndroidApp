package ca.ubc.best.mint.museandroidapp;

import android.util.Log;

import com.choosemuse.libmuse.Muse;

import eeg.useit.today.eegtoolkit.vm.MuseListViewModel;
import eeg.useit.today.eegtoolkit.vm.MuseListViewModel.MuseListListener;

/** Collection of common logic that doesn't have a home to live in. */
public class Util {
  /** @return A Muse matching the address, or null if none can be found. */
  public static void getByMacAddress(
      final String macAddress, int scanSec, final MuseListListener listener
  ) {
    final MuseListViewModel list = new MuseListViewModel();
    Log.i("MINT", "Scanning...");
    list.setListener(new MuseListListener() {
      @Override
      public void onScanForDevicesFinished() {
        Muse connected = null;
        for (Muse muse : list.getDevices()) {
          if (muse.getMacAddress().equals(macAddress)) {
            connected = muse;
          }
        }
        listener.onDeviceSelected(connected);
      }

      @Override public void onDeviceSelected(Muse muse) {}
    });
    list.scan(scanSec);
  }
}
