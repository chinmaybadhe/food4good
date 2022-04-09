package com.example.food4good;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Contributor");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sh = getSharedPreferences("Requester", MODE_PRIVATE);
        String name = sh.getString("name", "");
        String phoneNumber = sh.getString("phoneNumber", "");

        if( name.equals("") || phoneNumber.equals(""))
        {
            Log.d("MainActivity","User data not found");
            Intent intent = new Intent(MainActivity.this, RequesterLogin.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Intent intent=new Intent(getApplicationContext(), ContributorListActivity.class);
            startActivity(intent);
            finish();
        }




        //Log.d("Got a count as", String.valueOf(count));
    }


}