package com.sandboxcode.trackerappr2.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.InboxStyle;
import androidx.preference.PreferenceManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.activities.LoginActivity;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.room_components.ResultDao;
import com.sandboxcode.trackerappr2.room_components.SearchDao;
import com.sandboxcode.trackerappr2.room_components.SearchRoomDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO -- Access Room through Repository and maybe ViewModel?
public class ResultsReceiver extends BroadcastReceiver implements DailyAsyncResponse {

    private static final String TAG = "ResultsReceiver";
    // Notification ID.
    private static final int NOTIFICATION_ID = 0;
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    private static final String USER_ID_EXTRA = "user_id";
    private static final DatabaseReference DATABASE_REF = FirebaseDatabase.getInstance().getReference();
    private SearchDao searchDao;
    private ResultDao resultDao;
    private NotificationManager notificationManager;
    private Context context;
    private String userId;
    private int numberOfNotifications;

    private final ArrayList<NewNotification> newNotifications = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        userId = intent.getStringExtra(USER_ID_EXTRA);

        SearchRoomDatabase db = SearchRoomDatabase.getDatabase(context);
        searchDao = db.getSearchDao();
        resultDao = db.getResultDao();

        if (userId != null && !userId.isEmpty()) {
            notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            numberOfNotifications = 0;
            refreshResults(userId);
        }


    }

    private void refreshResults(String userId) {
        List<SearchModel> searches = searchDao.loadAllSearchesOnce();

        if (searches == null || searches.isEmpty())
            return;

        DailyWebScraper scraper = new DailyWebScraper(searches);
        scraper.setDelegate(ResultsReceiver.this);
        scraper.execute();
    }

    //    private void refreshResults(String userId) {
//        // Get search IDs
//        DATABASE_REF.child("queries").child(userId)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        ArrayList<SearchModel> searches = new ArrayList<>();
//                        if (snapshot.hasChildren()) {
//
//                            // Get the user's searches
//                            for (DataSnapshot child : snapshot.getChildren())
//                                searches.add(child.getValue(SearchModel.class));
//
//                            DailyWebScraper scraper = new DailyWebScraper(searches);
//                            scraper.setDelegate(ResultsReceiver.this);
//                            scraper.execute();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                    }
//                });
//    }

//    @Override
//    public void processResults(Map<String, ArrayList<ResultModel>> results) {
//
//        // Ref pointing to the user's search results grouped by search ID
//        DatabaseReference searchesRef = DATABASE_REF.child("results").child(userId);
//
//        searchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                // Close method if user has no searches
//                if (!snapshot.hasChildren())
//                    return;
//
//                // Counter used to create notifications after each set of results is checked
//                long totalSearches = snapshot.getChildrenCount();
//                int searchesCount = 0;
//
//                //  For each group of search results
//                for (DataSnapshot searchInDb : snapshot.getChildren()) {
//
//                    String searchId = searchInDb.getKey();
//
//                    if (searchId == null)
//                        return;
//
//                    // Ref pointing to results from one search
//                    DatabaseReference resultsRef = searchesRef.child(searchId);
//
//                    ArrayList<ResultModel> newSearchResults = results.get(searchId);
//
//                    if (newSearchResults == null) // Protects again Null Pointer Exception
//                        newSearchResults = new ArrayList<>();
//
//                    ArrayList<ResultModel> currentResults = new ArrayList<>();
//
//                    int numberOfResults = 0; // total results for the search
//                    int numberOfNewResults = 0; // total new (unseen) results for the search
//
//                    // Load the search's current results
//                    for (DataSnapshot currentResult : searchInDb.getChildren())
//                        currentResults.add(currentResult.getValue(ResultModel.class));
//
//                    // If there are existing results in database
//                    if (!currentResults.isEmpty()) {
//
//                        // Check each scrapedResult to see if it already exists
//                        for (ResultModel scrapedResult : newSearchResults) {
//                            if (currentResults.contains(scrapedResult)) {
//
//                                // Get the already existing result from currentResults
//                                ResultModel matchingCurrentResult =
//                                        currentResults.get(currentResults.indexOf(scrapedResult));
//
//                                // If the already existing result has been viewed by user -- update
//                                // the scrapedResult with the same value
//                                if (!matchingCurrentResult.getIsNewResult()) {
//                                    scrapedResult.setIsNewResult(false); // true by default
//
//                                } else // If currentResult has already been viewed
//                                    numberOfNewResults++;
//
//                            } else // If scrapedResult has not been scraped yet
//                                numberOfNewResults++;
//                        }
//                    } else // If there are no existing current results
//                        numberOfNewResults = newSearchResults.size();
//
//                    resultsRef.setValue(null); // reset the search's result document
//
//                    // Save each new search result and keep track of total results
//                    for (ResultModel result : newSearchResults) {
//                        resultsRef.child(result.getVin()).setValue(result);
//                        numberOfResults++;
//                    }
//
//                    // Set number of results (total and new) in search document
//                    DATABASE_REF.child("queries").child(userId).child(searchId)
//                            .child("numberOfResults").setValue(numberOfResults);
//                    DATABASE_REF.child("queries").child(userId).child(searchId)
//                            .child("numberOfNewResults").setValue(numberOfNewResults);
//
//                    // Create a new notification if there is one or more new results
//                    if (numberOfNewResults > 0) {
//                        numberOfNotifications++;
//                        newNotifications.add(new NewNotification(searchId, numberOfNewResults));
//                    }
//
//                    // Send notification once all searches have been checked
//                    searchesCount++;
//                    if (searchesCount == totalSearches) {
//                        int totalNewNotifications = 0;
//                        for (NewNotification newNotification : newNotifications)
//                            totalNewNotifications += newNotification.getNumberOfNewResults();
//
//                        // If user has notifications enabled -- show notifications
//                        SharedPreferences sharedPreferences =
//                                PreferenceManager.getDefaultSharedPreferences(context);
//                        if (sharedPreferences.getBoolean("notifications", false))
//                            deliverSummaryNotification(context, newNotifications, totalNewNotifications);
//                    }
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // No need to notify user of firebase read error
//            }
//        });
//    }

//    @Override
//    public void processResults(Map<String, ArrayList<ResultModel>> results) {
//
//        List<SearchModel> searches = searchDao.loadAllSearchesOnce();
//        if (searches == null || searches.isEmpty())
//            return;
//
//        int searchesCount = 0;
//        int totalSearchesCount = searches.size();
//
//        for (SearchModel search : searches) {
//
//            int numberOfResults = 0;
//            int numberOfNewResults = 0;
//            String searchId = search.getId();
//
//            ArrayList<ResultModel> scrapedResults = results.get(searchId);
//
//
//            if (scrapedResults == null)
//                scrapedResults = new ArrayList<>();
//
//            List<ResultModel> currentResultsInDb = resultDao.loadAllResults(searchId);
//
//            if (!currentResultsInDb.isEmpty()) {
//
//                for (ResultModel scrapedResult : scrapedResults) {
//                    if (currentResultsInDb.contains(scrapedResult)) {
//
//                        ResultModel matchingCurrentResult = currentResultsInDb.get(currentResultsInDb.indexOf(scrapedResult));
//                        if (!matchingCurrentResult.getIsNewResult())
//                            scrapedResult.setIsNewResult(false);
//                        else
//                            numberOfNewResults++;
//
//                    } else
//                        numberOfNewResults++;
//                }
//            } else
//                numberOfNewResults = scrapedResults.size();
//
//            resultDao.deleteAll(searchId);
//
//            for (ResultModel result : scrapedResults) {
//                resultDao.insertResults(result);
//                numberOfResults++;
//            }
//
//            search.setNumberOfNewResults(numberOfNewResults);
//            search.setNumberOfResults(numberOfResults);
//
//            if (numberOfNewResults > 0) {
//                numberOfNotifications++;
//                newNotifications.add(new NewNotification(searchId, numberOfNewResults));
//            }
//
//            searchesCount++;
//            if (searchesCount == totalSearchesCount) {
//                int totalNewNotifications = 0;
//                for (NewNotification newNotification : newNotifications)
//                    totalNewNotifications += newNotification.getNumberOfNewResults();
//
//                SharedPreferences sharedPreferences =
//                        PreferenceManager.getDefaultSharedPreferences(context);
//                if (sharedPreferences.getBoolean("notifications", false))
//                    deliverSummaryNotification(context, newNotifications, totalNewNotifications);
//            }
//        }
//    }

    @Override
    public void processResults(Map<String, ArrayList<ResultModel>> results) {

        // Load all searches from DB
        List<SearchModel> searches = searchDao.loadAllSearchesOnce();
        if (searches == null || searches.isEmpty())
            return;

        // Keeps track of iterations
        int searchesCount = 0;
        int totalSearchesCount = searches.size();


        for (SearchModel search : searches) {

            int numberOfResults = 0;
            int numberOfNewResults = 0;
            String searchId = search.getId();

            // Results from WebScraper
            ArrayList<ResultModel> scrapedResults = results.get(searchId);


            if (scrapedResults == null)
                scrapedResults = new ArrayList<>();

            // Insert only new results into DB
            for (ResultModel scrapedResult : scrapedResults) {
                resultDao.insertOnlyNewResults(scrapedResult);
            }

            // All results, both new and old
            List<ResultModel> currentResultsInDb = resultDao.loadAllResults(searchId);

            if (!currentResultsInDb.isEmpty()) {

                for (ResultModel result : currentResultsInDb) {

                    // Delete old results not found in new web scrape
                    if (!scrapedResults.contains(result)) {
                        resultDao.deleteResults(result);

                    } else {

                        // Keep track of total and new results
                        numberOfResults++;
                        if (result.getIsNewResult())
                            numberOfNewResults++;
                    }

                }
            }

            search.setNumberOfNewResults(numberOfNewResults);
            search.setNumberOfResults(numberOfResults);

            if (numberOfNewResults > 0) {
                numberOfNotifications++;
                newNotifications.add(new NewNotification(searchId, search.getSearchName(), numberOfNewResults));
            }

            searchesCount++;
            if (searchesCount == totalSearchesCount) {
                int totalNewNotifications = 0;
                for (NewNotification newNotification : newNotifications)
                    totalNewNotifications += newNotification.getNumberOfNewResults();

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);

                if (sharedPreferences.getBoolean("notifications", false))

                    deliverSummaryNotification(context, newNotifications,
                            totalNewNotifications);
            }
        }
    }

    private void deliverSummaryNotification(Context context,
                                            ArrayList<NewNotification> newNotifications,
                                            int totalNewNotifications) {

        String bigContentTitle;
        String summaryText;
        String contentTitle;
        String contextText;

        bigContentTitle = totalNewNotifications + " new results from "
                + newNotifications.size() + (newNotifications.size() > 1 ? " searches" : " search");
        summaryText = "New Results";
        contentTitle = totalNewNotifications
                + (totalNewNotifications > 1 ? " new results" : "new result");
        contextText = "Click here to see the new results";

        InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle(bigContentTitle)
                .setSummaryText(summaryText);

        for (NewNotification notification : newNotifications) {
            String inboxStyleLine = notification.getNumberOfNewResults() +  " new results in "
                    + notification.getSearchName();

            inboxStyle.addLine(inboxStyleLine);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID);

        Intent contentIntent = new Intent(context, LoginActivity.class);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context, 0, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setStyle(inboxStyle)
                .setContentTitle(contentTitle)
                .setContentText(contextText)
                .setSmallIcon(R.drawable.ic_search)
                .setContentIntent(contentPendingIntent)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSubText(Integer.toString(totalNewNotifications))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static class NewNotification {
        private final String searchId;
        private final String searchName;
        private final int numberOfNewResults;

        public NewNotification(String searchId, String searchName, int numberOfNewResults) {
            this.searchId = searchId;
            this.searchName = searchName;
            this.numberOfNewResults = numberOfNewResults;
        }

        public String getSearchId() {
            return searchId;
        }

        public String getSearchName() { return searchName; }

        public int getNumberOfNewResults() {
            return numberOfNewResults;
        }
    }

}
