package com.example.food4good;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.LatLng;
import com.google.type.LatLngOrBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ContributorListActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MyRecyclerViewAdapter.ItemClickListener ctx;
    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String latitude, longitude;

    Double distance;
    Location mylocation = new Location("");
    Location dest_location = new Location("");
    CollectionReference conRef = db.collection("Contributor");
    CollectionReference conRefFood = db.collection("AvailabilityFood");
    ArrayList<Contributor> clist;
    ArrayList<AvailableFood> olist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_contributor_list);
//        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.blue));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        ctx=this;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d("Location:", "provider not enabled");
            OnGPS();
        } else {
            Log.d("Location:", "provider enabled");
            getLocation();
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    //Location
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                ContributorListActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                ContributorListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                mylocation.setLatitude(lat);
                mylocation.setLongitude(longi);

                Log.d("Location: ","Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);

//                LatLng location = new LatLng(latitude, longitude);
            } else {
                Log.d("Location:","Unable to find location");
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }




    //Firebase pull
    @Override
    protected void onStart() {
        super.onStart();

        Log.d("Chinmay","Inside on start");

        SharedPreferences sh = getSharedPreferences("Requester", MODE_PRIVATE);
        String name = sh.getString("name", "");
        String phoneNumber = sh.getString("phoneNumber", "");
        longitude = sh.getString("longitude","");
        latitude = sh.getString("latitude","");

        Log.d("ContributorList",name);
        Log.d("ContributorList",phoneNumber);
        Log.d("ContributorList",longitude);
        Log.d("ContributorList",latitude);

        conRefFood.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {


                if(error!=null){
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    return;
                }
                olist = new ArrayList<>();
                for (QueryDocumentSnapshot documentSnapshot : value) {
                    AvailableFood c = documentSnapshot.toObject(AvailableFood.class);
                    if(c.getVegMeals()>0 || c.getNonVegMeals()>0) olist.add(c);
                    Log.d("Food", ""+olist.size());
                }
                Log.d("OnStart", String.valueOf(olist.size()));
                clist= new ArrayList<>();
                conRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Contributor c = documentSnapshot.toObject(Contributor.class);

                                clist.add(c);

                            }
                            Log.d("Contributor", clist.size()+"");
                            RecyclerView recyclerView = findViewById(R.id.contributorListRecycleView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            adapter = new MyRecyclerViewAdapter(getApplicationContext(), olist,clist, mylocation);
                            adapter.setClickListener(ContributorListActivity.this);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Log.d("onComplete", "Error getting documents: ", task.getException());
                        }
                    }
                });





            }
        });
    }


    @Override
    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Context context = view.getContext();
        Intent intent = new Intent(context , RequestFoodActivity.class);
        intent.putExtra("address",clist.get(position).getAddress());
        intent.putExtra("name",clist.get(position).getName());
        intent.putExtra("Id",clist.get(position).getId());
        intent.putExtra("nonvegmeals",olist.get(position).getNonVegMeals()+"");
        intent.putExtra("vegmeals",olist.get(position).getVegMeals()+"");
        intent.putExtra("photoUrl",clist.get(position).getPhotoUrl());
        intent.putExtra("latitude",clist.get(position).getLatitude());
        intent.putExtra("longitude",clist.get(position).getLongitude());

        System.out.println(clist.get(position)+" "+olist.get(position));
        this.startActivity(intent);
    }
}


//RecycleView population
class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<AvailableFood> mData2;
    private List<Contributor> mData;
    private LayoutInflater mInflater;
    private Context ctx;
    private ItemClickListener mClickListener;
    private Location myLoc= new Location("");
    private Location dest_location= new Location("");
    private static final DecimalFormat df = new DecimalFormat("0.00");


    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<AvailableFood> fdata,List<Contributor> cdata, Location myLocation) {
        this.mInflater = LayoutInflater.from(context);
        this.mData2 = fdata;
        this.mData=cdata;
        myLoc=myLocation;
        ctx=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.contributorlist_row, parent, false);
        return new ViewHolder(view);
    }



    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        for(int i=0;i<mData2.size();i++){
            System.out.println(mData.get(position).getId()+" "+mData2.get(i).getCid());
            if(mData.get(position).getId().equals(mData2.get(i).getCid())){
                System.out.println(mData2.get(i).getNonVegMeals()+" "+mData2.get(i).getVegMeals());
                if(mData2.get(i).getNonVegMeals()==0){

                    holder.redCircle.setVisibility(View.INVISIBLE);

                }
                if(mData2.get(i).getVegMeals()==0){

                    holder.greenCircle.setVisibility(View.INVISIBLE);

                }
                dest_location.setLatitude(Double.parseDouble(mData.get(position).getLatitude()));
                dest_location.setLongitude(Double.parseDouble(mData.get(position).getLongitude()));
                float distance = dest_location.distanceTo(myLoc);//in meters
                distance=distance/1000;
                String name = mData.get(position).getName();
                String address = mData.get(position).getAddress();
                holder.nameView.setText(name);
                holder.addressView.setText(address);
                holder.distanceView.setText(df.format(distance)+" km");
                Glide.with(ctx)
                        .load(mData.get(position).getPhotoUrl())
                        .centerCrop()
                        .into(holder.imgView);

                break;
            }
            else{

                //Pass

            }

        }

    }

    // total number of rows
    @Override
    public int getItemCount() {


        return mData2.size();
    }
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameView;
        TextView addressView;
        TextView distanceView;

        ImageView redCircle;
        ImageView greenCircle;
        ImageView imgView;


        ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.providerName);
            addressView = itemView.findViewById(R.id.providerAddress);
            distanceView=itemView.findViewById(R.id.distance);
            imgView=itemView.findViewById(R.id.icon);
            redCircle=itemView.findViewById(R.id.redCircle);
            greenCircle=itemView.findViewById(R.id.greenCircle);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id).getName();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}