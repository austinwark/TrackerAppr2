package com.sandboxcode.trackerappr2;

public class SearchModel {

    private String model;
    private String trim;
    private String year;
    private int minPrice;
    private int maxPrice;

    public SearchModel(String model, String trim, String year, int minPrice, int maxPrice) {
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

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(int maxPrice) {
        this.maxPrice = maxPrice;
    }

}
