package com.example.srikate.ibeacondemo;


import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.srikate.ibeacondemo.fragments.QRFragment;
import com.example.srikate.ibeacondemo.fragments.WebViewTestFragment;
import com.example.srikate.ibeacondemo.model.BeaconDeviceModel;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TargetApi(21)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private String TAG = "Bluetooth scanner:";
    private BluetoothLeScanner mLEScanner;
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private Handler scanHandler;
    private Handler mHandler;
    private ScanSettings settings;
    private ArrayList<ScanFilter> filters;
    private final ArrayList<BeaconDeviceModel> beaconList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i("TimeAttendantFast", "Main Activity Create");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        settingBlueTooth();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        QRFragment fragment = QRFragment.newInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentFrame, fragment, "FINDER").disallowAddToBackStack().commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                Toast.makeText(this, "You pressed settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_list_size:
                Fragment tempFrag = getSupportFragmentManager().findFragmentByTag("FINDER");
                if (tempFrag instanceof BeaconScannerFragment){
                    Toast.makeText(this, "Current list size: " + ((BeaconScannerFragment) tempFrag).listAccess().getItemCount(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        switch (id){
            case R.id.qr_scanner:
                fragment = QRFragment.newInstance();
                break;
            case R.id.web_view_test:
                fragment = WebViewTestFragment.newInstance();
                break;

        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.contentFrame, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
    }


    private void settingBlueTooth() {
        // init BLE
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();

        if (Build.VERSION.SDK_INT >= 21) {
            mLEScanner = btAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<>();
        }
    }

    public void stopScan(){
        if (Build.VERSION.SDK_INT < 21) {
            Log.i(TAG, "runnable stop SDK_INT < 21");
            btAdapter.stopLeScan(leScanCallback);
        } else {
            Log.i(TAG, "runnable stop SDK_INT >= 21");
            mLEScanner.stopScan(mScanCallback);
        }
    }

    public void startScan(){
        Log.i(TAG, "BLE start scan");
        if (Build.VERSION.SDK_INT < 21) {
            Log.i(TAG, "start SDK_INT < 21");
            btAdapter.startLeScan(leScanCallback);
        } else {
            Log.i(TAG, "start SDK_INT >= 21");
            mLEScanner.startScan(filters, settings, mScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            findBeaconPattern(scanRecord, rssi);
        }
    };

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i(TAG, "callbackType " + callbackType);
            byte[] scanRecord = result.getScanRecord().getBytes();
            findBeaconPattern(scanRecord, result.getRssi());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                Log.i(TAG, "ScanResult - Results" + sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(TAG, "Scan Failed Error Code: " + errorCode);
        }
    };

    // Identify iBeacons by pattern (unchanged from original project)
    private void findBeaconPattern(byte[] scanRecord, int strength) {
        int startByte = 2;
        boolean patternFound = false;
        while (startByte <= 5) {
            if (((int) scanRecord[startByte + 2] & 0xff) == 0x02 && //Identifies an iBeacon
                    ((int) scanRecord[startByte + 3] & 0xff) == 0x15) { //Identifies correct data length
                patternFound = true;
                break;
            }
            startByte++;
        }

        if (patternFound) {
            //Convert to hex String
            byte[] uuidBytes = new byte[16];
            System.arraycopy(scanRecord, startByte + 4, uuidBytes, 0, 16);
            String hexString = bytesToHex(uuidBytes);

            //UUID detection
            String uuid = hexString.substring(0, 8) + "-" +
                    hexString.substring(8, 12) + "-" +
                    hexString.substring(12, 16) + "-" +
                    hexString.substring(16, 20) + "-" +
                    hexString.substring(20, 32);

            // major
            final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

            // minor
            final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

            Log.i(TAG, "UUID: " + uuid + "\\nmajor: " + major + "\\nminor" + minor);
            foundBeacon(uuid, major, minor, strength);
        }
    }

    //Action to perform once beacon have been identified
    private void foundBeacon(String uuid, final int major, final int minor, final int strength) {
        Log.d(TAG, "foundBeacon: Beacon (" + major + " , " + minor + ") located");

        if(!checkUnique(major, minor)){
            updateBeacon(major, minor, strength);
            updateBeaconList();
        }
        else{
            saveToList(major, minor, strength);
            updateBeaconList();
        }
    }

    private void updateBeacon(int major, int minor, int signal){
        for (BeaconDeviceModel b: beaconList){
            if(b.getMajor() == major && b.getMinor() == minor){
                b.addToMeasures(signal);
            }
        }
    }

    private void saveToList(int major, int minor, int signal) {
        int position = beaconList.size();
        BeaconDeviceModel temp = new BeaconDeviceModel(major, minor, signal);
        beaconList.add(position, temp);
    }

    private boolean checkUnique(int major, int minor){
        for (BeaconDeviceModel b: beaconList) {
            if(b.getMajor() == major && b.getMinor() == minor){
                return false;
            }
        }
        return true;
    }

    private void updateBeaconList(){
        Collections.sort(beaconList);
    }

    public String retrieveBestBeacon(){
        BeaconDeviceModel temp = beaconList.get(0);
        return String.format(temp.getMajor() + "," + temp.getMinor());
    }

    // Helper method
    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
