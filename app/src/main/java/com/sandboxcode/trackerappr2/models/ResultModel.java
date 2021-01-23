package com.sandboxcode.trackerappr2.models;

import com.google.firebase.database.Exclude;

import org.parceler.Parcel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

// TODO - Implement AutoValue @Parcelable extension (need to make class immutable)
@Parcel
public class ResultModel implements Serializable {

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
    String engine;
    String transmission;
    String dealer;
    String imageUrl;
    boolean isNewResult;
    String carfaxLink;
    String detailsLink;
    boolean isChecked; // field used to keep track of UI state in RecyclerView

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
        detailsLink = details.get("detailsLink");
        isChecked = false;
    }

    public static Object deepCopy(Object object) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(object);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public ResultModel(ResultModel result) {
//        this.stock = result.getStock();
//        this.make = result.getMake();
//        this.model = result.getModel();
//        this.year = result.getYear();
//        this.trim = result.getTrim();
//        this.extColor = result.getExtColor();
//        this
//    }

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

    public String getMake() { return make; }

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

    public String getDetailsLink() { return detailsLink; }

    public void setIsChecked(boolean isChecked) { this.isChecked = isChecked; }

    // No need to save this UI state field in database
    @Exclude
    public boolean isChecked() { return isChecked; }

}
