package com.sandboxcode.trackerappr2.utils;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
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
    private final SearchModel search;
    private final DatabaseReference ref;
    private final String userUid;
    private final StringBuilder queryString;

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
        try {
            doc = Jsoup.connect(queryString.toString()).get();
            Elements mainContent = doc.select("div[data-vin]");
            return mainContent;

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
                details.put("carfaxLink", vehicle.select("a.stat-image-link").attr("href"));
                details.put("detailsLink", vehicle.select("a.vehicleDetailsLink").attr("href"));
                ResultModel resultModel = new ResultModel(details);
                results.add(resultModel);

            }
        }
        return results;
    }

    private enum UrlBits {
        BASE("https://www.liatoyotaofcolonie.com/searchused.aspx?"),
        MODEL("&Model="),
        YEAR("&Year="),
        TRIM("&Trim="),
        PRICE_RANGE("&Pricerange="),
        NOT_ALL_DEALERSHIPS("&Dealership=Lia%20Toyota%20of%20Colonie"),
        MILEAGERANGE("&Mileagerange="); // TODO: add mileage range parameter to search
        private final String val;

        UrlBits(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }

}
