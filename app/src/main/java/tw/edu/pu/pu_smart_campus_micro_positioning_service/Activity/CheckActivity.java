package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Fragment.check_ChartFragment;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Fragment.check_RecycleViewFragment;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.ViewModel.Check_ViewModel;

public class CheckActivity extends AppCompatActivity {

    private boolean animationChecked = true;

    ShapeableImageView btnBack;
    MaterialTextView btnDetail, btnChart;

    LottieAnimationView animation;

    Fragment fragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);

        viewInit();
        btnInit();
        apiData();
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

    private void apiData() {
        Check_ViewModel check_viewModel = new Check_ViewModel();
        VolleyApi volleyApi = new VolleyApi(this, "http://120.110.93.246/CAMEFSC/public/api/people");

        volleyApi.get_API_CheckActivity(new VolleyApi.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    int id = 0, pplNum = 0;
                    String viewNames = "", dataTime = "";
                    JSONArray allData = new JSONArray(result);

                    for (int i = 0; i < allData.length(); i++) {
                        JSONObject data = allData.getJSONObject(i);
                        id = data.getInt("id");
                        viewNames = data.getString("ViewName");
                        pplNum = data.getInt("Peoplenumber");
                        dataTime = data.getString("LogTime");
                    }

                    Log.e("checkAPI-jsonParserData", id + viewNames + pplNum + dataTime);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                Log.e("checkAPI", error.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiData();
    }
}