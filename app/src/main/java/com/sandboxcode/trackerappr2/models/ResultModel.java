package com.sandboxcode.trackerappr2.models;

import org.parceler.Parcel;

import java.util.Map;

// TODO - add engine, transmission, and dealer to model
@Parcel
public class ResultModel {

    private String stock;
    private String make;
    private String model;
    private String year;
    private String trim;
    private String extColor;
    private String intColor;
    private String price;
    private String vin;
    private String miles;
    private String engine;
    private String transmission;
    private String dealer;
    private String imageUrl;
    private boolean isNewResult;
    private String carfaxLink;

    /**
     * Default constructor required by Firebase
     */
    public ResultModel(){
    }

    public ResultModel(Map<String, String> details) {

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
        engine = details.get("engine");
        transmission = details.get("transmission");
        dealer = details.get("dealer");
        imageUrl = details.get("imageUrl");
        isNewResult = true;
        carfaxLink = details.get("carfaxLink");
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s",
                stock, make, model, year, trim, extColor, price, miles, intColor, vin, dealer);
    }

    public String getTitle() {
        return getYear() + " " + getModel() + " " + getTrim();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof ResultModel))
            return false;

        ResultModel otherResult = (ResultModel) o;

        if (otherResult.getVin() != null)
            return otherResult.getVin().equalsIgnoreCase(getVin());
        else
            return false;
    }

    @Override
    public int hashCode() {
        return getVin().hashCode();
    }


    public String getStock() {
        return stock;
    }

    public String getModel() {
        return model;
    }

    public String getYear() {
        return year;
    }

    public String getTrim() {
        return trim;
    }

    public String getExtColor() {
        return extColor;
    }

    public String getPrice() {
        return price;
    }

    public String getVin() {
        return vin;
    }

    public String getMiles() {
        return miles;
    }

    public String getIntColor() {
        return intColor;
    }

    public String getDealer() { return dealer; }

    public String getEngine() {
        return engine;
    }

    public String getTransmission() {
        return transmission;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean getIsNewResult() {
        return isNewResult;
    }

    public void setIsNewResult(boolean isNewResult) {
        this.isNewResult = isNewResult;
    }

    public String getCarfaxLink() {
        return carfaxLink;
    }
}
