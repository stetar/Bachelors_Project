package com.example.srikate.ibeacondemo.fragments;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.IpSecManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.srikate.ibeacondemo.MainActivity;
import com.example.srikate.ibeacondemo.R;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.w3c.dom.Text;

import java.io.Console;
import java.io.IOException;

public class QRFragment extends Fragment {

    private SurfaceView surfaceView;
    private TextView textView;
    private CameraSource cameraSource;
    private BarcodeDetector barcodeDetector;
    private Barcode.UrlBookmark url;

    public static QRFragment newInstance() {
        return new QRFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //Setup of UI
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.qr_scanner, container, false);

        surfaceView = v.findViewById(R.id.camerapreview);
        textView = v.findViewById(R.id.textView);
        final Context c = getContext();
        barcodeDetector = new BarcodeDetector.Builder(c)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(c, barcodeDetector)
                .setRequestedPreviewSize(640,480).setAutoFocusEnabled(true).build();

        //Specific setup for the camera view
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                if(ContextCompat.checkSelfPermission(c, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    Log.d("PERMISSIONS", "Camera permission not granted");
                    requestPermissions(
                            new String[]{Manifest.permission.CAMERA},
                            4);
                }
                try{
                    cameraSource.start(surfaceHolder);

                    Log.d("camera", "Started the camera");
                } catch (IOException e){
                    Log.d("camera", "Caught exception when trying to start camera");
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
                Log.d("CAMERA", "Camera stopped");
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if(qrCodes.size() != 0){
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(qrCodes.valueAt(0).displayValue);

                            //If barcode is an url format. URL value format = 8
                            if(qrCodes.valueAt(0).valueFormat == 8){
                                url = qrCodes.valueAt(0).url;
                                Bundle bundle=new Bundle();
                                bundle.putString("url", url.url);
                                //set Fragmentclass Arguments
                                WebViewTestFragment fragobj = new WebViewTestFragment();
                                fragobj.setArguments(bundle);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();


                                fragmentManager.beginTransaction().replace(R.id.contentFrame, fragobj).commit();
                            }
                        }
                    });
                }
            }
        });

        return v;
    }


}