package com.sandboxcode.trackerappr2.activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.DetailFragment;
import com.sandboxcode.trackerappr2.fragments.ResultsFragment;
import com.sandboxcode.trackerappr2.fragments.SearchesFragment;
import com.sandboxcode.trackerappr2.utils.ResultsReceiver;
import com.sandboxcode.trackerappr2.viewmodels.MainSharedViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private MainSharedViewModel viewModel;

    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final String USER_ID_EXTRA = "user_id";
    private String userId;
    private NotificationManager notificationManager;

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
            if (fragment instanceof SearchesFragment) {
                Log.d(TAG, "Searches Fragment Open onSaveInstance");
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.main_fragment_container, fragment);
            }
            if (fragment instanceof ResultsFragment) {
                Log.d(TAG, "Result Fragment Open");
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment_container, fragment);
            }
            if (fragment instanceof DetailFragment) {
                Log.d(TAG, "Detail Fragment Open");
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment_container, fragment);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbarTop = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbarTop);

        viewModel = new ViewModelProvider(this).get(MainSharedViewModel.class);
        viewModel.getUserSignedIn().observe(this, userSignedIn -> {
            viewModel.setSearchesListener();
            userId = viewModel.getUserId();
            startUpdatingResultsBroadcast();
        });
        viewModel.getSignUserOut().observe(this, signOut -> {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(task -> {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    });
        });

        if (savedInstanceState == null) {
            SearchesFragment fragment = new SearchesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }


    }

    private void startUpdatingResultsBroadcast() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(this, ResultsReceiver.class);
        notifyIntent.putExtra(USER_ID_EXTRA, userId);

        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT); // TODO -- check flag in docs

        boolean alarmAlreadyRunning = (PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);

        Log.d(TAG, "ALREADY RUNNING: " + alarmAlreadyRunning);

        // TODO -- check if alarms are running instead of cancel?
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(notifyPendingIntent);
        long repeatInterval = 5000;
        long triggerTime = SystemClock.elapsedRealtime();
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);

        createNotificationChannel();
    }

    public void createNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "Checking for new results",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            notificationChannel.setDescription("Notifies user that app is checking for new results");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
        if (fragment instanceof SearchesFragment) {
            if (viewModel.getEditMenuOpen().getValue() == View.VISIBLE) {
                viewModel.toggleEdit();
                return;
            }
        }
        super.onBackPressed();
    }
}