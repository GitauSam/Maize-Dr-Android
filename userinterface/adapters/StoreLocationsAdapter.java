package org.tensorflow.demo.userinterface.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.demo.R;
import org.tensorflow.demo.models.StoreLocations;

import java.util.ArrayList;


public class StoreLocationsAdapter extends RecyclerView.Adapter<StoreLocationsAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<StoreLocations> storeLocationsArrayList;

    public StoreLocationsAdapter(Context context, ArrayList<StoreLocations> storeLocationsArrayList) {
        this.context = context;
        this.storeLocationsArrayList = storeLocationsArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.disease_cardview_layout,parent,false);
        return new MyViewHolder(itemView);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView storeName, storeLocnDetails;
        private ImageView storeImage;
        private MyViewHolder(View itemView) {
            super(itemView);
//            storeName = itemView.findViewById(R.id.text_view_store_name);
//            storeLocnDetails = itemView.findViewById(R.id.text_view_store_location_details);
//            storeImage = itemView.findViewById(R.id.image_view_store_image);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        StoreLocations locations = storeLocationsArrayList.get(position);
//        holder.storeName.setText(locations.getStoreName());
//        holder.storeLocnDetails.setText(locations.getLocationDetails());
        //Glide.with(context).load(locations.getStoreImageUrl()).into(holder.storeImage);
    }


    @Override
    public int getItemCount() {
        return storeLocationsArrayList.size();
    }

}
