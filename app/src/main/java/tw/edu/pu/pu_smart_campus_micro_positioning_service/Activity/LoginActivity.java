package tw.edu.pu.pu_smart_campus_micro_positioning_service.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.Database.DBHelper;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etAcc, etPass;
    private MaterialButton btnLogin, btnRegister;

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
            }, 3000);

        }
        else {
            findView();
            buttonInit();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void buttonInit() {
        btnLogin.setOnClickListener(v -> loginFunction());

        btnRegister.setOnClickListener(v -> guestFunction());
    }

    private void findView() {
        etAcc = findViewById(R.id.etAcc);
        etPass = findViewById(R.id.etPass);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btn_Guest);
    }

    private void loginFunction() {
        if (etAcc.getText() != null && etPass.getText() != null)
        {
            String user = etAcc.getText().toString();
            String pass = etPass.getText().toString();

            if (user.equals("") || pass.equals("")) {
                Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();

            } else {

                if (user.equals(pass)) {
                    Toast.makeText(this, "Sign in Successfully!", Toast.LENGTH_SHORT).show();
                    Intent ii = new Intent(getApplicationContext(), Police_MainActivity.class);
                    ii.putExtra("ID", user);
                    startActivity(ii);
                }
            }

        } else {
            Toast.makeText(this, "Please enter your account and password!", Toast.LENGTH_SHORT).show();
            clearFunction();
        }
    }

    private void clearFunction() {
        etAcc.setText("");
        etPass.setText("");
    }

    private void guestFunction() {
        Intent ii = new Intent(getApplicationContext(), Police_MainActivity.class);
        startActivity(ii);
    }
}