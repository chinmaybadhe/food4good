package com.example.food4good;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.TextView;

public class OTPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Bundle extras = getIntent().getExtras();
        String otp=null;
        if (extras != null) {
            otp = extras.getString("otp");

        }
        TextView tv=findViewById(R.id.otp);
        tv.setText(otp);
    }
}