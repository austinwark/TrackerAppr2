package com.sandboxcode.trackerappr2.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchModel implements Parcelable {

    private String id;
    private String searchName;
    private String model;
    private String trim;
    private String minYear;
    private String maxYear;
    private String minPrice;
    private String maxPrice;
    private String allDealerships;

    public SearchModel(String key, String searchName, String model, String trim, String minYear,
                       String maxYear, String minPrice, String maxPrice, String allDealerships) {
        this.id = key;
        this.searchName = searchName;
        this.model = model;
        this.trim = trim;
        this.minYear = minYear;
        this.maxYear = maxYear;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.allDealerships = allDealerships;
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
        minYear = in.readString();
        maxYear = in.readString();
        minPrice = in.readString();
        maxPrice = in.readString();
        allDealerships = in.readString();
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

    public String getMinYear() {
        return minYear;
    }

    public void setMinYear(String minYear) {
        this.minYear = minYear;
    }

    public String getMaxYear() {
        return maxYear;
    }

    public void setMaxYear(String maxYear) {
        this.maxYear = maxYear;
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

    public String getAllDealerships() {
        return this.allDealerships;
    }

    public void setAllDealerships(String allDealerships) {
        this.allDealerships = allDealerships;
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
        dest.writeString(minYear);
        dest.writeString(maxYear);
        dest.writeString(minPrice);
        dest.writeString(maxPrice);
        dest.writeString(allDealerships);
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }
}
