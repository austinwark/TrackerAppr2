package com.sandboxcode.trackerappr2.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import static androidx.room.ForeignKey.CASCADE;

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
@Entity(tableName = "result_table",
        primaryKeys = {"vin", "search_id"}
)
public class ResultModel implements Serializable {

    @NonNull
    public String vin;

    @ColumnInfo(name = "search_id")
    @NonNull
    public String searchId;

    public String stock;
    public String make;
    public String model;
    public String year;
    public String trim;

    @ColumnInfo(name = "ext_color")
    public String extColor;

    @ColumnInfo(name = "int_color")
    public String intColor;
    public String price;
    public String miles;
    public String engine;
    public String transmission;
    public String dealer;

    @ColumnInfo(name = "image_url")
    public String imageUrl;

    @ColumnInfo(name = "is_new_result")
    public boolean isNewResult;

    @ColumnInfo(name = "carfax_link")
    public String carfaxLink;

    @ColumnInfo(name = "details_link")
    public String detailsLink;

    @Ignore
    boolean isChecked; // Used to keep track of UI state in RecyclerView (No need to save in DB)

    /**
     * Default constructor required by Firebase
     */
    public ResultModel(){
    }

    public ResultModel(Map<String, String> details) {
        stock = details.get("stock");
        searchId = details.get("searchId");
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

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s | %s",
                stock, searchId, make, model, year, trim, extColor, price, miles, intColor, vin, dealer);
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

        // TODO -- ALSO COMPARE SEARCH_ID
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

    public String getSearchId() { return searchId; }

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
