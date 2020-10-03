package com.sandboxcode.trackerappr2.models;

import org.parceler.Parcel;

import java.util.Map;

// TODO - add engine, transmission, and dealer to model
@Parcel
public class SearchResultModel {

    String stock;
    String make;
    String model;
    String year;
    String trim;
    String extColor;
    String intColor;
    String price;
    String vin;
    String miles;
    String dealer;

    public SearchResultModel(){

    }

    public SearchResultModel(Map<String, String> details) {

        stock = details.get("stock");
        make = details.get("make");
        model = details.get("model");
        year = details.get("year");
        trim = details.get("trim");
        extColor = details.get("extColor");
        intColor = details.get("intColor");
        price = details.get("price");
        vin = details.get("vin");
        miles = details.get("miles");
        dealer = details.get("dealer");
    }

    public SearchResultModel(String stock, String make, String model, String year, String trim,
                             String extColor, String intColor, String price, String vin, String miles, String dealer) {
        this.stock = stock;
        this.make = make;
        this.model = model;
        this.year = year;
        this.trim = trim;
        this.extColor = extColor;
        this.intColor = intColor;
        this.price = price;
        this.vin = vin;
        this.miles = miles;
        this.dealer = dealer;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s",
                stock, make, model, year, trim, extColor, price, miles, intColor, vin, dealer);
    }

    public String getTitle() {
        return getYear() + " " + getMake() + " " + getModel() + " " + getTrim();
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getMake() {
        return make;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTrim() {
        return trim;
    }

    public void setTrim(String trim) {
        this.trim = trim;
    }

    public String getExtColor() {
        return extColor;
    }

    public void setExtColor(String extColor) {
        this.extColor = extColor;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    public String getIntColor() {
        return intColor;
    }

    public void setIntColor(String intColor) {
        this.intColor = intColor;
    }

    public String getDealer() { return dealer; }

    public void setDealer(String dealer) { this.dealer = dealer; }

}
