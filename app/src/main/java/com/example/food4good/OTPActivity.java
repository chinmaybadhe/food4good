package com.example.food4good;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class OTPActivity extends AppCompatActivity {

    String o_id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference orRef = db.collection("Order");
    TextView tv;

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
            //o_id = extras.getString("o_id");

        }
        tv=findViewById(R.id.otp);
        tv.setText(otp);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String o_id = getIntent().getStringExtra("o_id");
        orRef.document(o_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Order o = value.toObject(Order.class);
                if(o.getStatus().equals("closed"))
                {
                    Toast.makeText(OTPActivity.this, "Order closed sucessfully!", Toast.LENGTH_SHORT).show();
                    tv.setText("DONE");
                }
                else
                {
                    Log.d("OTPActivity","order is open");
                }
            }
        });
    }
}