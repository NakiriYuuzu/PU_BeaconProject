package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import static tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine.*;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconStore;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.YuuzuAlertDialog;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RequestHelper;

public class SafetyActivity extends AppCompatActivity {

    private final String TAG = "SafetyActivity: ";

    private boolean animationRunning = false;
    private boolean sosIsRunning = false;
    private boolean firstChecked = true;
    private boolean apiChecked = true;

    private int count_Animation = 0;
    private int count_Api = 0;
    private int count_Alert = 0;

    private final String api_Safety_Start = "http://120.110.93.246/CAMEFSC/public/api/monitor/start";
    private final String api_Safety_Stop = "http://120.110.93.246/CAMEFSC/public/api/monitor/end";
    private final String api_SOS_Start = "http://120.110.93.246/CAMEFSC/public/api/monitor/sos";
    private final String api_SOS_Stop = "http://120.110.93.246/CAMEFSC/public/api/monitor/sosend";

    LottieAnimationView animation;
    MaterialTextView btnSafety, btnTEST;
    ShapeableImageView btnBack, btnSOS;

    RequestHelper requestHelper;
    BeaconStore beaconStore;
    YuuzuAlertDialog alertDialog;

    BeaconManager beaconManager;
    MediaPlayer mediaPlayer;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);

        initView();
        initButton();
        beaconInit();
        requestHelper.requestBluetooth();
        btnTEST = findViewById(R.id.btnTEST);
        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VolleyApi volleyApi = new VolleyApi(SafetyActivity.this, api_Safety_Stop);
                volleyApi.post_API_Safety_Stop();
            }
        });
    }

    private void initButton() {
        btnBack.setOnClickListener(v -> {
            animationStop();
            finish();
        });

        btnSafety.setOnClickListener(v -> {
            if (!animationRunning) {
                alertDialog.showDialog("安全監控", "啓動此功能會實時偵測您所在的位置", new YuuzuAlertDialog.AlertCallback() {
                    @Override
                    public void onOkay(DialogInterface dialog, int which) {
                        animationStart();
                        startScanning(api_Safety_Start);
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancel(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

            } else {
                stopScanning();
                sos_Stop();
                animationStop();
                apiSafetyStop();
            }
        });

        btnSOS.setOnClickListener(v -> {
            if (animationRunning) {
                if (!sosIsRunning) {
                    alertDialog.showDialog("安全通道SOS", "此功能會直接呼叫警衛室", new YuuzuAlertDialog.AlertCallback() {
                        @Override
                        public void onOkay(DialogInterface dialog, int which) {
                            stopScanning();
                            startScanning(api_SOS_Start);
                            sos_Start();
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancel(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    sos_Stop();
                    stopScanning();
                    apiSosStop();
                    startScanning(api_Safety_Start);
                }
            } else {
                alertDialog.showDialog("安全通道SOS", "請麻煩先打開安全監控才能打開SOS!", new YuuzuAlertDialog.AlertCallback() {
                    @Override
                    public void onOkay(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancel(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void initView() {
        btnSOS = findViewById(R.id.btn_safety_SOS);
        btnBack = findViewById(R.id.btn_safety_back);
        btnSafety = findViewById(R.id.btn_safety_trace);
        animation = findViewById(R.id.safety_Animation);

        btnSafety.setText(R.string.safety_Start);

        requestHelper = new RequestHelper(this);
        alertDialog = new YuuzuAlertDialog(this);
    }

    private void beaconInit() {
        beaconManager = BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
    }

    private void startScanning(String api) {
        new Thread(() -> requestHelper.flushBluetooth()).start();

        beaconManager.addRangeNotifier((beaconCollection, region) -> {
            if (beaconCollection.size() > 0) {
                count_Alert = 0;
                firstChecked = false;
                Log.e(TAG, "Success Get data");
                List<Beacon> beacons = new ArrayList<>();

                for (Beacon beacon : beaconCollection) {
                    beacons.add(beacon);
                }

                if (beacons.size() > 0) {
                    Collections.sort(beacons, (o1, o2) -> Double.compare(o2.getDistance(), o1.getDistance()));

                    apiTimer();

                    beaconStore = new BeaconStore(SafetyActivity.this, beacons.get(0));
                    beaconStore.beaconData(apiChecked, api);
                }

            } else {
                Log.e(TAG, "NO Beacon here.");
                if (firstChecked) {
                    Log.e(TAG, "Alert! 此道路暫時不支援安全通道。");
                    count_Animation++;
                    runOnUiThread(() -> {
                        if (count_Animation == 5) {
                            alertDialog.showDialog("安全通道", "此道路暫時不支援安全通道!", new YuuzuAlertDialog.AlertCallback() {
                                @Override
                                public void onOkay(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onCancel(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            animationStop();
                            count_Animation = 0;
                        }
                    });

                } else {
                    dialogTimer();
                }
            }
        });

        beaconManager.startRangingBeacons(REGION_BEACON_01);
    }

    private void stopScanning() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.stopRangingBeacons(REGION_BEACON_01);
        beaconManager.removeAllRangeNotifiers();
    }

    private void animationStart() {
        animation.playAnimation();
        btnSafety.setText(R.string.safety_Activate);

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
        soundPlay();
        int imageRes = getResources().getIdentifier("sos_1", "drawable", getPackageName());
        btnSOS.setImageResource(imageRes);
        sosIsRunning = true;
    }

    private void sos_Stop() {
        soundStop();
        int imageRes = getResources().getIdentifier("sos_0", "drawable", getPackageName());
        btnSOS.setImageResource(imageRes);
        sosIsRunning = false;
    }

    private void soundPlay() {
        try {
            audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

            if (audioManager.isSpeakerphoneOn()) {
                audioManager.setStreamVolume(AudioManager.STREAM_RING, 100, 0);

                if (mediaPlayer == null) {
                    mediaPlayer = MediaPlayer.create(this, R.raw.beepsoundeffect);
                }

                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(mp -> mediaPlayer.start());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void soundStop() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void apiSafetyStop() {
        VolleyApi volleyApi = new VolleyApi(this, api_Safety_Stop);
        volleyApi.post_API_Safety_Stop();
    }

    private void apiSosStop() {
        VolleyApi volleyApi = new VolleyApi(this, api_SOS_Stop);
        volleyApi.post_API_Safety_Stop();
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
        new Handler().postDelayed(() -> {
            Log.e(TAG, "count_Alert: " + count_Alert);
            count_Alert++;
            if (count_Alert >= 59) {
                count_Alert = 0;
                Log.e(TAG, "Alert!!!!!");
            }
        }, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScanning();
        apiSosStop();
        apiSafetyStop();
    }
}