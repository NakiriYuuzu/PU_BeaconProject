package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.permissionx.guolindev.PermissionX;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.Login_Auto;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RequestHelper;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData;

public class Police_MainActivity extends AppCompatActivity {

    private MaterialCardView btnMonitor, btnGuide, btnSafety, btnCheck;
    private MaterialTextView userNames;
    private ShapeableImageView btnSignOut;

    RequestHelper requestHelper;
    Login_Auto loginAuto;
    ShareData shareData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_police);

        viewInit();
        initData();
        requestPermission();
        roleCheck();
        btnInit();
    }

    private void viewInit() {
        btnMonitor = findViewById(R.id.btn_monitor);
        btnGuide = findViewById(R.id.btn_Guide);
        btnSafety = findViewById(R.id.btn_safety);
        btnCheck = findViewById(R.id.btn_check);
        btnSignOut = findViewById(R.id.btn_SignOut);
        userNames = findViewById(R.id.userNames);

        requestHelper = new RequestHelper(this);
        loginAuto = new Login_Auto(this);
        shareData = new ShareData(this);
    }

    private void initData() {
        if (!shareData.getNAME().equals("")) {
            userNames.setText(shareData.getNAME());
        }
    }

    private void roleCheck() {
        if (shareData.getROLE().equals("1") || shareData.getROLE().equals("3")) {
            btnMonitor.setVisibility(View.INVISIBLE);
        }
        else if (shareData.getROLE().equals("0")) {
            btnMonitor.setVisibility(View.INVISIBLE);
            btnSafety.setVisibility(View.INVISIBLE);
        }
    }

    private void btnInit() {
        btnMonitor.setOnClickListener(v -> {
            Intent ii = new Intent(getApplicationContext(), MonitorActivity.class);
            startActivity(ii);
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
            loginAuto.saveID("");
            loginAuto.savePassword("");
            shareData.saveNAME("");
            shareData.saveROLE("0");
            shareData.saveUID("");
            shareData.saveTOKEN("");
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