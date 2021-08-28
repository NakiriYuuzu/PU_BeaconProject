package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class GuideSpotActivity extends AppCompatActivity {

    private String spotNames = "";

    private MaterialTextView tvName, tvGuideInfo;
    private ShapeableImageView btnBack, ivSpotImage;
    private MaterialButton btnUrl;

    VolleyApi volleyApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_spot);

        viewInit();
        buttonInit();
        getData();
    }

    private void getData() {
        volleyApi = new VolleyApi(this, "http://120.110.93.246/volleyTest/dat.php");
        volleyApi.get_API_GuideActivity(new VolleyApi.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("onSuccess", result);
                Log.e("spotNames", spotNames);

                ArrayList<JSONObject> jsonData = new ArrayList<>();

                try {
                    JSONArray allData = new JSONArray(result);

                    for (int i = 0; i < allData.length(); i++) {
                        JSONObject data = allData.getJSONObject(i);
                        jsonData.add(data);
                    }

                    for (int i = 0; i < jsonData.size(); i++) {
                        if (jsonData.get(i).getString("Name").equals(spotNames)) {
                            tvName.setText(jsonData.get(i).getString("Name"));
                            tvGuideInfo.setText(jsonData.get(i).getString("Info"));
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailed(VolleyError error) {
                Log.e("onFailed", error.toString());
            }
        });
    }

    private void viewInit() {
        tvName = findViewById(R.id.tvName);
        ivSpotImage = findViewById(R.id.ivSpotImage);
        btnUrl = findViewById(R.id.btnUrl);
        btnBack = findViewById(R.id.btn_check_back);
        tvGuideInfo = findViewById(R.id.tvGuideInfo);

        Intent ii = getIntent();
        if (ii != null)
            spotNames = ii.getStringExtra("spotName");
    }

    private void buttonInit() {
        btnBack.setOnClickListener(v -> finish());
    }
}