package com.project.cst2335.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.cst2335.Activities.CarModelDetailActivity;
import com.project.cst2335.Models.CarModel;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Constants;

import java.util.ArrayList;

public class CarModelAdapter extends RecyclerView.Adapter<CarModelAdapter.CarModelsView> {

    ArrayList<CarModel> carModels;
    Context context;
    String distance_unit="";
    String vehicle_make;
    int distance;

    public CarModelAdapter(Context context,ArrayList<CarModel> carModels,int distance,String distance_unit,String vehicle_make) {
        this.carModels = carModels;
        this.context = context;
        this.distance = distance;
        this.distance_unit = distance_unit;
        this.vehicle_make = vehicle_make;
    }

    public CarModelAdapter(Context context,ArrayList<CarModel> carModels) {
        this.carModels = carModels;
        this.context = context;
    }

    @NonNull
    @Override
    public CarModelsView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_model, parent, false);
        CarModelsView holder = new CarModelsView(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CarModelsView holder, int position) {
        holder.modelName.setText((carModels.get(position)).getCarName());
    }

    @Override
    public int getItemCount() {
        return carModels.size();
    }

    protected class CarModelsView extends RecyclerView.ViewHolder {
        TextView modelName;

        public CarModelsView(View itemView) {
            super(itemView);
            itemView.setOnClickListener(click -> {

                int position = getAdapterPosition();
                String model_id = ((CarModel) carModels.get(position)).getId();
                String model_name = ((CarModel) carModels.get(position)).getCarName();

                Intent detailIntent = new Intent(context, CarModelDetailActivity.class);

                detailIntent.putExtra(Constants.ARG_MODEL_ID, model_id);
                detailIntent.putExtra(Constants.ARG_MODEL_NAME, model_name);
                detailIntent.putExtra(Constants.ARG_VEHICLE_MAKE, vehicle_make);

                if (!distance_unit.contentEquals("")) {
                    detailIntent.putExtra(Constants.ARG_DISTANCE, distance);
                    detailIntent.putExtra(Constants.ARG_DISTANCE_UNIT, distance_unit);
                    detailIntent.putExtra(Constants.ARG_DISPLAY, "fromAPI");
                }
                else
                    detailIntent.putExtra(Constants.ARG_DISPLAY, "fromDB");

                context.startActivity(detailIntent);
            });

            modelName = itemView.findViewById(R.id.modelName);
        }
    }
}