package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData;

public class GuideSpotActivity extends AppCompatActivity {

    private String spotName = "";
    private String spotNote = "";
    private String spotImage = "";
    private String spotUrl = "";

    private MaterialTextView tvName, tvGuideInfo;
    private ShapeableImageView btnBack, ivSpotImage;
    private MaterialButton btnUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_spot);

        viewInit();
        dataInit();
        buttonInit();
    }

    private void dataInit() {
        ShareData shareData = new ShareData(this);
        if (!shareData.getSpotTitle().equals("") && !shareData.getSpotInfo().equals("") && !shareData.getSpotImage().equals("") && !shareData.getSpotUrl().equals("")) {
            spotName = shareData.getSpotTitle();
            spotNote = shareData.getSpotInfo();
            spotImage = shareData.getSpotImage();
            spotUrl = shareData.getSpotUrl();
        }

        if (!spotName.equals("") && !spotNote.equals("") && !spotImage.equals("")) {
            tvName.setText(spotName);
            tvGuideInfo.setText(spotNote);
            Log.e("imageLoader", spotImage);
            Glide.with(this).load(spotImage).into(ivSpotImage);
        }
    }

    private void viewInit() {
        tvName = findViewById(R.id.tvName);
        ivSpotImage = findViewById(R.id.ivSpotImage);
        btnUrl = findViewById(R.id.btnUrl);
        btnBack = findViewById(R.id.btn_SpotInfo_back);
        tvGuideInfo = findViewById(R.id.tvGuideInfo);
    }

    private void buttonInit() {
        btnBack.setOnClickListener(v -> finish());

        btnUrl.setOnClickListener(v -> {
            if (!spotUrl.equals("")) {
                Intent ii = new Intent(getApplicationContext(), GuideSpotUrlActivity.class);
                startActivity(ii);
            }
        });
    }
}