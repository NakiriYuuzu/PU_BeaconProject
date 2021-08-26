package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.imageview.ShapeableImageView;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class MonitorActivity extends AppCompatActivity {

    ShapeableImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        initVIew();
        initButton();
    }

    private void initButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void initVIew() {
        btnBack = findViewById(R.id.btn_monitor_back);
    }
}