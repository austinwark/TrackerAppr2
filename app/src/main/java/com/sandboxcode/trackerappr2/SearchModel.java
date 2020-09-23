package com.sandboxcode.trackerappr2;

public class SearchModel {

    private String model;
    private String trim;
    private String year;

    public SearchModel(String model, String trim, String year) {
        this.model = model;
        this.trim = trim;
        this.year = year;
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

}
