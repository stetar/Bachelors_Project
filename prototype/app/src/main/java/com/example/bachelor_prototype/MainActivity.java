package com.example.bachelor_prototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView networkName = findViewById(R.id.networkNameText);


        WifiManager wifiMan = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 4;
        WifiInfo wifiInfo = wifiMan.getConnectionInfo();
        networkName.setText("Current connection level: " + WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 4));


        /*ConnectivityManager connMan = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connMan.getActiveNetworkInfo();
        networkName.setText("This is the type of network: " + info.getTypeName());*/
    }
}
