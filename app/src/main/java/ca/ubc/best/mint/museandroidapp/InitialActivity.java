package ca.ubc.best.mint.museandroidapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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

import ca.ubc.best.mint.museandroidapp.databinding.ActivityInitialBinding;
import ca.ubc.best.mint.museandroidapp.vm.InitialViewModel;
import eeg.useit.today.eegtoolkit.vm.MuseListViewModel;

public class InitialActivity extends AppCompatActivity {
  private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
  private static final int REQUEST_CODE_ENABLE_BT = 1;
  private static final int SCAN_LENGTH_SEC = 5;

  //Read Me button
    private Button readMeBtn;

  /** ViewModel for this activity. */
  public final InitialViewModel viewModel = new InitialViewModel();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MuseManagerAndroid.getInstance().setContext(InitialActivity.this);

    ActivityInitialBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_initial);
    binding.setActivity(this);
    binding.setViewModel(viewModel);
    getSupportActionBar().setTitle(R.string.app_name);

    maybeAskPermissions();
  }

    // Handles setting up the Read Me button
    public void readMeBtnSetUp(){
        readMeBtn = (Button) findViewById(R.id.readMeButton);
        readMeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReadMeActivity();
            }
        });
    }

    // Handles opening the Info Activity
    public void openReadMeActivity(){
        Intent intent = new Intent(this, readMeActivity.class);
        startActivity(intent);
    }


    /** Handles the scan button being clicked: starts the scan for muse devices. */
  public void handleScanClicked() {
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    if (mBluetoothAdapter == null) {
      Toast.makeText(this, "Bluetooth is not supported on this device :(", Toast.LENGTH_LONG).show();
    } else {
      // Only scan if bluetooth is enabled, otherwise turn on bluetooth first...
      if (mBluetoothAdapter.isEnabled()) {
        scanForDevices();
      } else {
        enableBluetooth();
      }
    }
  }

  /** Handles skip button clicked, debug shortcut to go to results screen with fake data. */
  public void viewHistoryClicked() {
    Intent intent = new Intent(InitialActivity.this, ResultListActivity.class);
    startActivity(intent);
  }


  private void scanForDevices() {
    viewModel.scanForDevice(SCAN_LENGTH_SEC, new MuseListViewModel.MuseListListener() {
      @Override
      public void onScanForDevicesFinished() { /* not called */ }

      @Override
      public void onDeviceSelected(Muse muse) {
        if (muse == null) {
          Toast.makeText(InitialActivity.this, "No devices found :(", Toast.LENGTH_LONG).show();
        } else {
          Log.i("MINT", "Device found: " + muse.getName());
          Intent intent = new Intent(InitialActivity.this, FlankerActivity.class);
          intent.putExtra("MAC", muse.getMacAddress());
          startActivity(intent);
        }
      }
    });
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
              InitialActivity.this,
              "All Permission GRANTED !! Thank You :)",
              Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(
              InitialActivity.this,
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
  private void maybeAskPermissions() {
    List<String> permissionsNeeded = new ArrayList<>();
    final List<String> permissionsList = new ArrayList<>();
    if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
      permissionsNeeded.add("Show Location");
    }
    if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
      permissionsNeeded.add("Write to file");
    }
    if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE)) {
      permissionsNeeded.add("Read file");
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
    new AlertDialog.Builder(InitialActivity.this)
        .setMessage(message)
        .setPositiveButton("OK", okListener)
        .setNegativeButton("Cancel", null)
        .create()
        .show();
  }

  @TargetApi(Build.VERSION_CODES.M)
  private boolean addPermission(List<String> permissionsList, String permission) {
    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
      permissionsList.add(permission);
      // Check for Rationale Option
      if (!shouldShowRequestPermissionRationale(permission)) {
        return false;
      }
    }
    return true;
  }

  /** Fire an intent which asks the user to turn on bluetooth. */
  private void enableBluetooth() {
    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BT);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // Enable-bluetooth intent has come back accepted, bluetooth is on, so start the scan.
    if (requestCode == REQUEST_CODE_ENABLE_BT && resultCode == RESULT_OK) {
      scanForDevices();
    }
  }
}
