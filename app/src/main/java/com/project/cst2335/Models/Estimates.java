package com.project.cst2335.Models;

import android.content.Context;

import androidx.annotation.NonNull;

import com.project.cst2335.R;

public class Estimates {
    private int distance;
    private String distance_unit;

    private Double carbon_g;
    private Double carbon_kg;
    private Double carbon_mt;
    private Double carbon_lb;

    private String model_name;

    public Estimates(String mdoel_name, int distance, String distance_unit, Double carbon_g, Double carbon_lb, Double carbon_mt, Double carbon_kg) {
        this.model_name = mdoel_name;
        this.distance = distance;
        this.distance_unit = distance_unit;
        this.carbon_g = carbon_g;
        this.carbon_kg = carbon_kg;
        this.carbon_lb = carbon_lb;
        this.carbon_mt = carbon_mt;
    }

    public Estimates(String model_name, int distance, String distance_unit) {
        this.model_name = model_name;
        this.distance = distance;
        this.distance_unit = distance_unit;
    }

    public void setCarbon_g(Double carbon_g) {
        this.carbon_g = carbon_g;
    }

    public void setCarbon_kg(Double carbon_kg) {
        this.carbon_kg = carbon_kg;
    }

    public void setCarbon_mt(Double carbon_mt) {
        this.carbon_mt = carbon_mt;
    }

    public void setCarbon_lb(Double carbon_lb) {
        this.carbon_lb = carbon_lb;
    }

    public int getDistance() {
        return distance;
    }

    public String getDistance_unit() {
        return distance_unit;
    }

    public Double getCarbon_g() {
        return carbon_g;
    }

    public Double getCarbon_kg() {
        return carbon_kg;
    }

    public Double getCarbon_mt() {
        return carbon_mt;
    }

    public Double getCarbon_lb() {
        return carbon_lb;
    }

    public String getModel_name() {
        return model_name;
    }

    public String toString(Context context) {
        return  context.getResources().getString(R.string.carbon_estimates,this.carbon_g,
                this.carbon_lb,
                this.carbon_kg,
                this.carbon_mt);
    }
}