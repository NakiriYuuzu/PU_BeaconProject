package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;


import tw.edu.pu.pu_smart_campus_micro_positioning_service.ApiConnect.VolleyApi;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.Login_Auto;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.RequestHelper;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = "Login Debug: ";

    private boolean userLoginChecked = false;
    private boolean guestLoginChecked = false;

    private TextInputEditText etAcc, etPass;
    private MaterialButton btnLogin, btnGuest;
    private CheckBox btnRememberMe;

    RequestHelper requestHelper;
    Login_Auto loginAuto;
    ShareData shareData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!(requestNetworkConnection())) {
            Toast.makeText(getApplicationContext(), "請打開網絡後，再打開APP.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(this::finish, 3500);

        } else {
            viewInit();
            dataInit();
            buttonInit();
            loginInit();
        }
    }

    private boolean requestNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void loginInit() {
        Log.e(TAG + "used", loginAuto.getID() + loginAuto.getPassword());

        if (loginAuto.getID() != null && loginAuto.getPassword() != null) {
            if (!loginAuto.getID().equals("") && !loginAuto.getPassword().equals("")) {
                autoLoginFunction();
            }
        }
    }

    private void dataInit() {
        shareData = new ShareData(this);
        shareData.saveUID("");
        shareData.saveNAME("");
        shareData.saveROLE("0");
        shareData.saveTOKEN("");
    }

    private void buttonInit() {
        btnLogin.setOnClickListener(v -> {
            if (!userLoginChecked) {
                userLoginChecked = true;
                loginFunction();
            }
        });

        btnGuest.setOnClickListener(v -> {
            if (!guestLoginChecked) {
                guestLoginChecked = true;
                guestFunction();
            }
        });
    }

    private void viewInit() {
        etAcc = findViewById(R.id.etAcc);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnGuest = findViewById(R.id.btn_Guest);
        btnRememberMe = findViewById(R.id.checkBox_rememberMe);

        requestHelper = new RequestHelper(this);
        loginAuto = new Login_Auto(this);
    }

    private void guestFunction() {
        VolleyApi volleyApi = new VolleyApi(this, "http://120.110.93.246/CAMEFSC1/public/api/login/tourist");
        volleyApi.post_API_Login_Guest(requestHelper.requestIMEI(), new VolleyApi.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.e("guest_success", result);

                try {
                    JSONObject jsonData = new JSONObject(result);
                    shareData.saveTOKEN(jsonData.getString("token"));

                    Intent ii = new Intent(getApplicationContext(), Police_MainActivity.class);

                    guestLoginChecked = false;

                    startActivity(ii);

                } catch (JSONException e) {
                    e.printStackTrace();
                    guestLoginChecked = false;
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                try {
                    Toast.makeText(getApplicationContext(), "認證失敗...", Toast.LENGTH_SHORT).show();
                    guestLoginChecked = false;

                } catch (Exception e) {
                    e.printStackTrace();
                    guestLoginChecked = false;
                }
            }
        });
    }

    private void loginFunction() {
        if (etAcc.getText() != null && etPass.getText() != null) {
            String user = etAcc.getText().toString();
            String pass = etPass.getText().toString();

            if (user.equals("") || pass.equals("")) {
                Toast.makeText(getApplicationContext(), "請輸入賬號與密碼!", Toast.LENGTH_SHORT).show();

            } else {
                VolleyApi volleyApi = new VolleyApi(LoginActivity.this, "http://120.110.93.246/CAMEFSC1/public/api/login/user");

                volleyApi.post_API_Login(user, pass, new VolleyApi.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e(TAG, result);

                        if (btnRememberMe.isChecked()) {
                            loginAuto.saveID(user);
                            loginAuto.savePassword(pass);
                            Log.e(TAG + "save", loginAuto.getID() + loginAuto.getPassword());
                        }

                        try {
                            JSONObject jsonData = new JSONObject(result);
                            shareData.saveUID(jsonData.getString("uid"));
                            shareData.saveNAME(jsonData.getString("name"));
                            shareData.saveROLE(String.valueOf(jsonData.getInt("role")));
                            shareData.saveTOKEN(jsonData.getString("token"));

                            Toast.makeText(getApplicationContext(), "登入成功", Toast.LENGTH_SHORT).show();

                            userLoginChecked = false;
                            clearFunction();

                            Intent ii = new Intent(getApplicationContext(), Police_MainActivity.class);
                            startActivity(ii);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            userLoginChecked = false;
                        }
                    }

                    @Override
                    public void onFailed(VolleyError error) {
                        try {
                            if (error.networkResponse.statusCode == 401) {
                                Toast.makeText(getApplicationContext(), "登入失敗，賬號密碼錯誤", Toast.LENGTH_SHORT).show();
                                userLoginChecked = false;
                            } else {
                                error.printStackTrace();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "連接伺服器失敗", Toast.LENGTH_SHORT).show();
                            userLoginChecked = false;
                        }
                    }
                });
            }
        }
    }

    private void autoLoginFunction() {
        try {
            String id = loginAuto.getID();
            String pass = loginAuto.getPassword();


            VolleyApi volleyApi = new VolleyApi(LoginActivity.this, "http://120.110.93.246/CAMEFSC1/public/api/login/user");

            volleyApi.post_API_Login(id, pass, new VolleyApi.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    Log.e(TAG, result);

                    try {
                        JSONObject jsonData = new JSONObject(result);

                        shareData.saveUID(jsonData.getString("uid"));
                        shareData.saveNAME(jsonData.getString("name"));
                        shareData.saveROLE(String.valueOf(jsonData.getInt("role")));
                        shareData.saveTOKEN(jsonData.getString("token"));

                        Toast.makeText(getApplicationContext(), "登入成功", Toast.LENGTH_SHORT).show();

                        Intent ii = new Intent(getApplicationContext(), Police_MainActivity.class);
                        startActivity(ii);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(VolleyError error) {
                    try {
                        loginAuto.saveID("");
                        loginAuto.savePassword("");
                        Toast.makeText(getApplicationContext(), "自動登入失敗，請重新登入...", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFunction() {
        etAcc.setText("");
        etPass.setText("");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(requestNetworkConnection())) {
            Toast.makeText(getApplicationContext(), "請打開網絡後，再打開APP.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(this::finish, 3500);
        }
    }
}