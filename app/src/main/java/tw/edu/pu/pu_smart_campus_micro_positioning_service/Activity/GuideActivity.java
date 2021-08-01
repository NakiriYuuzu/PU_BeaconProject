package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class GuideActivity extends AppCompatActivity implements BeaconConsumer {

    private final String TAG = "SafetyActivity: ";
    private final String UNIQUE_ID = "594650a2-8621-401f-b5de-6eb3ee398170";

    private final float DISTANCE_THRESHOLD = 2.5f;

    private BeaconManager beaconManager;

    private MaterialButton btnStart, btnStop;
    private MaterialTextView tvShowDisplay;

    private boolean beaconIsRunning = false;

    private ArrayList<HashMap<String, String>> beaconMap = new ArrayList<>();
    private HashMap<String, Integer> regionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        Init();
        buttonInit();
    }

    private void beaconInit() {
            beaconManager = BeaconManager.getInstanceForApplication(this);

            //beacon AddStone m:0-3=4c000215 or alt beacon = m:2-3=0215
            beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
            beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

            beaconIsRunning = true;

            beaconManager.bind(this);
    }

    private void buttonInit() {
        btnStart.setOnClickListener(v -> {
            beaconInit();
        });

        btnStop.setOnClickListener(v -> {
            if (beaconIsRunning) {
                beaconManager.unbind(this);
                beaconManager.removeAllRangeNotifiers();
                beaconIsRunning = false;
            }
        });
    }

    private void Init() {
        //findView
        tvShowDisplay = findViewById(R.id.tv_Guide_information);
        btnStart = findViewById(R.id.btn_Guide_Start);
        btnStop = findViewById(R.id.btn_Guide_Stop);

    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @SuppressLint("SetTextI18n")
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for (Beacon beacon: beacons) {
                        String distance = String.valueOf(beacons.iterator().next().getDistance());
                        String uniqueID = String.valueOf(beacons.iterator().next().getId1());
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
                    }
                }
                else {
                    tvShowDisplay.setText("Nothing...");
                    Log.e(TAG, String.valueOf(beacons.size()));
                }
            }
        });

        try {
            beaconManager.startRangingBeacons(new Region(BeaconDefine.POINT_01, Identifier.parse(UNIQUE_ID), null, null));
            regionMap.put(BeaconDefine.POINT_01, 101);

            beaconManager.startRangingBeacons(new Region(BeaconDefine.POINT_02, Identifier.parse(UNIQUE_ID), null, null));
            regionMap.put(BeaconDefine.POINT_02, 102);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beaconIsRunning) {
            beaconManager.unbind(this);
            beaconManager.removeAllRangeNotifiers();

            beaconIsRunning = false;
        }
    }
}