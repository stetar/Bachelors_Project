package com.example.srikate.ibeacondemo.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.example.srikate.ibeacondemo.MainActivity;

import java.io.Console;

public class JavaScriptInterface {
    Context mContext;
    Vibrator vibrator;
    public JavaScriptInterface(Context c){
        mContext = c;
        vibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @JavascriptInterface
    public void showToast(String text){
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void startScanning(){
        ((MainActivity)mContext).startScan();
    }

    @JavascriptInterface
    public void stopScanning(){
        ((MainActivity)mContext).stopScan();
    }

    @JavascriptInterface
    public String retrieveBestBeacon(){
        return ((MainActivity)mContext).retrieveBestBeacon();
    }

    @JavascriptInterface
    public void vibrate(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(500);
        }
    }
}