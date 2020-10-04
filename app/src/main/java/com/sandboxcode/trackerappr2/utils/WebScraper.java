package com.sandboxcode.trackerappr2.utils;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.sandboxcode.trackerappr2.activities.CreateActivity;
import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.models.SearchResultModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebScraper extends AsyncTask<Void, Void, String> {


    private static final String TAG = "WebScraper";
    private CreateActivity createActivity;
    private SearchModel search;
    private DatabaseReference ref;
    private String userUid;
    private StringBuilder queryString;

    public WebScraper(CreateActivity createActivity, SearchModel search, DatabaseReference ref, String userUid) {

        this.createActivity = createActivity;
        this.search = search;
        this.ref = ref;
        this.userUid = userUid;
        queryString = new StringBuilder();
        buildQueryString();
    }

    private void buildQueryString() {
        queryString.append(UrlBits.BASE.getVal());
        if (!search.getModel().isEmpty()) {
            queryString.append(UrlBits.MODEL.getVal());
            queryString.append(search.getModel());
            if (!search.getTrim().isEmpty()) {
                queryString.append(UrlBits.TRIM.getVal());
                queryString.append(search.getTrim());
            }
        }
        if (!search.getYear().isEmpty()) {
            queryString.append(UrlBits.YEAR.getVal());
            queryString.append(search.getYear());

        }

        queryString.append(UrlBits.PRICE_RANGE.getVal());
        queryString.append(search.getMinPrice());
        queryString.append("-");
        queryString.append(search.getMaxPrice());
    }

    // TODO - ask StackOverflow how to return Elements
    protected String doInBackground(Void... params) {
        Document doc;
        Log.d(TAG, queryString.toString());
        try {
            doc = Jsoup.connect(queryString.toString()).get();
            Elements mainContent = doc.select("div[data-vin]");
            Elements dealerNames = doc.select(".dealershipDisplay");
            parseElements(mainContent, dealerNames);

        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    private void parseElements(Elements content, Elements dealerNames) {
        ArrayList<SearchResultModel> results = new ArrayList<>();

        for (Element vehicle : content) {
            Map<String, String> details = new HashMap<>();
            details.put("vin", vehicle.attr("data-vin"));
            details.put("make", vehicle.attr("data-make"));
            details.put("model", vehicle.attr("data-model"));
            details.put("year", vehicle.attr("data-year"));
            details.put("trim", vehicle.attr("data-trim"));
            details.put("extColor", vehicle.attr("data-extcolor"));
            details.put("intColor", vehicle.attr("data-intcolor"));
            details.put("price", vehicle.attr("data-price"));
            details.put("stock", vehicle.attr("data-stocknum"));
            details.put("engine", vehicle.attr("data-engine"));
            details.put("transmission", vehicle.attr("data-trans"));
            details.put("miles", vehicle.select(".mileageDisplay").first().text().substring(8).trim());
            details.put("dealer", vehicle.select("li.dealershipDisplay").first().text().substring(11).trim());

            SearchResultModel resultModel = new SearchResultModel(details);
            results.add(resultModel);
            Log.d(TAG, "\t" + resultModel.toString() + "\n");
        }

        updateDatabase(results);
    }

    private void updateDatabase(ArrayList<SearchResultModel> searchResults) {
        final ArrayList<SearchResultModel> results = searchResults;
//        final String KEY = ref.child("queries").child(userUid).push().getKey();
        ref.child("queries").child(userUid).child(search.getId())
                .setValue(search).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

//                ref.child("results").child(search.getId()).setValue(results);
                for (SearchResultModel result : results) {
                    ref.child("results").child(search.getId()).child(result.getVin()).setValue(result);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                createActivity.sendMessage("Error saving query: " + e);
            }
        });
    }

    private enum UrlBits {
        BASE("https://www.liatoyotaofcolonie.com/searchused.aspx?Dealership=Lia%20Toyota%20of%20Colonie"),
        MODEL("&Model="),
        YEAR("&Year="),
        TRIM("&f=trim%3A"),
        PRICE_RANGE("&Pricerange="),
        MILEAGERANGE("&Mileagerange="); // TODO: add mileage range parameter to search
        private String val;
        private UrlBits(String val) {
            this.val = val;
        }
        public String getVal() {
            return val;
        }
    }

}
