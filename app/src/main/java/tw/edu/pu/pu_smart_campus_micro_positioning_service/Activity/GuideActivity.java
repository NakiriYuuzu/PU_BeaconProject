package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import static tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.DefaultSetting;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RequestHelper;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.YuuzuAlertDialog;

public class GuideActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String TAG = "GuideActivity: ";

    private static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 1000L;
    private static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 1000L;

    private FusedLocationProviderClient client;
    private GoogleMap gMap;
    private BeaconManager beaconManager;

    private RequestHelper requestHelper;
    private YuuzuAlertDialog alertDialog;
    private VolleyApi volleyApi;
    private ShareData shareData;

    private ShapeableImageView btnBack;

    private boolean alertChecked = false;
    private String apiURL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
        requestHelper.requestBluetooth();
        buttonInit();
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
        requestHelper = new RequestHelper(this);
        alertDialog = new YuuzuAlertDialog(this);
        shareData = new ShareData(this);

        //API init
        apiURL = DefaultSetting.API_GUIDE + shareData.getUID();
        volleyApi = new VolleyApi(GuideActivity.this, apiURL);
        volleyApi.post_API_GuideActivity_Close();

        // Google Maps findView
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Get user location findView
        client = LocationServices.getFusedLocationProviderClient(this);
    }

    private void startScanning() {
        new Thread(() -> requestHelper.flushBluetooth()).start();

        Log.e(TAG, "startScanning...");

        RangeNotifier rangeNotifier = (collection, region) -> {
            if (collection.size() > 0) {
                List<Beacon> beacons = new ArrayList<>();
                for (Beacon beaconData : collection) {
                    if (beaconData.getDistance() <= 15f) {
                        beacons.add(beaconData);
                    }

                    if (beacons.size() > 0) {

                        Collections.sort(beacons, (o1, o2) -> Double.compare(o2.getDistance(), o1.getDistance()));

                        Beacon beacon = beacons.get(0);
                        beaconData(beacon);
                    }
                }
            }
        };

        beaconManager.addRangeNotifier(rangeNotifier);
        beaconManager.startRangingBeacons(REGION_BEACON_01);
    }

    private void stopScanning() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();
        beaconManager.stopRangingBeacons(REGION_BEACON_01);
    }

    private void beaconData(Beacon beacon) {
        String major = String.valueOf(beacon.getId2());
        String minor = String.valueOf(beacon.getId3());
        String txpower = String.valueOf(beacon.getTxPower());
        String rssi = String.valueOf(beacon.getRssi());
        String distance = String.valueOf(beacon.getDistance());

        Log.e(TAG, major + "\n" + minor + "\n" + txpower + "\n" + rssi + "\n" + distance);

        apiPostData(major, minor, txpower, rssi, distance);
    }

    private void apiPostData(String major, String minor, String txpower, String rssi, String distance) {

        volleyApi.post_API_GuideActivity(new VolleyApi.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("onSuccess", result);
                String spotName = "", spotNote = "", spotImage = "", spotUrl = "";
                try {
                    JSONArray allData = new JSONArray(result);
                    for (int i = 0; i < allData.length(); i++) {
                        JSONObject data = allData.getJSONObject(i);
                        spotName = data.getString("viewname");
                        spotNote = data.getString("note");
                        spotImage = data.getString("image");
                        spotUrl = data.getString("url");
                    }
                    if (!spotName.equals("") && !spotNote.equals("") && !spotImage.equals("") && !spotUrl.equals("")) {
                        shareData.saveSpotTitle(spotName);
                        shareData.saveSpotInfo(spotNote);
                        shareData.saveSpotImage(spotImage);
                        shareData.saveSpotUrl(spotUrl);

                        switchAndNotify(Double.parseDouble(distance), spotName, result);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                Log.e(TAG, error.toString());
            }

        }, () -> {
            Map<String, String> params = new HashMap<>();
            params.put("uid", shareData.getUID());
            params.put("major", major);
            params.put("minor", minor);
            params.put("txpower", txpower);
            params.put("rssi", rssi);
            params.put("distance", distance);
            return params;
        });
    }

    private void apiCloseData() {
        VolleyApi volleyApi = new VolleyApi(this, apiURL);
        volleyApi.post_API_GuideActivity_Close();
    }

    private void switchAndNotify(double distance, String spotName, String result) {
        if (distance < 0f && distance > 15f) {
            alertChecked = false;
        }
        else {
            if (!result.equals("same position")) {
                if (!alertChecked) {
                    alertChecked = true;
                    alertDialog.showDialog("歡迎來到靜宜大學", "您的位置是: " + spotName + ".\n點擊確認并打開彩蛋~", new YuuzuAlertDialog.AlertCallback() {
                        @Override
                        public void onOkay(DialogInterface dialog, int which) {
                            Intent ii = new Intent(getApplicationContext(), GuideSpotActivity.class);
                            startActivity(ii);
                            dialog.dismiss();
                        }

                        @Override
                        public void onCancel(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        // 因為已經在 PoliceMainActivity 裡請求 Location，因此此處無需再請求 Location
        gMap.setMyLocationEnabled(true);

        // 將畫面定在使用者位置，前提是使用者的位置在靜宜大學範圍內
        ZoomToUserLocation();

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

    private void ZoomToUserLocation() {
        @SuppressLint("MissingPermission")
        Task<Location> locationTask = client.getLastLocation();

        try {
            locationTask.addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 17));
                }
            });

        } catch (Exception e) {
            Log.e(TAG, e.toString());
            Log.e("GuideActivity", "get nothing on location!");
            e.printStackTrace();
        }
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
        apiCloseData();
    }
}