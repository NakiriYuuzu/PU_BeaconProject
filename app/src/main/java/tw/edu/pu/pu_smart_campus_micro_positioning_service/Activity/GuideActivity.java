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
import java.util.Timer;
import java.util.TimerTask;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class GuideActivity extends AppCompatActivity implements BeaconConsumer {

    private final String TAG = "SafetyActivity: ";
    private final String UNIQUE_ID = "594650a2-8621-401f-b5de-6eb3ee398170";

    private static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 500L; // half sec
    private static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 500L; // half sec

    private final float DISTANCE_THRESHOLD = 3.5f;

    private boolean beaconIsRunning = false;

    private BeaconManager beaconManager;

    private MaterialButton btnStart, btnStop;
    private MaterialTextView tvShowDisplay;

    private ArrayList<HashMap<String, String>> beaconMap = new ArrayList<>();
    private HashMap<String, Integer> regionMap = new HashMap<>();
    private ArrayList<Beacon> beaconList = new ArrayList<>();

    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
        buttonInit();
    }

    private void beaconInit() {
        beaconManager = BeaconManager.getInstanceForApplication(this);

        //beacon AddStone m:0-3=4c000215 or alt beacon = m:2-3=0215
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.setForegroundBetweenScanPeriod(DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD);
        beaconManager.setForegroundScanPeriod(DEFAULT_FOREGROUND_SCAN_PERIOD);

        beaconIsRunning = true;

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
            if (beaconIsRunning) {
                beaconManager.unbind(this);
                beaconManager.removeAllRangeNotifiers();
                beaconIsRunning = false;
            }
        });
    }

    private void initView() {
        //findView
        tvShowDisplay = findViewById(R.id.tv_Guide_information);
        btnStart = findViewById(R.id.btn_Guide_Start);
        btnStop = findViewById(R.id.btn_Guide_Stop);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @SuppressLint("SetTextI18n")
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    for (Beacon beaconData : beacons) {
                        String distance = String.valueOf(beaconData.getDistance());
                        String uniqueID = String.valueOf(beaconData.getId1());
                        String major = String.valueOf(beaconData.getId2());
                        String minor = String.valueOf(beaconData.getId3());
                        String RSSI = String.valueOf(beaconData.getRssi());
                        String address = beaconData.getBluetoothAddress();
                        String txPower = String.valueOf(beaconData.getTxPower());

                        @SuppressLint("DefaultLocale")
                        String str = String.format("Distance: %s%nUniqueID: %s%nMajor: %s%nMinor: %s%nRSSI: %s%nAddress: %s%nTxPower: %s%n",
                                distance, uniqueID, major, minor, RSSI, address, txPower);

                        if (beaconData.getId1().toString().equalsIgnoreCase(UNIQUE_ID)) {
                            beaconList.add(beaconData);
                        }

                        tvShowDisplay.setText(str);

                        Log.e(TAG, str);
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeacons(new Region(BeaconDefine.POINT_01, null, Identifier.fromInt(87), null));
            regionMap.put(BeaconDefine.POINT_01, 101);

            beaconManager.startRangingBeacons(new Region(BeaconDefine.POINT_02, Identifier.parse(UNIQUE_ID), Identifier.fromInt(89), null));
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