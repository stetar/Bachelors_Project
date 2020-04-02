package com.example.srikate.ibeacondemo.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.srikate.ibeacondemo.R;

/**
 * Created by srikate on 10/4/2017 AD.
 * Source : https://github.com/inthepocket/ibeacon-scanner-android
 */

public class WebViewTestFragment extends Fragment{


    public static WebViewTestFragment newInstance() {
        return new WebViewTestFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.time_atten_fragment, container, false);


        return v;
    }

}
