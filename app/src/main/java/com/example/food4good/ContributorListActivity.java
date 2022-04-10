package com.example.food4good;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

public class ContributorListActivity extends AppCompatActivity{

    MyRecyclerViewAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //MyRecyclerViewAdapter.ItemClickListener ctx;
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

    //dev
    RecyclerView contributorListRecycleView;
    ArrayList<AvailableFood> alAF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        contributorListRecycleView = findViewById(R.id.contributorListRecycleView);
        alAF = new ArrayList<>();

        setContentView(R.layout.activity_contributor_list);
//        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.blue));
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        //ctx=this;
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

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        //double height = el1 - el2;

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    public void sort(ArrayList<Contributor> clist, ArrayList<AvailableFood> olist, String longitude, String latitude)
    {
        double lon = Double.parseDouble(longitude);
        double lat = Double.parseDouble(latitude);

        //remove contributor if it is not listed in olist
        Log.d("SORT", String.valueOf(clist.size()));
        Log.d("SORT", String.valueOf(olist.size()));

        TreeMap<Double,Contributor> tm = new TreeMap<>();
        for(Contributor c: clist)
        {
            Log.d("SORT",c.getId());
            double dis = distance(lat,Double.parseDouble(c.getLatitude()),lon,Double.parseDouble(c.getLongitude()));
            while(tm.containsKey(dis))
                dis += 0.01;
            tm.put(dis,c);
        }
        clist.clear();
        for(Double d: tm.keySet())
            clist.add(tm.get(d));
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

        MyRecyclerViewAdapter afAdapter = new MyRecyclerViewAdapter(alAF,ContributorListActivity.this);
        contributorListRecycleView = findViewById(R.id.contributorListRecycleView);
        contributorListRecycleView.setAdapter(afAdapter);
        contributorListRecycleView.setLayoutManager(new LinearLayoutManager(ContributorListActivity.this));

        conRefFood.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    //
                }
                else
                {
                    alAF.clear();
                    for(QueryDocumentSnapshot documentSnapshot: value)
                    {
                        AvailableFood af = documentSnapshot.toObject(AvailableFood.class);
                        alAF.add(af);
                        afAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }


//    @Override
//    public void onItemClick(View view, int position) {
////        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
//        Context context = view.getContext();
//        Intent intent = new Intent(context , RequestFoodActivity.class);
//        intent.putExtra("address",clist.get(position).getAddress());
//        intent.putExtra("name",clist.get(position).getName());
//        intent.putExtra("Id",clist.get(position).getId());
//        intent.putExtra("nonvegmeals",olist.get(position).getNonVegMeals()+"");
//        intent.putExtra("vegmeals",olist.get(position).getVegMeals()+"");
//        intent.putExtra("photoUrl",clist.get(position).getPhotoUrl());
//        intent.putExtra("latitude",clist.get(position).getLatitude());
//        intent.putExtra("longitude",clist.get(position).getLongitude());
//
//        System.out.println(clist.get(position)+" "+olist.get(position));
//        this.startActivity(intent);
//    }
}


//RecycleView population
class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<AvailableFood> mData2;
    private List<Contributor> mData;
    private LayoutInflater mInflater;
    private Context ctx;
    //private ItemClickListener mClickListener;
    private Location myLoc= new Location("");
    private Location dest_location= new Location("");
    private static final DecimalFormat df = new DecimalFormat("0.00");

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference conRef = db.collection("Contributor");

    ArrayList<AvailableFood> alAF;


    // data is passed into the constructor
    MyRecyclerViewAdapter(ArrayList<AvailableFood> alAF, Context context) {
        this.alAF = alAF;
        ctx=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.contributorlist_row,parent,false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return  viewHolder;
    }



    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        AvailableFood af = alAF.get(position);

        String c_id = af.getCid();
        final Contributor[] outer_c = new Contributor[1];
        conRef.document(c_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null && value.exists())
                {
                    Contributor c = value.toObject(Contributor.class);
                    outer_c[0] = c;
                    holder.nameView.setText(c.getName());
                    holder.addressView.setText(c.getAddress());
                    dest_location.setLatitude(Double.parseDouble(c.getLatitude()));
                    dest_location.setLongitude(Double.parseDouble(c.getLongitude()));
                    float distance = dest_location.distanceTo(myLoc);//in meters
                    distance=distance/1000;

                    holder.distanceView.setText(distance+ " in mi.");

                    Glide.with(ctx)
                        .load(c.getPhotoUrl())
                        .centerCrop()
                        .into(holder.imgView);

                    if(af.getNonVegMeals()==0){

                        holder.redCircle.setVisibility(View.INVISIBLE);

                    }
                    if(af.getVegMeals()==0){

                        holder.greenCircle.setVisibility(View.INVISIBLE);

                    }
                }
            }
        });


        holder.cvContributorOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ctx, "Time pass", Toast.LENGTH_SHORT).show();
                ////        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
        Context context = view.getContext();
        Intent intent = new Intent(context , RequestFoodActivity.class);
        intent.putExtra("address",holder.addressView.getText().toString());
        intent.putExtra("name",holder.nameView.getText().toString());
        intent.putExtra("Id",c_id);
        intent.putExtra("nonvegmeals",""+af.getNonVegMeals());
        intent.putExtra("vegmeals",""+af.getVegMeals());
        intent.putExtra("photoUrl",outer_c[0].getPhotoUrl());
        intent.putExtra("latitude",outer_c[0].getLatitude());
        intent.putExtra("longitude",outer_c[0].getLongitude());

        ctx.startActivity(intent);
            }
        });

//        for(int i=0;i<mData2.size();i++){
//            System.out.println(mData.get(position).getId()+" "+mData2.get(i).getCid());
//            if(mData.get(position).getId().equals(mData2.get(i).getCid())){
//                System.out.println(mData2.get(i).getNonVegMeals()+" "+mData2.get(i).getVegMeals());
//                if(mData2.get(i).getNonVegMeals()==0){
//
//                    holder.redCircle.setVisibility(View.INVISIBLE);
//
//                }
//                if(mData2.get(i).getVegMeals()==0){
//
//                    holder.greenCircle.setVisibility(View.INVISIBLE);
//
//                }
//                dest_location.setLatitude(Double.parseDouble(mData.get(position).getLatitude()));
//                dest_location.setLongitude(Double.parseDouble(mData.get(position).getLongitude()));
//                float distance = dest_location.distanceTo(myLoc);//in meters
//                distance=distance/1000;
//                String name = mData.get(position).getName();
//                String address = mData.get(position).getAddress();
//                holder.nameView.setText(name);
//                holder.addressView.setText(address);
//                holder.distanceView.setText(df.format(distance)+" km");
//                Glide.with(ctx)
//                        .load(mData.get(position).getPhotoUrl())
//                        .centerCrop()
//                        .into(holder.imgView);
//
//                break;
//            }
//            else{
//
//                //Pass
//
//            }
//
//        }

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return alAF.size();
    }
    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nameView;
        TextView addressView;
        TextView distanceView;

        ImageView redCircle;
        ImageView greenCircle;
        ImageView imgView;

        CardView cvContributorOrder;


        ViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.providerName);
            addressView = itemView.findViewById(R.id.providerAddress);
            distanceView=itemView.findViewById(R.id.distance);
            imgView=itemView.findViewById(R.id.icon);
            redCircle=itemView.findViewById(R.id.redCircle);
            greenCircle=itemView.findViewById(R.id.greenCircle);
            cvContributorOrder = itemView.findViewById(R.id.cvContributorOrder);
            //itemView.setOnClickListener(this);
        }


    }


}
