package com.sandboxcode.trackerappr2.utils;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WebScraper extends AsyncTask<Void, Void, Elements> {


    private static final String TAG = "WebScraper";
    private SearchModel search;
    private DatabaseReference ref;
    private String userUid;
    private StringBuilder queryString;

    private AsyncResponse delegate = null;

    public WebScraper(SearchModel search, DatabaseReference ref, String userUid) {

        this.search = search;
        this.ref = ref;
        this.userUid = userUid;
        queryString = new StringBuilder();
        buildQueryString();
    }

    public void setDelegate(AsyncResponse delegate) {
        this.delegate = delegate;
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

        if (!Boolean.parseBoolean(search.getAllDealerships()))
            queryString.append(UrlBits.NOT_ALL_DEALERSHIPS.getVal());

//        if (!search.getYear().isEmpty()) {
//            queryString.append(UrlBits.YEAR.getVal());
//            queryString.append(search.getYear());
//
//        }

        queryString.append(UrlBits.PRICE_RANGE.getVal());
        queryString.append(search.getMinPrice());
        queryString.append("-");
        queryString.append(search.getMaxPrice());
    }

    // TODO - ask StackOverflow how to return Elements
    protected Elements doInBackground(Void... params) {
        Document doc;
        Log.d(TAG, queryString.toString());
        try {
            doc = Jsoup.connect(queryString.toString()).get();
            Elements mainContent = doc.select("div[data-vin]");
            Elements dealerNames = doc.select(".dealershipDisplay");
            return mainContent;
//            parseElements(mainContent, dealerNames);

        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Elements mainContent) {
        ArrayList<ResultModel> results;
        if (mainContent != null) {
            results = parseElements(mainContent);
            delegate.processResults(results, search.getId());
        }
    }

    private ArrayList<ResultModel> parseElements(Elements content) {
        ArrayList<ResultModel> results = new ArrayList<>();

        for (Element vehicle : content) {
            float minYear = Float.parseFloat(search.getMinYear());
            float maxYear = Float.parseFloat(search.getMaxYear());
            float vehicleYear = Float.parseFloat(vehicle.attr("data-year"));
            if (vehicleYear >= minYear && vehicleYear <= maxYear) {

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
                String imageUrl = "https://www.liatoyotaofcolonie.com" +
                        vehicle.select("div.vehiclePhoto").first()
                                .select("img.vehicleImg").attr("src");
                details.put("imageUrl", imageUrl);
                ResultModel resultModel = new ResultModel(details);
                results.add(resultModel);

//                Log.d(TAG, "\t" + resultModel.toString() + "\n");
                Log.d(TAG, "URL: " + imageUrl);
            } else
                Log.d(TAG, "not in year range");
        }
        return results;
//        updateDatabase(results);
    }

    private void updateDatabase(ArrayList<ResultModel> searchResults) {
        DatabaseReference resultsRef = ref.child("results").child(search.getId());

        resultsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ResultModel> currentResults = new ArrayList<>();
                int numberOfResults = 0;

                // Get the current results of the search
                if (snapshot.hasChildren())
                    for (DataSnapshot child : snapshot.getChildren())
                        currentResults.add(child.getValue(ResultModel.class));

                if (!currentResults.isEmpty()) {
                    // Check each newly scraped result against each current result
                    for (ResultModel newResult : searchResults) {
                        for (ResultModel currentResult : currentResults) {
                            // If newResult is in currentResults (meaning it has already been
                            // scraped) copy the currentResult's isNew field
                            if (newResult.equals(currentResult))
                                newResult.setIsNewResult(currentResult.getIsNewResult());
                        }
                    }
                }

                // Reset the results document in firebase
                resultsRef.setValue(null);

                // Save each new result and keep track of the total count
                for (ResultModel result : searchResults) {
                    resultsRef.child(result.getVin()).setValue(result);
                    numberOfResults++;
                }

                // Set number of results in search document
                ref.child("queries").child(userUid).child(search.getId())
                        .child("numberOfResults").setValue(numberOfResults);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    private enum UrlBits {
        BASE("https://www.liatoyotaofcolonie.com/searchused.aspx?"),
        MODEL("&Model="),
        YEAR("&Year="),
        TRIM("&Trim="),
        PRICE_RANGE("&Pricerange="),
        NOT_ALL_DEALERSHIPS("&Dealership=Lia%20Toyota%20of%20Colonie"),
        MILEAGERANGE("&Mileagerange="); // TODO: add mileage range parameter to search
        private String val;

        UrlBits(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

}
