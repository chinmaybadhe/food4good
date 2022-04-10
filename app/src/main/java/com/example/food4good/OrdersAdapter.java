package com.example.food4good;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder>{

    private List<Order> lOrders;
    private Context context;

    public OrdersAdapter(List<Order> lOrders, Context context){
        this.lOrders = lOrders;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.rv_item_order, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // Get the data model based on position
        Order order = lOrders.get(position);

        // Set item views based on your views and data model
        ImageView imgProfile = holder.imgProfile;
        holder.tvOrder.setText("Order ID:");
        holder.tvOrderId.setText("#"+order.getO_id());
        holder.tvRequesterName.setText(order.getRequesterName());
        holder.tvVeg.setText(order.getV_qty()+" veg meal requested");
        holder.tvNonVeg.setText(order.getNv_qty()+" non veg meal requested");
        holder.cvContributorOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context,VerifyOrder.class);
//                intent.putExtra("order_obj", order);
//                context.startActivity(intent);
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reqRef = db.collection("Requester");
        reqRef.document(order.getR_id()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null && value.exists())
                {
                    Requester r = value.toObject(Requester.class);
                    holder.tvRequesterRating.setText(String.format("%.1f", r.getRating()));
                }
                else
                {
                    Log.d("OrdersAdapter",order.getR_id()+" not found");
                }

            }

        });

    }

    @Override
    public int getItemCount() {
        return lOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgProfile;
        public TextView tvOrder, tvOrderId, tvRequesterName, tvVeg, tvNonVeg, tvRequesterRating;
        public CardView cvContributorOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvOrder =  itemView.findViewById(R.id.tvOrder);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvRequesterName = itemView.findViewById(R.id.tvRequesterName);
            tvVeg = itemView.findViewById(R.id.tvVeg);
            tvNonVeg = itemView.findViewById(R.id.tvNonVeg);
            tvRequesterRating = itemView.findViewById(R.id.tvRequesterRating);
            cvContributorOrder = itemView.findViewById(R.id.cvContributorOrder);
        }
    }
}
