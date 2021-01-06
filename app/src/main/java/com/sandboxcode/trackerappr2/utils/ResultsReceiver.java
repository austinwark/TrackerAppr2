package com.sandboxcode.trackerappr2.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.InboxStyle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.activities.LoginActivity;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.ArrayList;
import java.util.Map;

public class ResultsReceiver extends BroadcastReceiver implements DailyAsyncResponse {

    private static final String TAG = "ResultsReceiver";
    // Notification ID.
    private static final int NOTIFICATION_ID = 0;
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";
    private static final String USER_ID_EXTRA = "user_id";
    private static final DatabaseReference DATABASE_REF = FirebaseDatabase.getInstance().getReference();
    private NotificationManager notificationManager;
    private Context context;
    private String userId;
    private int numberOfNotifications;

    private ArrayList<NewNotification> newNotifications = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        userId = intent.getStringExtra(USER_ID_EXTRA);

        Log.d(TAG, "Extra: " + userId);

        if (userId != null && !userId.isEmpty()) {
            notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            numberOfNotifications = 0;
            refreshResults(userId);
        }


    }

    private void refreshResults(String userId) {
        // Get search IDs
        DATABASE_REF.child("queries").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<SearchModel> searches = new ArrayList<>();
                        if (snapshot.hasChildren()) {

                            // Get the user's searches
                            for (DataSnapshot child : snapshot.getChildren())
                                searches.add(child.getValue(SearchModel.class));

                            DailyWebScraper scraper = new DailyWebScraper(searches, DATABASE_REF, userId);
                            scraper.setDelegate(ResultsReceiver.this);
                            scraper.execute();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void processResults(Map<String, ArrayList<ResultModel>> results) {

        // Ref pointing to the user's search results grouped by search ID
        DatabaseReference searchesRef = DATABASE_REF.child("results").child(userId);

        searchesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Close method if user has no searches
                if (!snapshot.hasChildren())
                    return;

                // Counter used to create notifications after each set of results is checked
                long totalSearches = snapshot.getChildrenCount();
                int searchesCount = 0;

                //  For each group of search results
                for (DataSnapshot searchInDb : snapshot.getChildren()) {

                    String searchId = searchInDb.getKey();
                    if (searchId == null)
                        return;

                    // Ref pointing to results from one search
                    DatabaseReference resultsRef = searchesRef.child(searchId);

                    ArrayList<ResultModel> newSearchResults = results.get(searchId);

                    if (newSearchResults == null) // Protects again Null Pointer Exception
                        newSearchResults = new ArrayList<>();

                    ArrayList<ResultModel> currentResults = new ArrayList<>();

                    int numberOfResults = 0; // total results for the search
                    int numberOfNewResults = 0; // total new (unseen) results for the search

                    // Load the search's current results
                    for (DataSnapshot currentResult : searchInDb.getChildren())
                        currentResults.add(currentResult.getValue(ResultModel.class));

                    // If there are existing results in database
                    if (!currentResults.isEmpty()) {

                        // Check each scrapedResult to see if it already exists
                        for (ResultModel scrapedResult : newSearchResults) {
                            if (currentResults.contains(scrapedResult)) {

                                // Get the already existing result from currentResults
                                ResultModel matchingCurrentResult =
                                        currentResults.get(currentResults.indexOf(scrapedResult));

                                // If the already existing result has been viewed by user -- update
                                // the scrapedResult with the same value
                                if (!matchingCurrentResult.getIsNewResult()) {
                                    scrapedResult.setIsNewResult(false); // true by default

                                } else // If currentResult has already been viewed
                                    numberOfNewResults++;

                            } else // If scrapedResult has not been scraped yet
                                numberOfNewResults++;
                        }
                    } else // If there are no existing current results
                        numberOfNewResults = newSearchResults.size();

                    resultsRef.setValue(null); // reset the search's result document

                    // Save each new search result and keep track of total results
                    for (ResultModel result : newSearchResults) {
                        resultsRef.child(result.getVin()).setValue(result);
                        numberOfResults++;
                    }

                    // Set number of results (total and new) in search document
                    DATABASE_REF.child("queries").child(userId).child(searchId)
                            .child("numberOfResults").setValue(numberOfResults);
                    DATABASE_REF.child("queries").child(userId).child(searchId)
                            .child("numberOfNewResults").setValue(numberOfNewResults);

                    // Create a new notification if there is one or more new results
                    if (numberOfNewResults > 0) {
                        numberOfNotifications++;
                        newNotifications.add(new NewNotification(searchId, numberOfNewResults));
                    }

                    // Send notification once all searches have been checked
                    searchesCount++;
                    if (searchesCount == totalSearches) {
                        int totalNewNotifications = 0;
                        for (NewNotification newNotification : newNotifications)
                            totalNewNotifications += newNotification.getNumberOfNewResults();

                        deliverSummaryNotification(context, newNotifications, totalNewNotifications);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // No need to notify user of firebase read error
            }
        });
    }

    private void deliverSummaryNotification(Context context,
                                            ArrayList<NewNotification> newNotifications,
                                            int totalNewNotifications) {

        String bigContentTitle;
        String summaryText;
        String contentTitle;
        String contextText;

        bigContentTitle = totalNewNotifications + " new results from "
                + newNotifications.size() + " searches.";
        summaryText = "New Results";
        contentTitle = totalNewNotifications
                + (totalNewNotifications > 1 ? " new results" : "new result");
        contextText = "Click here to see the new results";

        InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle(bigContentTitle)
                .setSummaryText(summaryText);

        for (NewNotification notification : newNotifications) {
            String inboxStyleLine = notification.getNumberOfNewResults() +  " new results in "
                    + notification.getSearchId();

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
        private String searchId;
        private int numberOfNewResults;

        public NewNotification(String searchId, int numberOfNewResults) {
            this.searchId = searchId;
            this.numberOfNewResults = numberOfNewResults;
        }

        public String getSearchId() {
            return searchId;
        }

        public int getNumberOfNewResults() {
            return numberOfNewResults;
        }
    }

}
