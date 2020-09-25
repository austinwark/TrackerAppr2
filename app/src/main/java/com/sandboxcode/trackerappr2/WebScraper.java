package com.sandboxcode.trackerappr2;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebScraper extends AsyncTask<Void, Void, String> {


    private final String TAG = "WebScraper";
    private String model;
    private String trim;
    private String year;
    private StringBuilder queryString = new StringBuilder();

    public WebScraper(String model, String trim, String year) {

        this.model = model;
        this.year = year;
        this.trim = trim;

        buildQueryString();
    }

    private void buildQueryString() {
        queryString.append(UrlBits.BASE.getVal());
        if (model != null) {
            queryString.append(UrlBits.MODEL.getVal());
            queryString.append(model);
            if (trim != null) {
                queryString.append(UrlBits.TRIM.getVal());
                queryString.append(trim);
            }
        }
        if (year != null)
            queryString.append(UrlBits.YEAR.getVal());
            queryString.append(year);
    }

    public void scrape() throws IOException {
        Log.d(TAG, "TEST");
        Document doc = Jsoup.connect(queryString.toString()).get();
        Log.d(TAG, doc.title());
    }

    @Override
    protected String doInBackground(Void... params) {
        String title = "";
        Document doc;
        Log.d(TAG, queryString.toString());
        try {
            doc = Jsoup.connect(queryString.toString()).get();
            Elements content = doc.select("[data-at]");
            for (Element e: content) {
                Log.d(TAG, e.text());
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return title;
    }

    private enum UrlBits {
        BASE("https://express.liatoyotaofcolonie.com/inventory?f=dealer.name%3ALia%20Toyota%20of%20Colonie"),
        MODEL("&f=submodel%3A"),
        YEAR("&f=year%3A"),
        TRIM("&f=trim%3A");
        private String val;
        private UrlBits(String val) {
            this.val = val;
        }
        public String getVal() {
            return val;
        }
    }

}
