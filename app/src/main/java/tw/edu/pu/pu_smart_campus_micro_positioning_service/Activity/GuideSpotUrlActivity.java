package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.imageview.ShapeableImageView;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData;

public class GuideSpotUrlActivity extends AppCompatActivity {

    private WebView webView;
    private ShapeableImageView btnBack;

    private ShareData shareData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_spot_url);

        initView();
        initButton();
        initWebView();
    }

    private void initButton() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void initWebView() {
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(shareData.getSpotUrl());
    }

    private void initView() {
        webView = findViewById(R.id.spotUrl_web);
        btnBack = findViewById(R.id.btn_spotUrl_back);

        shareData = new ShareData(this);
    }
}