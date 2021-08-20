package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimerTask;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconStore;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RequestItem;

public class SafetyActivity extends AppCompatActivity {

    private final String TAG = "SafetyActivity: ";

    private boolean animationRunning = false;
    private boolean sosIsRunning = false;
    private boolean firstChecked = true;
    private boolean apiChecked = true;

    private int count_Animation = 0;
    private int count_Api = 0;
    private int count_Alert = 0;

    LottieAnimationView animation;
    MaterialTextView btnSafety;
    ShapeableImageView btnBack, btnSOS;

    RequestItem requestItem;
    VolleyApi volleyApi;
    BeaconStore beaconStore;
    BeaconDefine beaconDefine;

    BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        initView();
        initButton();
        beaconInit();

        requestItem.requestBluetooth();
    }

    private void initButton() {
        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnSafety.setOnClickListener(v -> {
            if (!animationRunning) {
                animationStart();

            } else {
                animationStop();
            }
        });

        btnSOS.setOnClickListener(v -> {
            if (!sosIsRunning) {
                sos_Start();
            }
            else {
                sos_Stop();
            }
        });
    }

    private void initView() {
        btnSOS = findViewById(R.id.btn_safety_SOS);
        btnBack = findViewById(R.id.btn_safety_back);
        btnSafety = findViewById(R.id.btn_safety_trace);
        animation = findViewById(R.id.safety_Animation);

        btnSafety.setText(R.string.safety_Start);

        requestItem = new RequestItem(this);
    }

    private void beaconInit() {
        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
    }

    private void startScanning() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beaconCollection, Region region) {
                if (beaconCollection.size() > 0) {
                    count_Alert = 0;
                    firstChecked = false;
                    Log.e(TAG, "Success Get data");
                    List<Beacon> beacons = new ArrayList<>();
                    for (Beacon beacon : beaconCollection) {
                        beacons.add(beacon);
                    }

                    if (beacons.size() > 0) {
                        Collections.sort(beacons, new Comparator<Beacon>() {
                            @Override
                            public int compare(Beacon o1, Beacon o2) {
                                return Double.compare(o2.getDistance(), o1.getDistance());
                            }
                        });

                        apiTimer();
                        if (beacons.size() > 1) {
                            beaconStore = new BeaconStore(beacons.get(0), beacons.get(1));
                            beaconStore.beaconData(apiChecked);
                        }

                        else if (beacons.size() == 1) {
                            beaconStore = new BeaconStore(beacons.get(0));
                            beaconStore.beaconData(apiChecked);
                        }
                    }

                } else {
                    Log.e(TAG, "NO Beacon here.");
                    if (firstChecked) {
                        Log.e(TAG, "Alert! 此道路暫時不支援安全通道。");
                        count_Animation++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (count_Animation == 4) {
                                    animationStop();
                                    count_Animation = 0;
                                }
                            }
                        });

                    } else {
                        dialogTimer();
                    }
                }
            }
        });
        try {
            beaconManager.startRangingBeacons(new Region("", null, null, null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopScanning() {
        beaconManager.removeAllRangeNotifiers();
    }

    private void animationStart() {
        animation.playAnimation();
        btnSafety.setText(R.string.safety_Activate);

        startScanning();

        animationRunning = true;
    }

    private void animationStop() {
        animation.setProgress(0);
        animation.cancelAnimation();
        btnSafety.setText(R.string.safety_Start);

        stopScanning();

        animationRunning = false;
    }

    private void sos_Start() {
        int imageRes = getResources().getIdentifier("sos_1", "drawable", getPackageName());
        btnSOS.setImageResource(imageRes);
        sosIsRunning = true;
    }

    private void sos_Stop() {
        int imageRes = getResources().getIdentifier("sos_0", "drawable", getPackageName());
        btnSOS.setImageResource(imageRes);
        sosIsRunning = false;
    }

    private void apiTimer() {
        new Handler().postDelayed(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "count_API: " + count_Api);
                count_Api++;
                if (count_Api == 5) {
                    apiChecked = true;
                    count_Api = 0;

                } else {
                    apiChecked = false;
                }
            }
        }, 1000);
    }

    private void dialogTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "count_Alert: " + count_Alert);
                count_Alert++;
                if (count_Alert >= 59) {
                    count_Alert = 0;
                    Log.e(TAG, "Alert!!!!!");
                }
            }
        }, 1000);
    }
}