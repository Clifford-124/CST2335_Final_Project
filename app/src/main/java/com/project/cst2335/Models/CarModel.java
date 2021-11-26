package com.project.cst2335.Models;

public class CarModel {
    private String name;
    private String id;

    public CarModel(String id,String name) {
        this.name= name;
        this.id = id;
    }

    public String getCarName() {
        return this.name;
    }

    public String getId() {
        return this.id;
    }
}