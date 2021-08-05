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
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.ToDoubleFunction;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class SafetyActivity extends AppCompatActivity implements BeaconConsumer {

    private final String TAG = "SafetyActivity: ";
    private final String IPHONE_UUID = "594650a2-8621-401f-b5de-6eb3ee398170";
    private final String IBEACON_UUID = "699ebc80-e1f3-11e3-9a0f-0cf3ee3bc012";

    private static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 500L; // half sec
    private static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 500L; // half sec

    private BeaconManager beaconManager;
    private BeaconDefine beaconDefine;

    private MaterialButton btnStart, btnStop;
    private MaterialTextView tvShowDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        initView();
        initButton();
    }

    private void initBeacon() {
        beaconManager = BeaconManager.getInstanceForApplication(this);
        //beacon AddStone m:0-3=4c000215 or alt beacon = m:2-3=0215
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.setForegroundBetweenScanPeriod(DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD);
        beaconManager.setForegroundScanPeriod(DEFAULT_FOREGROUND_SCAN_PERIOD);

        beaconManager.bind(this);
    }

    private void initButton() {
        btnStart.setOnClickListener(v -> {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initBeacon();
                }
            }).start();
        });

        btnStop.setOnClickListener(v -> {
            beaconManager.unbind(this);
        });
    }

    private void initView() {
        tvShowDisplay = findViewById(R.id.showDisplay);
        btnStart = findViewById(R.id.btn_Receive_Start);
        btnStop = findViewById(R.id.btn_Receive_Stop);

        beaconDefine = new BeaconDefine();
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                if (collection.size() > 0) {
                    List<Beacon> beacons = new ArrayList<>();
                    for (Beacon beacon : collection) {
                        if (beacon.getId1().toString().equalsIgnoreCase(IPHONE_UUID) || beacon.getId1().toString().equalsIgnoreCase(IBEACON_UUID) && beacon.getDistance() <= 30F) {
                            beacons.add(beacon);
                            Log.e("Debug01", beacon.toString());
                        }
                    }

                    if (beacons.size() > 0) {

                        Collections.sort(beacons, new Comparator<Beacon>() {
                            public int compare(Beacon arg0, Beacon arg1) {
                                //Rssi 判斷
                                return (arg1.getRssi() - arg0.getRssi());

                                //Distance 判斷
                                //return Double.compare(arg0.getDistance(), arg1.getDistance());
                            }
                        });

                        Beacon nearBeacon = beacons.get(0);
                        String uniqueID = nearBeacon.getId1().toString();
                        String major = nearBeacon.getId2().toString();
                        String minor = nearBeacon.getId3().toString();
                        String rssi = String.valueOf(nearBeacon.getRssi());
                        String distance = String.valueOf(nearBeacon.getDistance());
                        String address = nearBeacon.getBluetoothAddress();
                        String txPower = String.valueOf(nearBeacon.getTxPower());

                        @SuppressLint("DefaultLocale")
                        String str = String.format("Distance: %s%nUniqueID: %s%nMajor: %s%nMinor: %s%nRSSI: %s%nAddress: %s%nTxPower: %s%n",
                                distance, uniqueID, major, minor, rssi, address, txPower);

                        String location = beaconDefine.getLocationMsg(major, minor);
                        Log.e("Debug02", str);
                        Log.e("Debug03", location);

                        updateTextViewMsg(location);
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeacons(new Region(IPHONE_UUID, null, null, null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTextViewMsg(final String location) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvShowDisplay.setText(location);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

}