package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.permissionx.guolindev.PermissionX;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.PermissionRequest;

public class Police_MainActivity extends AppCompatActivity {

    private MaterialCardView btnMonitor, btnGuide, btnSafety, btnCheck;
    private ShapeableImageView btnSignOut;

    PermissionRequest permissionRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_police);

        viewInit();
        initView();
        requestPermission();
        permissionRequest.requestBluetooth(this);
    }

    private void viewInit() {
        btnMonitor = findViewById(R.id.btn_monitor);
        btnGuide = findViewById(R.id.btn_Guide);
        btnSafety = findViewById(R.id.btn_safety);
        btnCheck = findViewById(R.id.btn_check);
        btnSignOut = findViewById(R.id.btn_SignOut);

        permissionRequest = new PermissionRequest();
    }

    private void initView() {
        btnMonitor.setOnClickListener(v -> {
            Intent ii = new Intent(getApplicationContext(), MonitorActivity.class);
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

        btnSignOut.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();
            finish();
        });
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
}