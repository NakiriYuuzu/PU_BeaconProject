package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.permissionx.guolindev.PermissionX;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class Police_MainActivity extends AppCompatActivity {

    private MaterialCardView btnMonitor, btnGuide, btnSafety, btnCheck;
    private ShapeableImageView btnSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_police);

        findView();
        initView();
        requestPermission();
        requestBluetooth();
    }

    private void findView() {
        btnMonitor = findViewById(R.id.btn_monitor);
        btnGuide = findViewById(R.id.btn_Guide);
        btnSafety = findViewById(R.id.btn_safety);
        btnCheck = findViewById(R.id.btn_check);
        btnSignOut = findViewById(R.id.btn_SignOut);
    }

    private void initView() {
        btnMonitor.setOnClickListener(v -> {

        });

        btnGuide.setOnClickListener(v -> {
            Intent ii = new Intent(getApplicationContext(), GuideActivity.class);
            startActivity(ii);
        });

        btnSafety.setOnClickListener(v -> {
            Intent ii = new Intent(getApplicationContext(), SafetyActivity.class);
            startActivity(ii);
        });

        btnCheck.setOnClickListener(v -> {
            Intent ii = new Intent(getApplicationContext(), CheckActivity.class);
            startActivity(ii);
        });

        btnSignOut.setOnClickListener(v -> finish());
    }

    /**
     * Request Permission...
     * by using implementation PermissionX
     */
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
}