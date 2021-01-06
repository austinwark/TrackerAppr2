package com.sandboxcode.trackerappr2.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
    private int numberOfResults;
    private int numberOfNewResults;
    private String createdDate;
    private String lastEditedDate;

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

    public SearchModel(String key, String searchName, String model, String trim, String minYear,
                       String maxYear, String minPrice, String maxPrice, String allDealerships,
                       int numberOfResults, int numberOfNewResults) {
        this.id = key;
        this.searchName = searchName;
        this.model = model;
        this.trim = trim;
        this.minYear = minYear;
        this.maxYear = maxYear;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.allDealerships = allDealerships;
        this.numberOfResults = numberOfResults;
        this.numberOfNewResults = numberOfNewResults;
    }

    /**
     * Default constructor required by Firebase
     */
    public SearchModel() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof SearchModel))
            return false;

        SearchModel otherSearch = (SearchModel) o;

        if (otherSearch.getId() != null)
            return otherSearch.getId().equalsIgnoreCase(getId());
        else
            return false;
    }

    @Override
    public int hashCode() { return getId().hashCode(); }

    public void setCreatedDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        createdDate = sdf.format(cal.getTime());
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setLastEditedDate() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        lastEditedDate = sdf.format(cal.getTime());
    }

    @Override
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

    public String getTrim() {
        return trim;
    }

    public String getMinYear() {
        return minYear;
    }

    public String getMaxYear() {
        return maxYear;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public String getId() {
        return id;
    }

    public String getAllDealerships() {
        return this.allDealerships;
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

    public int getNumberOfResults() {
        return numberOfResults;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getLastEditedDate() {
        return lastEditedDate;
    }

}
