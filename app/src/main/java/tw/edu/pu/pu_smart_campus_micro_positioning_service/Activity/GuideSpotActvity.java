package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.textview.MaterialTextView;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.Database.DBHelper;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class GuideSpotActvity extends AppCompatActivity {


    private MaterialTextView tvName, tvGuideInfo;
    private ImageView ivSpotImage;
    private Button btnUrl, btnBack;

    private DBHelper DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_spot);

        findView();
        buttonInit();
        createInformation();
    }

    private void createInformation() {
        DB = new DBHelper(this);
        String arr[] = new String[3];
        String finalArr[];

        Bundle bundle = getIntent().getExtras();
        if (bundle.getBoolean("spot01")) {

            arr = DB.fetchSpotData("主顧聖母堂");

            if (arr.length > 0) {

                tvName.setText(arr[0]);
                tvGuideInfo.setText(arr[1]);
                ivSpotImage.setImageResource(R.drawable.providence_chapel);

                finalArr = arr;
                btnUrl.setOnClickListener(v -> {
                    Uri uri = Uri.parse(finalArr[2]);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
            }

        } else if (bundle.getBoolean("spot02")) {

            arr = DB.fetchSpotData("主顧樓");

            if (arr.length > 0) {

                tvName.setText(arr[0]);
                tvGuideInfo.setText(arr[1]);
                ivSpotImage.setImageResource(R.drawable.providence_hall);

                finalArr = arr;
                btnUrl.setOnClickListener(v -> {
                    Uri uri = Uri.parse(finalArr[2]);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
            }

        } else if (bundle.getBoolean("spot03")) {
            arr = DB.fetchSpotData("若望保祿二世體育館");

            if (arr.length > 0) {

                tvName.setText(arr[0]);
                tvGuideInfo.setText(arr[1]);
                ivSpotImage.setImageResource(R.drawable.sport_hall);

                finalArr = arr;
                btnUrl.setOnClickListener(v -> {
                    Uri uri = Uri.parse(finalArr[2]);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                });
            }
        }
    }

    private void findView() {
        tvName = findViewById(R.id.tvName);
        ivSpotImage = findViewById(R.id.ivSpotImage);
        btnUrl = findViewById(R.id.btnUrl);
        btnBack = findViewById(R.id.btnBack);
        tvGuideInfo = findViewById(R.id.tvGuideInfo);
    }

    private void buttonInit() {
        btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}