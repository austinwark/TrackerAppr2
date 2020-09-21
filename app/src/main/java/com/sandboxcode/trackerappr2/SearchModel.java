package com.sandboxcode.trackerappr2;

public class SearchModel {

    public String make;
    private String model;
    private String stock;

    public SearchModel(String make, String model, String stock) {
        this.make = make;
        this.model = model;
        this.stock = stock;
    }

    public String getMake() {
        return this.make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
