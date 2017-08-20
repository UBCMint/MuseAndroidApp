package ca.ubc.best.mint.museandroidapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseManagerAndroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ubc.best.mint.museandroidapp.databinding.ActivityRecordingBinding;
import ca.ubc.best.mint.museandroidapp.vm.RecordingViewModel;
import eeg.useit.today.eegtoolkit.vm.MuseListViewModel;

public class RecordingActivity extends AppCompatActivity {
  final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

  /** ViewModel for this activity. */
  public final RecordingViewModel recordingViewModel = new RecordingViewModel();

  /** ViewModel for muse devices found during scan. */
  public final MuseListViewModel listViewModel = new MuseListViewModel();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MuseManagerAndroid.getInstance().setContext(RecordingActivity.this);

    ActivityRecordingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_recording);
    binding.setActivity(this);
    binding.setRecordVM(recordingViewModel);
    getSupportActionBar().setTitle("Record EEG");

    askPermissions();

    configureScanButton();
  }

  // Set up what to do when the scan button is pressed: Scan, then connect to the muse found.
  private void configureScanButton() {
    final Button scanButton = (Button) findViewById(R.id.scanButton);
    scanButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        scanButton.setText("Scanning...");
        scanButton.setEnabled(false);
        Log.i("App", "Starting recording activity, listening to muse...");
        listViewModel.setListener(new MuseListViewModel.MuseListListener() {
          @Override
          public void onScanForDevicesFinished() {
            if (listViewModel.getDevices().isEmpty()) {
              scanButton.setEnabled(true);
              scanButton.setText("Scan");
              Toast.makeText(
                  RecordingActivity.this, "No devices found :(", Toast.LENGTH_LONG
              ).show();
            } else {
              Muse muse = listViewModel.getDevices().get(0);
              RecordingActivity.this.recordingViewModel.attachMuse(muse);
              RecordingActivity.this.getSupportActionBar().setTitle("Device: " + muse.getName());
              Log.i("MINT", "Setting record section to visible...");
            }
          }
          @Override
          public void onDeviceSelected(Muse muse) {
            // Ignore, just chose the first.
          }
        });
        listViewModel.scan(5);
      }
    });
  }

  /** Handles the record button being clicked - either start or stop recording. */
  public void handleRecordClicked() {
    if (recordingViewModel.canRecord()) {
      // Start recording!
      Log.i("MINT", "Starting recording!");
      recordingViewModel.startRecording(RecordingActivity.this);
    } else if (recordingViewModel.canStopRecording()) {
      // Stop recording!
      Log.i("MINT", "Stop recording!");
      String path = recordingViewModel.stopRecordingAndSave();
      // TODO: Enable sharing the file that was just recorded. Currently just flash message:
      Toast.makeText(RecordingActivity.this, "Recorded! To: " + path, Toast.LENGTH_LONG).show();
    }
  }


  // HACK - permissions:
  //
  // Permissions code below. See
  // https://stackoverflow.com/questions/32708374/bluetooth-le-scanfilters-dont-work-on-android-m
  //

  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    switch (requestCode) {
      case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
        Map<String, Integer> perms = new HashMap<>();
        perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
        for (int i = 0; i < permissions.length; i++) {
          perms.put(permissions[i], grantResults[i]);
        }

        if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
          Toast.makeText(
              RecordingActivity.this,
              "All Permission GRANTED !! Thank You :)",
              Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(
              RecordingActivity.this,
              "One or More Permissions are DENIED Exiting App :(",
              Toast.LENGTH_SHORT).show();
          finish();
        }
        break;
      default:
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  @TargetApi(Build.VERSION_CODES.M)
  private void askPermissions() {
    List<String> permissionsNeeded = new ArrayList<>();
    final List<String> permissionsList = new ArrayList<>();
    if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
      permissionsNeeded.add("Show Location");
    }
    if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      permissionsNeeded.add("Write to file");
    }

    if (permissionsList.size() > 0) {
      if (permissionsNeeded.size() > 0) {
        String message = "App need access to " + permissionsNeeded.get(0);
        for (int i = 1; i < permissionsNeeded.size(); i++) {
          message = message + ", " + permissionsNeeded.get(i);
        }
        showMessageOKCancel(message, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            requestPermissions(
                permissionsList.toArray(new String[permissionsList.size()]),
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
            );
          }
        });
        return;
      }
      requestPermissions(
          permissionsList.toArray(new String[permissionsList.size()]),
          REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
      );
      return;
    }
  }

  private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
    new AlertDialog.Builder(RecordingActivity.this)
        .setMessage(message)
        .setPositiveButton("OK", okListener)
        .setNegativeButton("Cancel", null)
        .create()
        .show();
  }

  @TargetApi(Build.VERSION_CODES.M)
  private boolean addPermission(List<String> permissionsList, String permission) {
    if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
      permissionsList.add(permission);
      // Check for Rationale Option
      if (!shouldShowRequestPermissionRationale(permission)) {
        return false;
      }
    }
    return true;
  }
}
