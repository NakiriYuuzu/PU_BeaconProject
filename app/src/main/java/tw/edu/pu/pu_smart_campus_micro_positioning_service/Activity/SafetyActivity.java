package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.List;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class SafetyActivity extends AppCompatActivity implements BeaconConsumer {

    private final String TAG = "SafetyActivity: ";
    private final String myUniqueID = "594650a2-8621-401f-b5de-6eb3ee398170";

    private Beacon beacon;
    private BeaconManager beaconManager;
    private BeaconParser beaconParser;
    private BeaconTransmitter beaconTransmitter;

    private MaterialButton btnStart, btnStop;
    private MaterialTextView tvShowDisplay;

    private String uniqueID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        findView();
        buttonInit();
    }

    private void beaconInit() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        //beacon AddStone m:0-3=4c000215 or alt beacon = m:2-3=0215
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
    }

    private void buttonInit() {
        btnStart.setOnClickListener(v -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    beaconInit();
                }
            }).start();
        });

        btnStop.setOnClickListener(v -> {
            beaconManager.unbind(this);
        });
    }

    private void findView() {
        tvShowDisplay = findViewById(R.id.showDisplay);
        btnStart = findViewById(R.id.btn_Receive_Start);
        btnStop = findViewById(R.id.btn_Receive_Stop);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @SuppressLint("SetTextI18n")
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    String distance = String.valueOf(beacons.iterator().next().getDistance());
                    uniqueID = String.valueOf(beacons.iterator().next().getId1());
                    String major = String.valueOf(beacons.iterator().next().getId2());
                    String minor = String.valueOf(beacons.iterator().next().getId3());
                    int RSSI = beacons.iterator().next().getRssi();
                    String address = beacons.iterator().next().getBluetoothAddress();
                    int txPower = beacons.iterator().next().getTxPower();
                    String btName = beacons.iterator().next().getBluetoothName();


                    @SuppressLint("DefaultLocale")
                    String str = String.format("Distance: %s%nUniqueID: %s%nMajor: %s%nMinor: %s%nRSSI: %d%nAddress: %s%nTxPower: %d%nBtName : %s%n",
                            distance, uniqueID, major, minor, RSSI, address, txPower, btName);

                    tvShowDisplay.setText(str);
                    Log.e(TAG, str);

//                    if (uniqueID.equals(myUniqueID)) {
//                        beaconManager.unbind(SafetyActivity.this);
//                        beaconManager.removeAllRangeNotifiers();
//                        new AlertDialog.Builder(SafetyActivity.this)
//                                .setTitle("Welcome To Providence University!")
//                                .setMessage("Location: Xijia Schultz Hall\nPlace: 1F\n")
//                                .setPositiveButton("Ok", null)
//                                .setNegativeButton("Cancel", null)
//                                .show();
//                    }
                }
                else {
                    tvShowDisplay.setText("Nothing...");
                    Log.e(TAG, String.valueOf(beacons.size()));
                }
            }
        });

        try {
            beaconManager.startRangingBeacons(new Region("", null, null, null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

}