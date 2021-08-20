package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.Database.DBHelper;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class GuideSpotActivity extends AppCompatActivity {


    private MaterialTextView tvName, tvGuideInfo;
    private ShapeableImageView btnBack, ivSpotImage;
    private MaterialButton btnUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_spot);

        findView();
        buttonInit();
        createInformation();
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
            if(str == "主顧聖母堂") {
                ivSpotImage.setImageResource(R.drawable.providence_chapel);
            }
            else if(str == "主顧樓"){
                ivSpotImage.setImageResource(R.drawable.providence_hall);
            }
            else if(str == "若望保祿二世體育館"){
                ivSpotImage.setImageResource(R.drawable.sport_hall);
            }

            btnUrl.setOnClickListener(v -> {
                Uri uri = Uri.parse(spotData[2]);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            });
        }
    }

    private void findView() {
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