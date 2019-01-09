package org.tensorflow.demo.userinterface.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.tensorflow.demo.R;
import org.tensorflow.demo.models.Diseases;

import java.util.ArrayList;

public class DiseasesAdapter extends RecyclerView.Adapter<DiseasesAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Diseases> diseasesArrayList;

    public DiseasesAdapter(Context context, ArrayList<Diseases> diseasesArrayList) {
        this.context = context;
        this.diseasesArrayList = diseasesArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.disease_cardview_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView diseaseName, causativeAgent, diseaseDetails;
        private ImageView diseaseImage;
        private MyViewHolder(View itemView) {
            super(itemView);
            diseaseName = itemView.findViewById(R.id.text_view_disease_name);
            causativeAgent = itemView.findViewById(R.id.text_view_causative_agent_name);
            diseaseDetails = itemView.findViewById(R.id.text_view_disease_details);
            diseaseImage = itemView.findViewById(R.id.image_view_disease_lib_image);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Diseases disease = diseasesArrayList.get(position);
        holder.diseaseName.setText(disease.getDiseaseName());
        holder.causativeAgent.setText(disease.getCausativeAgent());
        holder.diseaseDetails.setText(disease.getDiseaseSymptoms());
        Glide.with(context).load(disease.getImageURL()).into(holder.diseaseImage);
    }

    @Override
    public int getItemCount() {
        return diseasesArrayList.size();
    }

}
