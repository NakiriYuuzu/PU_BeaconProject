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
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
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
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon.BeaconDefine;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Database.DBHelper;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class GuideActivity extends AppCompatActivity implements BeaconConsumer, OnMapReadyCallback {

    private final String TAG = "SafetyActivity: ";

    private static final long DEFAULT_FOREGROUND_SCAN_PERIOD = 500L; // half sec
    private static final long DEFAULT_FOREGROUND_BETWEEN_SCAN_PERIOD = 500L; // half sec

    private final float DISTANCE_THRESHOLD = 3f;
    private final float DISTANCE_THRESHOLD_DATA = 1.5f;

    private boolean beaconIsRunning = false;

    private GoogleMap gMap;

    private BeaconManager beaconManager;
    private BeaconDefine beaconDefine;

    private MaterialButton btnStart, btnStop;
    private MaterialTextView tvShowDisplay;
    private ShapeableImageView btnBack;

    private ArrayList<HashMap<String, String>> beaconMap = new ArrayList<>();
    private HashMap<String, Integer> regionMap = new HashMap<>();
    private ArrayList<Beacon> beaconList = new ArrayList<>();

    private String TmpMajor;
    private String TmpMinor;
    private boolean shouldShowAlert = true;

    Handler objHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle objBundle = msg.getData();
            String Message = objBundle.getString("MSG_key");

            Log.e("Message", Message);
            tvShowDisplay.setText(Message);
            showAlert(Message);
        }
    };


    private DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initView();
        requestPermission();
        requestBluetooth();
        buttonInit();
        createSpotData();
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
        btnBack.setOnClickListener(v -> {
            finish();
        });

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
                beaconManager.removeAllRangeNotifiers();
                beaconIsRunning = false;
            }
        });
    }

    private void initView() {
        //findView
        btnBack = findViewById(R.id.btn_Guide_back);
        tvShowDisplay = findViewById(R.id.tv_Guide_information);
        btnStart = findViewById(R.id.btn_Guide_Start);
        btnStop = findViewById(R.id.btn_Guide_Stop);

        beaconDefine = new BeaconDefine();

        // Google Maps findView
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
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

    private void createSpotData() throws SQLiteException {
        DB = new DBHelper(this);

        // 從Resource裡拿資訊

        // 主顧聖母堂
        String pu_chapel_name = getResources().getString(R.string.Providence_Chapel);
        String pu_chapel_info = getResources().getString(R.string.Providence_Chapel_Info);
        String pu_chapel_url = getResources().getString(R.string.Providence_Chapel_Link);

        // 主顧樓
        String pu_hall_name = getResources().getString(R.string.Providence_Hall);
        String pu_hall_info = getResources().getString(R.string.Providence_Hall_Info);
        String pu_hall_url = getResources().getString(R.string.Providence_Hall_Link);

        // 體育館
        String sport_hall_name = getResources().getString(R.string.Sport_Hall);
        String sport_hall_info = getResources().getString(R.string.Sport_hall_Info);
        String sport_hall_url = getResources().getString(R.string.Sport_Hall_Link);

        // 將資料依序存入資料庫
        DB.insertSpotData(pu_chapel_name, pu_chapel_info, pu_chapel_url);
        DB.insertSpotData(pu_hall_name, pu_hall_info, pu_hall_url);
        DB.insertSpotData(sport_hall_name, sport_hall_info, sport_hall_url);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.removeAllMonitorNotifiers();
        beaconManager.removeAllRangeNotifiers();

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @SuppressLint("SetTextI18n")
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                if (collection.size() > 0) {
                    List<Beacon> beacons = new ArrayList<>();
                    for (Beacon beaconData : collection) {
                        if (beaconData.getDistance() <= 30) {
                            beacons.add(beaconData);
                            Log.e("Beacon", beaconDefine.getLocationMsg(String.valueOf(beaconData.getId2()), String.valueOf(beaconData.getId3())));
                        }
                    }

                    if (beacons.size() > 0) {

                        Collections.sort(beacons, new Comparator<Beacon>() {
                            @Override
                            public int compare(Beacon o1, Beacon o2) {
                                // Rssi 判斷
                                //return o2.getRssi() - o1.getRssi();

                                //Distance 判斷
                                return Double.compare(o2.getDistance(), o1.getDistance());
                            }
                        });

                        Beacon beacon = beacons.get(0);
                        Log.e("Should show Alert", String.valueOf(shouldShowAlert));
                        showData(beacon);
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

                if (firstTimeCheck()) {
                    TmpMajor = major;
                    TmpMinor = minor;
                }

                if (TmpMajor.equals(major) && TmpMinor.equals(minor)) {
                    if (shouldShowAlert) {
                        String str;

                        switch (beaconDefine.getLocationMsg(major, minor)) {
                            case "IBEACON_10":
                                str = String.format("景點名稱：聖母堂");
                                objBundle.putString("MSG_key", str);
                                objMessage.setData(objBundle);
                                shouldShowAlert = false;
                                break;
                            case "IBEACON_11":
                                str = String.format("景點名稱：主顧樓");
                                objBundle.putString("MSG_key", str);
                                objMessage.setData(objBundle);
                                shouldShowAlert = false;
                                break;
                            case "IBEACON_21":
                                str = String.format("景點名稱：保祿二世體育館");
                                objBundle.putString("MSG_key", str);
                                objMessage.setData(objBundle);
                                shouldShowAlert = false;
                                break;
                        }

                        objHandler.sendMessage(objMessage);
                    }
                } else {
                    shouldShowAlert = true;
                    TmpMajor = major;
                    TmpMinor = minor;
                }

            }
        };

        Thread objBgThread = new Thread(objRunnable);
        objBgThread.start();
    }

    private boolean firstTimeCheck() {
        if (TmpMajor == null && TmpMinor == null)
            return true;
        else
            return false;
    }

    private void showAlert(String str) {
        AlertDialog dlg = new AlertDialog.Builder(GuideActivity.this)
                .setTitle("Testing")
                .setMessage(str)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (str) {
                            case "景點名稱：聖母堂":
                                intentToGuideSpot("spot01");
                                Log.e(TAG, "onClick: " + str);
                                break;
                            case "景點名稱：主顧樓":
                                intentToGuideSpot("spot02");
                                Log.e(TAG, "onClick: " + str);
                                break;
                            case "景點名稱：保祿二世體育館":
                                intentToGuideSpot("spot03");
                                Log.e(TAG, "onClick: " + str);
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .create();
        dlg.show();
    }

    private void intentToGuideSpot(String key) {

        Intent intent = new Intent();
        intent.setClass(GuideActivity.this, GuideSpotActivity.class);
        //intent.putExtra(key, "spot");
        Bundle bundle = new Bundle();
        bundle.putBoolean(key, true);
        intent.putExtras(bundle);
        startActivity(intent);

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        // 靜宜大學在Google Maps上的經緯度
        //LatLng pu = new LatLng(24.22614525191815, 120.57709151695924);

        // 醬範圍限定在靜宜大學
        LatLngBounds puBounds = new LatLngBounds(
                new LatLng(24.22448382742779, 120.57652347691048),
                new LatLng(24.22995245833786, 120.58448818883151)
        );

        // 校園10個景點的經緯度
        LatLng rome = new LatLng(24.226610134801795, 120.57928995320502); // 羅馬小劇場
        LatLng walkway = new LatLng(24.2264189871141, 120.57889715093683); // 櫻花步道
        LatLng library = new LatLng(24.22619638950677, 120.58132494938461); // 盖夏图书馆
        LatLng bigLawn = new LatLng(24.22579162681339, 120.57802974150556); // 校園前大草坪
        LatLng fountain = new LatLng(24.226702031880514, 120.581359417509); // 噴水池
        LatLng puChapel = new LatLng(24.22806516348092, 120.58148547507882); // 主顧聖母堂
        LatLng artCenter = new LatLng(24.22666298140053, 120.57996407804856); // 任垣藝術中心
        LatLng sportHall = new LatLng(24.22892328815199, 120.58090492459124); // 若望保祿二世體育館
        LatLng loverBridge = new LatLng(24.22660666908093, 120.58000585060829); // 情人橋
        LatLng swimmingPool = new LatLng(24.229518440880714, 120.58041574557829); // 溫水游泳池

        // 在地圖添加標點 和 設置標點的範圍
        addMarker_Circle(rome, getString(R.string.Rome));
        addMarker_Circle(walkway, getString(R.string.Cherry_Blossom_Walkway));
        addMarker_Circle(library, getString(R.string.Library));
        addMarker_Circle(bigLawn, getString(R.string.Big_Lawn));
        addMarker_Circle(fountain, getString(R.string.Fountain));
        addMarker_Circle(puChapel, getString(R.string.Providence_Chapel));
        addMarker_Circle(artCenter, getString(R.string.Art_Center));
        addMarker_Circle(sportHall, getString(R.string.Sport_Hall));
        addMarker_Circle(loverBridge, getString(R.string.Lover_Bridge));
        addMarker_Circle(swimmingPool, getString(R.string.Swimming_Pool));

        // 限定縮放級別 和 地圖平移限制
        gMap.setMinZoomPreference(15);
        gMap.setMaxZoomPreference(19);
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(puBounds, 0));
        gMap.setLatLngBoundsForCameraTarget(puBounds);
    }

    private void addMarker_Circle(LatLng latLng, String title) {
        gMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(title));
        gMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(20)
                .strokeColor(getColor(R.color.strokeWidth))
                .fillColor(getColor(R.color.fillStroke))
                .strokeWidth(5));
    }
}