package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class CheckActivity extends AppCompatActivity {

    private MaterialButton btnPost, btnGet;
    private ShapeableImageView btnBack;
    private MaterialTextView tvTest;

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
        btnPost = findViewById(R.id.btn_POST);
        btnGet = findViewById(R.id.btn_GET);
        tvTest = findViewById(R.id.tv_CHECK_Test);
    }

    private void btnInit() {
        btnBack.setOnClickListener(v -> finish());

        btnPost.setOnClickListener(v -> {
            volleyApi = new VolleyApi(CheckActivity.this, "https://reqres.in/api/users");
            volleyApi.post_API_TESTING(tvTest);
        });

        btnGet.setOnClickListener(v -> {
            volleyApi = new VolleyApi(CheckActivity.this, "https://reqres.in/api/users?page=2");
            volleyApi.get_API_TESTING(tvTest);
        });
    }
}