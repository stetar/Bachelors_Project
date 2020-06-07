package com.example.srikate.ibeacondemo.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import com.example.srikate.ibeacondemo.MainActivity;
import com.example.srikate.ibeacondemo.R;
import org.altbeacon.beacon.Beacon;
import java.util.Arrays;


public class BeaconSimulatorFragment extends Fragment implements View.OnClickListener {
    private ImageView beaconIv;
    private static String TAG = "BeaconSimulator";
    private EditText minorSelect, majorSelect, signalSelect;

    public static BeaconSimulatorFragment newInstance() {
        return new BeaconSimulatorFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Creates the advertising beacon from the Builder pattern in the API
    private Beacon setupBeacon() {
              return new Beacon.Builder()
                .setBluetoothName("TEST_BEACON")
                .setId1(getString(R.string.beacon_uuid_simulator)) // UUID for beacon
                .setId2(majorSelect.getText().toString()) // Major for beacon
                .setId3(minorSelect.getText().toString()) // Minor for beacon
                .setManufacturer(0x004C) // Radius Networks.0x0118  Change this for other beacon layouts//0x004C for iPhone
                .setTxPower(-Integer.valueOf(signalSelect.getText().toString())) // Power in dB
                .setDataFields(Arrays.asList(new Long[]{0l})) // Remove this for beacon layouts without d: fields
                .build();
    }

    //Accessing the UI.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.beacon_simu_fragment, container, false);
        minorSelect = v.findViewById(R.id.beaconMinorSelect);
        majorSelect = v.findViewById(R.id.beaconMajorSelect);
        signalSelect = v.findViewById(R.id.beaconSignalSelect);
        beaconIv = v.findViewById(R.id.beaconIV);
        beaconIv.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.beaconIV) {
            onBeaconClicked();
        }
    }

    //Calls the starting method on the MainActivity with the newly created beacon.
    private void onBeaconClicked() {
        ((MainActivity)getActivity()).StartBeacon(setupBeacon());

    }

}
