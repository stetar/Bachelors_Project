package com.example.srikate.ibeacondemo.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.example.srikate.ibeacondemo.R;

public class WebViewTestFragment extends Fragment{

    private WebView webview;
    private String url;
    public static WebViewTestFragment getInstance() {
        return new WebViewTestFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            url = getArguments().getString("url");
        } catch (NullPointerException e){
            Log.d("Bundle", "Bundle is null");
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }break;
            }
        }
    };

    //Loads the web view, either by a given URL or by the standard URL for our website.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.web_view, container, false);
        webview = (WebView) v.findViewById(R.id.tabletUIwebview);
        webview.setWebViewClient(new WebViewClient());

        if (url != null){
            webview.loadUrl(url);
        } else {
            webview.loadUrl("https://bach2020.azurewebsites.net/");
        }

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webview.setOnKeyListener(new View.OnKeyListener(){

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webview.canGoBack()) {

                    handler.sendEmptyMessage(1);
                    return true;
                }

                return false;
            }

        });

        return v;
    }


    private void webViewGoBack(){
        webview.goBack();
    }




}
