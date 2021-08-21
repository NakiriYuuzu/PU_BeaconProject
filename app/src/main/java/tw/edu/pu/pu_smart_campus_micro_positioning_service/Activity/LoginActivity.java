package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;


import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RequestItem;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "Login Debug: ";

    private TextInputEditText etAcc, etPass;
    private MaterialButton btnLogin, btnGuest;
    private CheckBox btnRememberMe;

    RequestItem requestItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!(requestNetworkConnection())) {
            Toast.makeText(getApplicationContext(), "請打開網絡後，再打開APP.", Toast.LENGTH_SHORT).show();

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

    private boolean requestNetworkConnection() {
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

        requestItem = new RequestItem(this);
    }

    @SuppressLint("HardwareIds")
    private void loginFunction() {
        if (etAcc.getText() != null && etPass.getText() != null) {
            String user = etAcc.getText().toString();
            String pass = etPass.getText().toString();

            if (user.equals("") || pass.equals("")) {
                Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();

            } else {
                Log.e("Yuuzu", requestItem.requestIMEI());

                VolleyApi volleyApi = new VolleyApi(LoginActivity.this, "http://120.110.93.246/CAMEFSC1/public/api/login/user");

                volleyApi.post_API_Login(user, pass, new VolleyApi.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        try {
                            JSONObject jsonData = new JSONObject(result);
                            String token = jsonData.getString("token");
                            String users = jsonData.getString("name");
                            int role = jsonData.getInt("role");

                            Intent ii = new Intent(getApplicationContext(), Police_MainActivity.class);
                            ii.putExtra("tokens", token);
                            ii.putExtra("users", users);
                            ii.putExtra("role", role);

                            Toast.makeText(getApplicationContext(), "登入成功", Toast.LENGTH_SHORT).show();

                            startActivity(ii);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(VolleyError error) {
                        if (error.networkResponse.statusCode == 401) {
                            Toast.makeText(getApplicationContext(), "登入失敗，賬號密碼錯誤", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "連接伺服器失敗", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        } else {
            Toast.makeText(this, "Please enter your account and password!", Toast.LENGTH_SHORT).show();

        }
    }

    private void guestFunction() {
        VolleyApi volleyApi = new VolleyApi(this, "http://120.110.93.246/CAMEFSC1/public/api/login/tourist");
        volleyApi.post_API_Login_Guest(requestItem.requestIMEI(), new VolleyApi.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonData = null;
                try {
                    jsonData = new JSONObject(result);
                    String token = jsonData.getString("token");
                    String users = "tourist";

                    Intent ii = new Intent(getApplicationContext(), Police_MainActivity.class);
                    ii.putExtra("tokens", token);
                    ii.putExtra("users", users);

                    startActivity(ii);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                if (error.networkResponse.statusCode == 400) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "連接伺服器失敗", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}