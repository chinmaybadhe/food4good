package com.example.food4good;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;
import java.util.UUID;

public class RequestFoodActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference conRef = db.collection("Order");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_food);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        Bundle extras = getIntent().getExtras();
        String photoUrl="";
        String providerName="";
        String providerAddress="";
        String veg_Count="";
        String nvegCount="";
        String id=null,latitude = null,longitude=null;
        ImageView imgView,map;
        TextView name, address, vegCount, nonvegCount;
        Switch simpleSwitch = (Switch) findViewById(R.id.simpleSwitch);
        Button btn;
        int vegQuantity=1;
        int nonvegQuantity=1;

        if (extras != null) {
            photoUrl = extras.getString("photoUrl");
            providerAddress = extras.getString("address");
            providerName = extras.getString("name");
            veg_Count = extras.getString("vegmeals")+"";
            nvegCount= extras.getString("nonvegmeals")+"";
            id= extras.getString("Id");
            latitude=extras.getString("latitude");
            longitude=extras.getString("longitude");
        }
        System.out.println(veg_Count);
        imgView=findViewById(R.id.image);
        name=findViewById(R.id.providerName);
        address=findViewById(R.id.providerAddress);
        vegCount=findViewById(R.id.veg);
        nonvegCount=findViewById(R.id.nonveg);
        map=findViewById(R.id.map);
        btn=findViewById(R.id.request);


        name.setText(providerName);
        address.setText(providerAddress);
        vegCount.setText(veg_Count);
        nonvegCount.setText(nvegCount);
        String finalLatitude = latitude;
        String finalLongitude = longitude;
        String finalId = id;
        Random r = new Random();
        int i1 = r.nextInt(8999) + 1000;
        String otp=i1+"";;
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "geo:" + finalLatitude + ","
                        +finalLongitude + "?q=" + finalLatitude
                        + "," + finalLongitude;
                startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse(uri)));
            }
        });

        String finalNvegCount = nvegCount;
        String finalVeg_Count = veg_Count;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(simpleSwitch.isChecked()==false && Integer.parseInt(finalNvegCount)==0){
                    Toast.makeText(RequestFoodActivity.this, "Sorry! Non-Veg meals are not available at this time", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(simpleSwitch.isChecked() && Integer.parseInt(finalVeg_Count)==0){
                    Toast.makeText(RequestFoodActivity.this, "Sorry! Veg meals are not available at this time", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(RequestFoodActivity.this)
                        .setTitle("Confirm order")
                        .setMessage("Do want to confirm this request?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                SharedPreferences sharedPreferences = getSharedPreferences("Requester",MODE_PRIVATE);
                                String requesterName=sharedPreferences.getString("name","john");
                                String requesterPhone=sharedPreferences.getString("phoneNumber","john");

                                String order_id= UUID.randomUUID().toString().substring(0,6);
                                Order order=new Order();
                                order.setLatitude(finalLatitude);
                                order.setLongitude(finalLongitude);
                                order.setC_id(finalId);
                                order.setRequesterName(requesterName);
                                order.setR_id(requesterPhone);
                                order.setStatus("open");
                                order.setO_id(order_id);
                                if(simpleSwitch.isChecked()){
                                    order.setNv_qty(0);
                                    order.setV_qty(1);
                                }
                                else{
                                    order.setNv_qty(1);
                                    order.setV_qty(0);

                                }

                                order.setOtp(otp);
                                conRef.document(order_id)
                                        .set(order)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
//                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                                if(simpleSwitch.isChecked()) db.collection("AvailabilityFood").document(finalId).update("vegMeals",Integer.parseInt(finalVeg_Count)-1);
                                                else db.collection("AvailabilityFood").document(finalId).update("nonVegMeals",Integer.parseInt(finalNvegCount)-1);
                                                Context context = view.getContext();
                                                Intent intent = new Intent(context , OTPActivity.class);
                                                intent.putExtra("otp",otp);
                                                intent.putExtra("o_id",order_id);
                                                finish();
                                                startActivity(intent);
                                                Toast.makeText(RequestFoodActivity.this, "Request placed!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
//                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });


                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });


        Glide.with(this)
                .load(photoUrl)
                .centerCrop()
                .into(imgView);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

    }



}