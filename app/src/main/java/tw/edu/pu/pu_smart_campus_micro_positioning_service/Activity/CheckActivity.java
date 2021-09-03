package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.Timer;
import java.util.TimerTask;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.DefaultSetting;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Fragment.check_ChartFragment;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Fragment.check_RecycleViewFragment;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData;

public class CheckActivity extends AppCompatActivity {

    private boolean animationChecked = true;

    ShapeableImageView btnBack;
    MaterialTextView btnDetail, btnChart;

    LottieAnimationView animation;

    Fragment fragment = null;
    ShareData shareData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        viewInit();
        btnInit();
        timer.schedule(timerTask, 10000, 10000);
    }

    private void viewInit() {
        btnBack = findViewById(R.id.btn_SpotInfo_back);
        btnDetail = findViewById(R.id.btn_check_detail);
        btnChart = findViewById(R.id.btn_check_chart);

        animation = findViewById(R.id.check_animation);

        shareData = new ShareData(this);
    }

    private void btnInit() {
        btnBack.setOnClickListener(v -> finish());

        btnDetail.setOnClickListener(v -> {
            btnDetailAnimation();
            try {
                fragment = new check_RecycleViewFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.NavHostFragment, new check_RecycleViewFragment()).commit();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error..." + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        btnChart.setOnClickListener(v -> {
            btnChartAnimation();
            try {
                fragment = new check_ChartFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.NavHostFragment, new check_ChartFragment()).commit();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error..." + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void btnDetailAnimation() {
        if (!animationChecked) {
            animation.setMinAndMaxProgress(0.5f, 1f);
            animation.playAnimation();
            animationChecked = true;
        }
    }

    private void btnChartAnimation() {
        if (animationChecked) {
            animation.setMinAndMaxProgress(0, 0.5f);
            animation.playAnimation();
            animationChecked = false;
        }
    }

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            apiData();
        }
    };

    private void apiData() {
        VolleyApi volleyApi = new VolleyApi(this, DefaultSetting.API_CHECK);
        volleyApi.get_API_CheckActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.purge();
        timer.cancel();
    }
}