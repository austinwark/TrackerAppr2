package com.sandboxcode.trackerappr2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchModel implements Parcelable {

    private String id;
    private String searchName;
    private String model;
    private String trim;
    private String year;
    private String minPrice;
    private String maxPrice;

    public SearchModel(String key, String searchName, String model, String trim, String year, String minPrice, String maxPrice) {
        this.id = key;
        this.searchName = searchName;
        this.model = model;
        this.trim = trim;
        this.year = year;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    /**
     * Default constructor required by Firebase
     */
    public SearchModel() {
    }

    public String toString() {
        return String.format("%s | %s | %s | %s", id, searchName, model, trim);
    }

    protected SearchModel(Parcel in) {
        id = in.readString();
        searchName = in.readString();
        model = in.readString();
        trim = in.readString();
        year = in.readString();
        minPrice = in.readString();
        maxPrice = in.readString();
    }

    public static final Creator<SearchModel> CREATOR = new Creator<SearchModel>() {
        @Override
        public SearchModel createFromParcel(Parcel in) {
            return new SearchModel(in);
        }

        @Override
        public SearchModel[] newArray(int size) {
            return new SearchModel[size];
        }
    };

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getTrim() {
        return trim;
    }

    public void setTrim(String trim) {
        this.trim = trim;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(model);
        dest.writeString(trim);
        dest.writeString(year);
        dest.writeString(minPrice);
        dest.writeString(maxPrice);
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }
}
