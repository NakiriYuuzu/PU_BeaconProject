package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.Database.DBHelper;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class GuideSpotActivity extends AppCompatActivity {

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
        createInformation();
    }

    private void getData() {
        volleyApi = new VolleyApi(this, "https://nakiriyuuzu.github.io/volleyTest/dat.php");
        volleyApi.get_API_GuideActivity(new VolleyApi.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createInformation() {
        DBHelper DB = new DBHelper(this);
        String [] spotData;

        Bundle bundle = getIntent().getExtras();
        String str = bundle.getString("Key");


        spotData = DB.fetchSpotData(str);
        if(spotData.length > 0){

            tvName.setText(spotData[0]);
            tvGuideInfo.setText(spotData[1]);
            if(str.equals("主顧聖母堂")) {
                ivSpotImage.setImageResource(R.drawable.providence_chapel);
            }
            else if(str.equals("主顧樓")){
                ivSpotImage.setImageResource(R.drawable.providence_hall);
            }
            else if(str.equals("若望保祿二世體育館")){
                ivSpotImage.setImageResource(R.drawable.sport_hall);
            }

            btnUrl.setOnClickListener(v -> {
                Uri uri = Uri.parse(spotData[2]);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            });
        }
    }

    private void viewInit() {
        tvName = findViewById(R.id.tvName);
        ivSpotImage = findViewById(R.id.ivSpotImage);
        btnUrl = findViewById(R.id.btnUrl);
        btnBack = findViewById(R.id.btn_check_back);
        tvGuideInfo = findViewById(R.id.tvGuideInfo);
    }

    private void buttonInit() {
        btnBack.setOnClickListener(v -> finish());
    }
}