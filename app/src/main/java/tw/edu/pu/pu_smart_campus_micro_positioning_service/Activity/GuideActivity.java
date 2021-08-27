package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.permissionx.guolindev.PermissionX;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RequestItem;

public class GuideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "GuideActivity: ";

    private static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 1000L;
    private static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 1000L;

    private GoogleMap gMap;

    private BeaconManager beaconManager;
    private BeaconDefine beaconDefine;
    private RequestItem requestItem;

    private ShapeableImageView btnBack;

    private String TmpMajor;
    private String TmpMinor;
    private boolean shouldShowAlert = true;

    @SuppressLint("HandlerLeak")
    Handler objHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle objBundle = msg.getData();
            String Message = objBundle.getString("MSG_key");

            Log.e("Message", Message);
            showAlert(Message);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
        buttonInit();
        requestPermission();
        requestItem.requestBluetooth();
        beaconInit();
    }

    private void beaconInit() {
        beaconManager = BeaconManager.getInstanceForApplication(this);

        //beacon AddStone m:0-3=4c000215 or alt beacon = m:2-3=0215
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.setForegroundBetweenScanPeriod(DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD);
        beaconManager.setForegroundScanPeriod(DEFAULT_FOREGROUND_SCAN_PERIOD);

        startScanning();
    }

    private void buttonInit() {
        btnBack.setOnClickListener(v -> {
            stopScanning();
            finish();
        });
    }

    private void initView() {
        //findView
        btnBack = findViewById(R.id.btn_Guide_back);
        beaconDefine = new BeaconDefine();
        requestItem = new RequestItem(this);

        // Google Maps findView
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
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
        } else {
            Toast.makeText(getApplicationContext(), "您的手機無法使用該應用...", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void startScanning() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();

        Log.e(TAG, "startScanning...");
        beaconManager.addRangeNotifier((collection, region) -> {
            Log.e(TAG, "SCANNING!\nBeaconDATA: " + collection.size());
            if (collection.size() > 0) {
                List<Beacon> beacons = new ArrayList<>();
                for (Beacon beaconData : collection) {
                    if (beaconData.getDistance() <= 30) {
                        beacons.add(beaconData);
                        Log.e("Beacon", beaconDefine.getLocationMsg(String.valueOf(beaconData.getId2()), String.valueOf(beaconData.getId3())));
                    }

                    if (beacons.size() > 0) {

                        Collections.sort(beacons, (o1, o2) -> Double.compare(o2.getDistance(), o1.getDistance()));

                        Beacon beacon = beacons.get(0);
                        Log.e("Should show Alert", String.valueOf(shouldShowAlert));
                        showData(beacon);
                    }
                }
            } else {
                Log.e("BeaconMsg", "didRangeBeaconsInRegion: No Beacon detected");
            }
        });

        try {
            beaconManager.startRangingBeacons(new Region("Beacon", Identifier.parse("699ebc80-e1f3-11e3-9a0f-0cf3ee3bc012"), null, null));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopScanning() {
        beaconManager.removeAllRangeNotifiers();
    }

    private void showData(Beacon beacon) {
        String major = String.valueOf(beacon.getId2());
        String minor = String.valueOf(beacon.getId3());

        Runnable objRunnable = new Runnable() {
            Message objMessage = objHandler.obtainMessage();
            Bundle objBundle = new Bundle();

            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (TmpMajor == null && TmpMinor == null) {
                    TmpMajor = major;
                    TmpMinor = minor;
                }

                if (TmpMajor.equals(major) && TmpMinor.equals(minor)) {
                    if (shouldShowAlert) {
                        String str = beaconDefine.getLocationMsg(major, minor);

                        switch (str) {
                            case "主顧聖母堂":
                            case "主顧樓":
                            case "若望保祿二世體育館":
                                shouldShowAlert = false;
                                objBundle.putString("MSG_key", str);
                                objMessage.setData(objBundle);
                                objHandler.sendMessage(objMessage);
                        }
                    } else {
                        shouldShowAlert = true;
                        TmpMajor = major;
                        TmpMinor = minor;
                    }
                }
            }
        };

        Thread objBgThread = new Thread(objRunnable);
        objBgThread.start();
    }

    private void showAlert(String str) {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        materialAlertDialogBuilder.setTitle("景點導覽");
        materialAlertDialogBuilder.setMessage(str);
        materialAlertDialogBuilder.setBackground(getResources().getDrawable(R.drawable.alert_dialog, null));
        materialAlertDialogBuilder.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (str) {
                    case "主顧聖母堂":
                    case "主顧樓":
                    case "若望保祿二世體育館":
                        intentToGuideSpot(str);
                        Log.e(TAG, "onClick: " + str);
                        break;
                }
                dialog.dismiss();
            }
        });
        materialAlertDialogBuilder.show();
    }

    private void intentToGuideSpot(String str) {
        Intent intent = new Intent();
        intent.setClass(GuideActivity.this, GuideSpotActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Key", str);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.removeAllRangeNotifiers();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        // 靜宜大學在Google Maps上的經緯度
        //LatLng pu = new LatLng(24.22614525191815, 120.57709151695924);

        // 將範圍限定在靜宜大學
        LatLngBounds puBounds = new LatLngBounds(
                new LatLng(24.22448382742779, 120.57652347691048),
                new LatLng(24.22995245833786, 120.58448818883151)
        );

        // 校園10個景點的經緯度
        LatLng rome = new LatLng(24.22656871091271, 120.57929918020874); // 羅馬小劇場
        LatLng walkway = new LatLng(24.226427833581084, 120.57887211932552); // 櫻花步道
        LatLng library = new LatLng(24.226204112427833, 120.58132421032526); // 盖夏图书馆
        LatLng bigLawn = new LatLng(24.225789857013105, 120.57800583799148); // 校園前大草坪
        LatLng fountain = new LatLng(24.226700226331236, 120.58135686967283); // 噴水池
        LatLng puChapel = new LatLng(24.228066079615992, 120.58148984619457); // 主顧聖母堂
        LatLng artCenter = new LatLng(24.226646215767868, 120.57996053368652); // 任垣藝術中心
        LatLng sportHall = new LatLng(24.229192561674754, 120.58106247771143); // 若望保祿二世體育館
        LatLng loverBridge = new LatLng(24.226613625861138, 120.58000175246843); // 情人橋
        LatLng swimmingPool = new LatLng(24.22942687416963, 120.58019098909462); // 溫水游泳池

        // 在地圖添加標點 和 設置標點的範圍
        addMarker_Circle(rome, getString(R.string.Rome), 20);
        addMarker_Circle(walkway, getString(R.string.Cherry_Blossom_Walkway), 20);
        addMarker_Circle(library, getString(R.string.Library), 35);
        addMarker_Circle(bigLawn, getString(R.string.Big_Lawn), 50);
        addMarker_Circle(fountain, getString(R.string.Fountain), 15);
        addMarker_Circle(puChapel, getString(R.string.Providence_Chapel), 25);
        addMarker_Circle(artCenter, getString(R.string.Art_Center), 20);
        addMarker_Circle(sportHall, getString(R.string.Sport_Hall), 40);
        addMarker_Circle(loverBridge, getString(R.string.Lover_Bridge), 20);
        addMarker_Circle(swimmingPool, getString(R.string.Swimming_Pool), 25);

        // 限定縮放級別 和 地圖平移限制
        gMap.setMinZoomPreference(16);
        gMap.setMaxZoomPreference(19);
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(puBounds, 0));
        gMap.setLatLngBoundsForCameraTarget(puBounds);
    }

    private void addMarker_Circle(LatLng latLng, String title, int radius) {
        Marker marker = gMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title));
        if (marker != null) {
            marker.showInfoWindow();
        }

        gMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(getColor(R.color.strokeWidth))
                .fillColor(getColor(R.color.fillStroke))
                .strokeWidth(5));
    }
}