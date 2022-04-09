package com.example.food4good;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RequesterLogin extends AppCompatActivity {


    Button btnRequesterSignIn;
    EditText etName,etPhoneNumber;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference ref = db.collection("Requester");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requester_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();



        btnRequesterSignIn = findViewById(R.id.btnRequesterSignIn);
        etName = findViewById(R.id.etName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        btnRequesterSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String phoneNumber = etPhoneNumber.getText().toString();

                if("".equals(name) || "".equals(phoneNumber))
                {
                    Toast.makeText(RequesterLogin.this, "Both are mandatory field", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(phoneNumber.length()!=10)
                {
                    Toast.makeText(RequesterLogin.this, "Phone number should be of 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                Requester r = new Requester(phoneNumber,name,0.0,0);
                ref.document(r.getPhoneNumber()).set(r).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(RequesterLogin.this, "Your account has been created successfully!", Toast.LENGTH_SHORT).show();
                        //Shared Preferences
                        SharedPreferences sharedPreferences = getSharedPreferences("Requester",MODE_PRIVATE);

                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                        myEdit.putString("name", r.getName());
                        myEdit.putString("phoneNumber",r.getPhoneNumber() );
                        myEdit.commit();

                        Intent intent = new Intent(RequesterLogin.this, ContributorListActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RequesterLogin.this, "Failed while creating account", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
}