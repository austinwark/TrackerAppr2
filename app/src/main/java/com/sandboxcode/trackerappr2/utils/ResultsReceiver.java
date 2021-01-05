package com.sandboxcode.trackerappr2.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.activities.MainActivity;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.ArrayList;

public class ResultsReceiver extends BroadcastReceiver implements AsyncResponse {

    private NotificationManager notificationManager;
    private Context context;
    private String userId;

    private static final String TAG = "ResultsReceiver";
    // Notification ID.
    private static final int NOTIFICATION_ID = 0;
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";

    private static final String USER_ID_EXTRA = "user_id";

    private static final FirebaseAuth AUTH_REF = FirebaseAuth.getInstance();
    private static final DatabaseReference DATABASE_REF = FirebaseDatabase.getInstance().getReference();


    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        userId = intent.getStringExtra(USER_ID_EXTRA);

        Log.d(TAG, "Extra: " + userId);

        if (userId != null && !userId.isEmpty()) {
            notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            refreshResults(userId);
        }


    }

    private void refreshResults(String userId) {

        // Get search IDs
        DATABASE_REF.child("queries").child(userId)
                .addListenerForSingleValueEvent(new SearchesListener(userId));
    }

    private class SearchesListener implements ValueEventListener {
        private String userId;

        public SearchesListener(String userId) {
            this.userId = userId;
        }

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ArrayList<SearchModel> searches = new ArrayList<>();
            if (snapshot.hasChildren()) {

                // Get the user's searches
                for (DataSnapshot child : snapshot.getChildren())
                    searches.add(child.getValue(SearchModel.class));

                for (SearchModel search : searches) {
                    WebScraper scraper = new WebScraper(search, DATABASE_REF, userId);
                    scraper.setDelegate(ResultsReceiver.this);
                    scraper.execute();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    }

    @Override
    public void processResults(ArrayList<ResultModel> searchResults, String searchId) {
        DatabaseReference resultsRef = DATABASE_REF.child("results").child(searchId);

        resultsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ResultModel> currentResults = new ArrayList<>();
                int numberOfResults = 0;
                int numberOfNewResults = 0;

                // Get the current results of the search
                if (snapshot.hasChildren())
                    for (DataSnapshot currentResult : snapshot.getChildren())
                        currentResults.add(currentResult.getValue(ResultModel.class));

                if (!currentResults.isEmpty()) {

                    // Check each scrapedResult to see if it already exists
                    for (ResultModel scrapedResult : searchResults) {
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
                    numberOfNewResults = searchResults.size();

                // Reset the currentResults document in firebase
                resultsRef.setValue(null);

                // Save each new result and keep track of the total count
                for (ResultModel result : searchResults) {
                    resultsRef.child(result.getVin()).setValue(result);
                    numberOfResults++;
                }

                // Set number of results in search document
                DATABASE_REF.child("queries").child(userId).child(searchId)
                        .child("numberOfResults").setValue(numberOfResults);
                DATABASE_REF.child("queries").child(userId).child(searchId)
                        .child("numberOfNewResults").setValue(numberOfNewResults);

                if (numberOfNewResults > 0)
                    deliverNotification(context, numberOfNewResults);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void deliverNotification(Context context, int numberOfNewResults) {

        Intent contentIntent = new Intent(context, MainActivity.class); // TODO - MainActivity?
        PendingIntent contentPendingIntent = PendingIntent.getActivity(
                context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_search)
                .setContentTitle("New results!")
                .setContentText("A search has " + numberOfNewResults + " new results!")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
}
