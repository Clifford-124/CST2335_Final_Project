package com.project.cst2335.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.cst2335.Activities.CarbonActivity;
import com.project.cst2335.Models.CarModel;
import com.project.cst2335.R;

import java.util.ArrayList;
import java.util.List;

public class CarModelAdapter extends RecyclerView.Adapter<CarModelsView> {

    ArrayList<CarModel> carModels;

    public CarModelAdapter(ArrayList<CarModel> carModels) {
        this.carModels = carModels;
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
}

class CarModelsView extends RecyclerView.ViewHolder {
    TextView modelName;

    public CarModelsView(View itemView) {
        super(itemView);
        itemView.setOnClickListener(click -> {

            int position = getAdapterPosition();
            //String model = ((CarModel)models.get(position)).getCarName();
            //Toast.makeText(getApplicationContext(),model,Toast.LENGTH_LONG).show();

        });

        modelName = itemView.findViewById(R.id.modelName);
    }
}