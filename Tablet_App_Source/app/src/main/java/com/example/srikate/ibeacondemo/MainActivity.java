package com.example.srikate.ibeacondemo;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.srikate.ibeacondemo.fragments.BeaconSimulatorFragment;
import com.example.srikate.ibeacondemo.fragments.WebViewTestFragment;
import com.example.srikate.ibeacondemo.utils.UiHelper;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Bluetooth stuff:";
    private Beacon beacon;
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private BeaconTransmitter beaconTransmitter;
    private Fragment fragment;
    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment = BeaconSimulatorFragment.newInstance();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawer_layout, fragment).commit();

        setupBTAdapter();

    }

    //Switches to the web view fragment
    public void swapToWebview(){
        fragment = WebViewTestFragment.getInstance();
        fragmentManager.beginTransaction().replace(R.id.drawer_layout, fragment).commit();
    }

    //Gains access to Bluetooth functionality, with prompt for user to turn on Bluetooth
    private void setupBTAdapter(){
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();

        if (getBlueToothOn()) {
            beaconTransmitter = new BeaconTransmitter (this, new BeaconParser()
                    .setBeaconLayout ("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        } else if (!isBluetoothLEAvailable()) {
            UiHelper.showErrorMessage(this, "Bluetooth not available on your device");
        } else {
            UiHelper.showInformationMessage(this, "Enable Bluetooth", "Please enable bluetooth before transmit iBeacon.",
                    false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == DialogInterface.BUTTON_POSITIVE) {
                                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableIntent, 1);
                            }
                        }
                    });
        }
    }

    //Starts the advertising of the given beacon
    public void StartBeacon(Beacon newBeacon) {
        beacon = newBeacon;
        transmitIBeacon();
    }

    //Advertising of the beacon
    private void transmitIBeacon() {
        boolean isSupported = false;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            isSupported = btAdapter.isMultipleAdvertisementSupported();
            if (isSupported) {
                beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback(){

                    @Override
                    public void onStartFailure(int errorCode) {
                        Log.e(TAG, "Advertisement start failed with code: " + errorCode);
                    }

                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                        Log.i(TAG, "Advertisement start succeeded." + settingsInEffect.toString());
                    }
                });

            } else {
                UiHelper.showErrorMessage(this, "Your device is not support leBluetooth.");
            }
        } else {
            UiHelper.showErrorMessage(this, "Your device is not support leBluetooth.");
        }
        swapToWebview();
    }

    //Checks for BLE
    private boolean isBluetoothLEAvailable() {return btAdapter != null && getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);}

    //Checks for regular Bluetooth
    private boolean getBlueToothOn() {return btAdapter != null && btAdapter.isEnabled();}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            beaconTransmitter = new BeaconTransmitter (this, new BeaconParser()
                    .setBeaconLayout ("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        } else {
            Log.e(TAG, "result not ok");
        }
    }
}
