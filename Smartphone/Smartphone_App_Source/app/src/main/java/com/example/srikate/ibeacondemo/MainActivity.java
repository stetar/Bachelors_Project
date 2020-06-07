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
import android.content.DialogInterface;
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

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.srikate.ibeacondemo.fragments.QRFragment;
import com.example.srikate.ibeacondemo.fragments.WebViewTestFragment;
import com.example.srikate.ibeacondemo.model.BeaconDeviceModel;
import com.example.srikate.ibeacondemo.utils.UiHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TargetApi(21)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final int ENABLE_BLUETOOTH_REQUEST_CODE = 1;
    private String TAG = "Bluetooth scanner:";
    private BluetoothLeScanner mLEScanner;
    private BluetoothManager btManager;
    private BluetoothAdapter btAdapter;
    private ScanSettings settings;
    private ArrayList<ScanFilter> filters;
    private final ArrayList<BeaconDeviceModel> beaconList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Surrounding UI setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.i("TimeAttendantFast", "Main Activity Create");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Bluetooth setup
        settingBlueTooth();

        //Setting up sidebar
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Setting up for starting fragment
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

    //Changing between the fragments through the Navigation Menu
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

    //Bluetooth activation pop-up result handler
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ENABLE_BLUETOOTH_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                settingBlueTooth();
                startScan();
            }
        }
        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
    }

    //Initiates Bluetooth and BLE, if available.
    private void settingBlueTooth() {
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

    //Stops scanning for beacons
    public void stopScan(){
        if (Build.VERSION.SDK_INT < 21) {
            Log.i(TAG, "runnable stop SDK_INT < 21");
            btAdapter.stopLeScan(leScanCallback);
        } else {
            Log.i(TAG, "runnable stop SDK_INT >= 21");
            mLEScanner.stopScan(mScanCallback);
        }
    }

    //Start scanning for beacons
    public void startScan(){
        if (btAdapter != null && btAdapter.isEnabled()){
            Log.i(TAG, "BLE start scan");
            if (Build.VERSION.SDK_INT < 21) {
                Log.i(TAG, "start SDK_INT < 21");
                btAdapter.startLeScan(leScanCallback);
            } else {
                Log.i(TAG, "start SDK_INT >= 21");
                mLEScanner.startScan(filters, settings, mScanCallback);
            }
        }
        else{ //Asks for permission to turn on Bluetooth on the device, if it isn't on
            UiHelper.showInformationMessage(this, "Enable Bluetooth", "Please enable bluetooth to start scanning for beacons.",
                    false, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (i == DialogInterface.BUTTON_POSITIVE) {
                                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableIntent, ENABLE_BLUETOOTH_REQUEST_CODE);
                            }
                        }
                    });
        }

    }

    // BLE callback handler
    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            findBeaconPattern(scanRecord, rssi);
        }
    };

    // Bluetooth callback handler
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
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

            // Retrieves the major
            final int major = (scanRecord[startByte + 20] & 0xff) * 0x100 + (scanRecord[startByte + 21] & 0xff);

            // Retrieves the minor
            final int minor = (scanRecord[startByte + 22] & 0xff) * 0x100 + (scanRecord[startByte + 23] & 0xff);

            foundBeacon(uuid, major, minor, strength);
        }
    }

    //Action to perform once beacon have been identified
    private void foundBeacon(String uuid, final int major, final int minor, final int strength) {
        if(!checkUnique(major, minor)){
            updateBeacon(major, minor, strength);
            updateBeaconList();
        }
        else{
            saveToList(major, minor, strength);
            updateBeaconList();
        }
    }

    //Updates an existing beacon object with new signal information
    private void updateBeacon(int major, int minor, int signal){
        for (BeaconDeviceModel b: beaconList){
            if(b.getMajor() == major && b.getMinor() == minor){
                b.addToMeasures(signal);
                b.setSignal(signal);
            }
        }
    }

    //Creates a new beacon object and saves it to a list
    private void saveToList(int major, int minor, int signal) {
        int position = beaconList.size();
        BeaconDeviceModel temp = new BeaconDeviceModel(major, minor, signal);
        beaconList.add(position, temp);
    }

    //Checks if the given major and minor is unique in the list
    private boolean checkUnique(int major, int minor){
        for (BeaconDeviceModel b: beaconList) {
            if(b.getMajor() == major && b.getMinor() == minor){
                return false;
            }
        }
        return true;
    }

    //Sorts the list descending, which puts the beacon with the best signal on top
    private void updateBeaconList(){
        Collections.sort(beaconList);
    }

    //Returns the best beacon based on what function is used
    public String retrieveBestBeacon(){
        BeaconDeviceModel bestBeacon = bestSignal();
        return String.format(bestBeacon.getMajor() + "," + bestBeacon.getMinor());
    }

    //Returns the beacon with the highest RSSI value
    private BeaconDeviceModel bestSignal(){
        Collections.sort(beaconList);
        for (BeaconDeviceModel b : beaconList){
            b.getMeasures().clear();
        }
        return beaconList.get(0);
    }

    //Returns the beacon with the best Average and best Mode
    private BeaconDeviceModel bestAverageAndMode(){
        int bestAverage = Integer.MIN_VALUE;
        int average = 0;
        int bestMode = Integer.MIN_VALUE;
        boolean modeCheck = false;
        boolean averageCheck = false;
        BeaconDeviceModel bestBeacon = new BeaconDeviceModel(0,0,0);
        for (BeaconDeviceModel b : beaconList){
            if (b.getMeasures().size() > 0) {
                int mode = mostCommon(b.getMeasures());
                if (mode > bestMode){
                    bestMode = mode;
                    modeCheck = true;
                }
                for (Integer i : b.getMeasures()) {
                    average = average + i;
                }
                average = average / b.getMeasures().size();
                b.getMeasures().clear();
                if (average > bestAverage) {
                    bestAverage = average;
                    averageCheck = true;
                }

                if(modeCheck && averageCheck){
                    bestBeacon = b;
                }
            }
        }
        return bestBeacon;
    }

    //Returns the beacon with the best average
    private BeaconDeviceModel byBestAverageWithClear(){
        int bestAverage = Integer.MIN_VALUE;
        int average = 0;
        BeaconDeviceModel bestBeacon = new BeaconDeviceModel(0,0,0);
        for (BeaconDeviceModel b : beaconList){
            if (b.getMeasures().size() > 0) {
                for (Integer i : b.getMeasures()) {
                    average = average + i;
                }
                average = average / b.getMeasures().size();
                b.getMeasures().clear();
                if (average > bestAverage) {
                    bestAverage = average;
                    bestBeacon = b;
                }
            }
        }
        return bestBeacon;
    }

    //Returns the beacon with the least change in average between readings
    private BeaconDeviceModel LeastAverageChange(){
        int leastChange = Integer.MAX_VALUE;
        int average = 0;
        BeaconDeviceModel bestBeacon = new BeaconDeviceModel(0,0,0);
        for (BeaconDeviceModel b : beaconList){
            if (b.getMeasures().size() > 0) {
                for (Integer i : b.getMeasures()) {
                    average = average + i;
                }
                average = average / b.getMeasures().size();
                b.getMeasures().clear();

                int tempResult = b.getAverage() - average;
                b.setAverage(average);

                if (tempResult < leastChange){
                    leastChange = tempResult;
                    bestBeacon = b;
                }
            }
        }
        return bestBeacon;
    }

    //Returns the beacon with the best mode
    private BeaconDeviceModel byBestModeWithClear(){
        int bestMode = Integer.MIN_VALUE;
        BeaconDeviceModel bestBeacon = new BeaconDeviceModel(0,0,0);
        for (BeaconDeviceModel b : beaconList){
            if (b.getMeasures().size() > 0){
                int mode = mostCommon(b.getMeasures());
                b.getMeasures().clear();
                if (mode > bestMode){
                    bestMode = mode;
                    bestBeacon = b;
                }
            }
        }
        return bestBeacon;
    }

    //https://stackoverflow.com/questions/19031213/java-get-most-common-element-in-a-list
    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
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
