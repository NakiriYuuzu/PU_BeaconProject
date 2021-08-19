package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;


import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "Login Debug: ";

    public static String deviceIMEI;

    private String status;

    private TextInputEditText etAcc, etPass;
    private MaterialButton btnLogin, btnGuest;
    private CheckBox btnRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!isNetworkConnected()) {
            Toast.makeText(getApplicationContext(), "請打開網絡后，在打開APP.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 3500);

        } else {
            viewInit();
            buttonInit();
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void buttonInit() {
        btnLogin.setOnClickListener(v -> loginFunction());

        btnGuest.setOnClickListener(v -> guestFunction());

        SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
        String checkBox = preferences.getString("remember", "");

        if (checkBox.equals("true")) {
            Toast.makeText(getApplicationContext(), "Sign in Successfully!", Toast.LENGTH_SHORT).show();
            Intent ii = new Intent(getApplicationContext(), Police_MainActivity.class);
            startActivity(ii);

        } else if (checkBox.equals("false")) {
            Toast.makeText(getApplicationContext(), "Please Sign In.", Toast.LENGTH_SHORT).show();
        }

        btnRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();

                    Log.e("checkBox", "checked.");

                } else if (!buttonView.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();

                    Log.e("checkBox", "unchecked.");
                }
            }
        });
    }

    private void viewInit() {
        etAcc = findViewById(R.id.etAcc);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnGuest = findViewById(R.id.btn_Guest);
        btnRememberMe = findViewById(R.id.checkBox_rememberMe);
    }

    @SuppressLint("HardwareIds")
    private void loginFunction() {
        if (etAcc.getText() != null && etPass.getText() != null) {
            String user = etAcc.getText().toString();
            String pass = etPass.getText().toString();

            if (user.equals("") || pass.equals("")) {
                Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();

            } else {
                //getIMEI
                deviceIMEI = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.e("Yuuzu", deviceIMEI);

                VolleyApi volleyApi = new VolleyApi(LoginActivity.this, "https://reqbin.com/echo/post/json");

                volleyApi.post_API_Login(user, pass, deviceIMEI, new VolleyApi.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject requestStatus = new JSONObject(result);
                            status = requestStatus.getString("success");

                            if (status.equals("true")) {
                                Toast.makeText(getApplicationContext(), "Sign in Successfully!", Toast.LENGTH_SHORT).show();
                                Intent ii = new Intent(getApplicationContext(), Police_MainActivity.class);
                                ii.putExtra("ID", user);
                                startActivity(ii);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        } else {
            Toast.makeText(this, "Please enter your account and password!", Toast.LENGTH_SHORT).show();

        }
    }

    private void guestFunction() {
        Intent ii = new Intent(getApplicationContext(), Police_MainActivity.class);
        startActivity(ii);
    }

    private void loginApiData(VolleyApi.VolleyCallback volleyCallback) {

    }
}