package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;


import static tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.imageview.ShapeableImageView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RequestHelper;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.YuuzuAlertDialog;

public class GuideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "GuideActivity: ";

    private static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 1000L;
    private static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 1000L;

    private GoogleMap gMap;

    private BeaconManager beaconManager;
    private BeaconDefine beaconDefine;
    private RequestHelper requestHelper;
    private YuuzuAlertDialog alertDialog;

    private ShapeableImageView btnBack;

    private String TmpMajor;
    private String TmpMinor;

    private int CounterBeacon = 3;

    private boolean AlertShow = true;

    @SuppressLint("HandlerLeak")
    Handler objHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle objBundle = msg.getData();
            String Message = objBundle.getString("MSG_key");

            Log.e("Message", Message);
            Log.e("Counter", "CounterBeacon: " + CounterBeacon);
            if(CounterBeacon == 3 && AlertShow) {
                AlertShow = false;
                alertDialog.showDialog("景點導覽", Message, new YuuzuAlertDialog.AlertCallback() {
                    @Override
                    public void onOkay(DialogInterface dialog, int which) {
                        intentToGuideSpot(Message);
                        Log.e(TAG, "onClick: " + Message);
                        AlertShow = true;
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancel(DialogInterface dialog, int which) {
                        AlertShow = true;
                        dialog.dismiss();
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
        buttonInit();
        requestHelper.requestBluetooth();
        beaconInit();
    }

    private void beaconInit() {
        beaconManager = BeaconManager.getInstanceForApplication(this);

        //beacon AddStone m:0-3=4c000215 or alt beacon = m:2-3=0215
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.setForegroundBetweenScanPeriod(DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD);
        beaconManager.setForegroundScanPeriod(DEFAULT_FOREGROUND_SCAN_PERIOD);
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
        requestHelper = new RequestHelper(this);
        alertDialog = new YuuzuAlertDialog(this);

        // Google Maps findView
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void startScanning() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();

        new Thread(() -> requestHelper.flushBluetooth()).start();

        Log.e(TAG, "startScanning...");
        beaconManager.addRangeNotifier((collection, region) -> {
            if (collection.size() > 0) {
                List<Beacon> beacons = new ArrayList<>();
                for (Beacon beaconData : collection) {
                    if (beaconData.getDistance() <= 15f) {
                        beacons.add(beaconData);
                    }

                    if (beacons.size() > 0) {

                        Collections.sort(beacons, (o1, o2) -> Double.compare(o2.getDistance(), o1.getDistance()));

                        Beacon beacon = beacons.get(0);
                        showData(beacon);
                    }
                }
            }
        });

        beaconManager.startRangingBeacons(new Region("Beacon", Identifier.parse(UUID_IBEACON_V1), null, null));
    }

    private void stopScanning() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();
    }

    private void showData(Beacon beacon) {
        String major = String.valueOf(beacon.getId2());
        String minor = String.valueOf(beacon.getId3());

        Runnable objRunnable = new Runnable() {
            final Message objMessage = objHandler.obtainMessage();
            final Bundle objBundle = new Bundle();

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (TmpMajor == null && TmpMinor == null) {
                    TmpMajor = major;
                    TmpMinor = minor;

                    String str = beaconDefine.getLocationMsg(major, minor);

                    objBundle.putString("MSG_key", str);
                    objMessage.setData(objBundle);
                    objHandler.sendMessage(objMessage);
                }
                else if(TmpMajor != null && TmpMinor != null){
                    if(!TmpMajor.equals(major) && !TmpMinor.equals(minor)) {
                        TmpMajor = major;
                        TmpMinor = minor;
                        CounterBeacon = 2;

                        String str = beaconDefine.getLocationMsg(major, minor);

                        objBundle.putString("MSG_key", str);
                        objMessage.setData(objBundle);
                        objHandler.sendMessage(objMessage);
                    }
                    else {
                        String str = beaconDefine.getLocationMsg(major, minor);

                        objBundle.putString("MSG_key", str);
                        objMessage.setData(objBundle);
                        objHandler.sendMessage(objMessage);

                        CounterBeacon++;

                        if (CounterBeacon >= 300) {
                            CounterBeacon = 3;
                        }
                    }
                }
            }
        };

        Thread objBgThread = new Thread(objRunnable);
        objBgThread.start();
    }

    private void intentToGuideSpot(String str) {
        Intent ii = new Intent(getApplicationContext(), GuideSpotActivity.class);
        ii.putExtra("spotName", str);
        startActivity(ii);
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

    @Override
    protected void onPause() {
        super.onPause();
        stopScanning();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScanning();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startScanning();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScanning();
    }
}