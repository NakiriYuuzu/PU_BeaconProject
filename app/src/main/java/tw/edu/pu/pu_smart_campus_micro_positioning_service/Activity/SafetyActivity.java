package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.permissionx.guolindev.PermissionX;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.RunnableFuture;

import javax.security.auth.login.LoginException;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.TimerInBg;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class SafetyActivity extends AppCompatActivity implements BeaconConsumer {
    private final String TAG = "SafetyActivity: ";

    private static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 1000L; // half sec
    private static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 1000L; // half sec

    private final double DISTANCE_THRESHOLD = 3.5f;
    private final double DISTANCE_THRESHOLD_TEST = 0.5f;

    private boolean beaconRunning = false;
    private boolean animationRunning = false;
    private boolean isTimerStarted = false;
    private volatile boolean stopThread = false;

    private static final long Timer = 10000;
    private long TimeLeft = Timer;
    private CountDownTimer countDownTimer;

    private BeaconManager beaconManager;
    private BeaconDefine beaconDefine;

    private MaterialButton btnStart, btnStop;
    private ShapeableImageView btnBack;
    private MaterialTextView tvShowDisplay, btnSafety;
    private LottieAnimationView safetyAnimation;

    private String major;
    private String minor;
    private int Counter = 0;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            String str = bundle.getString("Key");

            showAlert(str);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        initView();
        requestPermission();
        requestBluetooth();
        initButton();

        Log.e("isTimerStarted", String.valueOf(isTimerStarted));
        Log.e("Timer", String.valueOf(TimeLeft));
    }

    private void initBeacon() {
        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.setForegroundBetweenScanPeriod(DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD);
        beaconManager.setForegroundScanPeriod(DEFAULT_FOREGROUND_SCAN_PERIOD);

        beaconRunning = true;
    }

    private void initButton() {
        btnStart.setOnClickListener(v -> {
            initBeacon();
            Log.e(TAG, "didRangeBeaconsInRegion: " + major + ", " + minor + ", " + isTimerStarted);
            if(major == null && minor == null || isTimerStarted == false) {
                TimerRunInBg timer = new TimerRunInBg();
                timer.start();
            }
        });

        btnStop.setOnClickListener(v -> {
            if (beaconRunning) {
                beaconManager.removeAllRangeNotifiers();
                beaconRunning = false;
            }
        });

        btnSafety.setOnClickListener(v -> {
            Log.e(TAG, "initButton: " + animationRunning + ", " + beaconRunning);

            if (animationRunning && beaconRunning) {
                animationStop();

                beaconManager.removeAllRangeNotifiers();
                beaconRunning = false;

                stopThread = true;
            }
            else {
                animationStart();

                initBeacon();
                TimerRunInBg timer = new TimerRunInBg();
                timer.start();
                stopThread = false;
            }
        });

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void initView() {
        tvShowDisplay = findViewById(R.id.showDisplay);
        btnStart = findViewById(R.id.btn_Receive_Start);
        btnStop = findViewById(R.id.btn_Receive_Stop);
        btnBack = findViewById(R.id.btn_safety_back);

        btnSafety = findViewById(R.id.btn_safety_trace);
        btnSafety.setText(R.string.safety_Start);

        safetyAnimation = findViewById(R.id.safety_Animation);

        beaconDefine = new BeaconDefine();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT > 23) {
            PermissionX.init(this)
                    .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

                    .onExplainRequestReason((scope, deniedList) -> scope.showRequestReasonDialog(
                            deniedList, "Grant Permission!", "Sure", "Cancel"))

                    .request((allGranted, grantedList, deniedList) -> {
                        if (!allGranted) {
                            Toast.makeText(getApplicationContext(), "Grant Permission failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(getApplicationContext(), "您的手機無法使用該應用...", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void requestBluetooth() {
        BluetoothAdapter mBluetoothAdapter;

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(this, "Bluetooth is not supported!", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble is not supported!", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBluetooth);
        }
    }

    @Override
    public void onBeaconServiceConnect() {
        passData(major, minor);
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                if (collection.size() > 0) {
                    stopThread = true;

                    List<Beacon> beacons = new ArrayList<>();
                    for (Beacon beacon : collection) {
                        if (beacon.getDistance() <= 30) {
                            beacons.add(beacon);
                            Log.e("Debug01", beacon.toString());
                        }
                    }

                    if (beacons.size() > 0) {
                        Counter++;
                        Collections.sort(beacons, new Comparator<Beacon>() {
                            public int compare(Beacon arg0, Beacon arg1) {
                                //Rssi 判斷
                                //return (arg1.getRssi() - arg0.getRssi());

                                //Distance 判斷
                                return Double.compare(arg0.getDistance(), arg1.getDistance());
                            }
                        });

                        Beacon beacon = beacons.get(0);
                        major = String.valueOf(beacon.getId2());
                        minor = String.valueOf(beacon.getId3());
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeacons(new Region("Beacon", Identifier.parse("699ebc80-e1f3-11e3-9a0f-0cf3ee3bc012"), null, null));
            beaconManager.startRangingBeacons(new Region("IPhone", Identifier.parse("594650a2-8621-401f-b5de-6eb3ee398170"), null, null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void passData(String major, String minor){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this){
                    try {
                        wait(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if(Counter > 0){
                    // 傳major minor 到後臺
                    Log.e(TAG, "run: major minor 傳到後臺");
                    Counter = 0;
                }
            }
        };

        Thread passThread = new Thread(runnable);
        passThread.start();
    }

    class TimerRunInBg extends Thread {
        Message message = handler.obtainMessage();
        Bundle bundle = new Bundle();

        @Override
        public void run() {
            try {
                isTimerStarted = true;
                for(int i = 0; i <= 60; i++){
                    if(stopThread){
                        return;
                    }
                    Log.e("i", String.valueOf(i));
                    if(i == 59){
                        String str = "請到安全通道方能使用此功能";
                        Log.e("str", str);
                        bundle.putString("Key", str);
                        message.setData(bundle);
                        handler.sendMessage(message);
                        beaconManager.removeAllRangeNotifiers();
                        beaconRunning = false;
                    }
                    Thread.sleep(1000);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String str) {
        AlertDialog dlg = new AlertDialog.Builder(SafetyActivity.this)
                .setTitle("安全通道")
                .setMessage(str)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dlg.show();
    }

    private void animationStart() {
        safetyAnimation.playAnimation();
        btnSafety.setText(R.string.safety_Activate);
        animationRunning = true;
    }

    private void animationStop(){
        safetyAnimation.setProgress(0);
        safetyAnimation.cancelAnimation();
        btnSafety.setText(R.string.safety_Start);
        animationRunning = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beaconRunning) {
            beaconManager.unbind(this);
        }
    }
}