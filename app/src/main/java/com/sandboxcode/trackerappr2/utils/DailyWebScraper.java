package com.sandboxcode.trackerappr2.utils;

import android.os.AsyncTask;
import android.util.Log;

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

public class DailyWebScraper extends AsyncTask<Void, Void, Map<SearchModel, Elements>> {


    private static final String TAG = "WebScraper";
    private final ArrayList<SearchModel> searches;

    private DailyAsyncResponse delegate = null;

    public DailyWebScraper(ArrayList<SearchModel> searches) {

        this.searches = searches;
    }

    public void setDelegate(DailyAsyncResponse delegate) {
        this.delegate = delegate;
    }

    private StringBuilder buildQueryString(SearchModel search) {
        StringBuilder queryString = new StringBuilder();
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
        return queryString;
    }

    protected Map<SearchModel, Elements> doInBackground(Void... params) {
        Map<SearchModel, Elements> map = new HashMap<>();
        StringBuilder queryString;
        for (SearchModel search : searches) {
            queryString = buildQueryString(search);
            Document doc;
            try {
                doc = Jsoup.connect(queryString.toString()).get();
                Elements mainContent = doc.select("div[data-vin]");
                if (mainContent != null)
                    map.put(search, mainContent);

            } catch (IOException e) {
                Log.e(TAG, e.toString());
                return null;
            }
        }
        return map;

    }

    @Override
    protected void onPostExecute(Map<SearchModel, Elements> map) {
        Map<String, ArrayList<ResultModel>> resultsMap = new HashMap<>();

        SearchModel search;
        Elements mainContent;
        ArrayList<ResultModel> results;

        for (Map.Entry<SearchModel, Elements> entry : map.entrySet()) {
            search = entry.getKey();
            mainContent = entry.getValue();

            results = parseElements(search, mainContent);
            resultsMap.put(search.getId(), results);
        }
        delegate.processResults(resultsMap);


//        for (SearchModel search : map.keySet()) {
//             mainContent = map.get(search);
//
//            if (mainContent != null) {
//                results = parseElements(mainContent);
//                resultMap.put(search.getId(), results);
//                //                delegate.processResults(results, search.getId());
//
//            }
//        }
    }

    private ArrayList<ResultModel> parseElements(SearchModel search, Elements content) {
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
