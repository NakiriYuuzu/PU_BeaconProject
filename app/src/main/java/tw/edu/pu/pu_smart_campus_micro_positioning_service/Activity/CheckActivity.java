package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Fragment.check_ChartFragment;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Fragment.check_RecycleViewFragment;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class CheckActivity extends AppCompatActivity {

    private boolean animationChecked = true;

    ShapeableImageView btnBack;
    BottomNavigationView btnNav;
    MaterialTextView btnDetail, btnChart;

    LottieAnimationView animation;

    Fragment fragment = null;

    VolleyApi volleyApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        viewInit();
        btnInit();
    }

    private void viewInit() {
        btnBack = findViewById(R.id.btn_check_back);
        btnDetail = findViewById(R.id.btn_check_detail);
        btnChart = findViewById(R.id.btn_check_chart);

        animation = findViewById(R.id.check_animation);
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
}