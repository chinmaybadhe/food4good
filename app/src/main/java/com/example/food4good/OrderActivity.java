package com.example.food4good;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {


        RecyclerView rvContributorOrders;
        //Firebase database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference conRef = db.collection("Order");


        ArrayList<Order> alOrders;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_order);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
            ActionBar actionBar = getSupportActionBar();
            actionBar.hide();

            rvContributorOrders = findViewById(R.id.rvContributorOrders);
            alOrders = new ArrayList<>();


            Log.d("Orders","Before rendering:"+alOrders.size());


        }

        @Override
        protected void onStart() {
            super.onStart();

            OrdersAdapter ordersAdapter = new OrdersAdapter(alOrders,OrderActivity.this);
            rvContributorOrders.setAdapter(ordersAdapter);
            rvContributorOrders.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
            SharedPreferences sharedPreferences = getSharedPreferences("Requester",MODE_PRIVATE);
            String r_id=sharedPreferences.getString("phoneNumber","john");

            Log.d("Orders","Inside onStart");
            conRef.whereEqualTo("r_id",r_id).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error!=null){
                        Toast.makeText(OrderActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                    alOrders.clear();
                    for(QueryDocumentSnapshot qds : value){
                        Order o = qds.toObject(Order.class);
                        alOrders.add(o);
                        ordersAdapter.notifyDataSetChanged();
                    }
                    Log.d("Start", "Inside onStart size"+String.valueOf(alOrders.size()));


                }
            });


        }
    }