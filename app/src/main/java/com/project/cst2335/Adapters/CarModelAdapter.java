package com.project.cst2335.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.cst2335.Activities.CarModelDetailActivity;
import com.project.cst2335.Activities.CarbonActivity;
import com.project.cst2335.Models.CarModel;
import com.project.cst2335.R;
import com.project.cst2335.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class CarModelAdapter extends RecyclerView.Adapter<CarModelAdapter.CarModelsView> {

    ArrayList<CarModel> carModels;
    Context context;
    String display;
    int distance;

    ItemClickListener mItemClickListener;

    //Define Interface method here
    public interface ItemClickListener {
        void onItemClick(CarModel model,String Display);
    }

    public CarModelAdapter(Context context,ArrayList<CarModel> carModels) {
        this.carModels = carModels;
        this.context = context;
    }

    public void addItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setDisplay(String display) {
        this.display = display;
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

                mItemClickListener.onItemClick(carModels.get(position),display);

            });

            modelName = itemView.findViewById(R.id.modelName);
        }
    }
}