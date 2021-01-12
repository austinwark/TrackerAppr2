package com.sandboxcode.trackerappr2.activities;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.firebase.ui.auth.AuthUI;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.DetailFragment;
import com.sandboxcode.trackerappr2.fragments.ResultsFragment;
import com.sandboxcode.trackerappr2.fragments.SearchesFragment;
import com.sandboxcode.trackerappr2.fragments.SettingsFragment;
import com.sandboxcode.trackerappr2.utils.ResultsReceiver;
import com.sandboxcode.trackerappr2.viewmodels.MainSharedViewModel;

public class MainActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "MainActivity";
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final String USER_ID_EXTRA = "user_id";
    private MainSharedViewModel viewModel;
    private String userId;
    private NotificationManager notificationManager;

    SharedPreferences sharedPreferences;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);
            if (fragment instanceof SearchesFragment) {
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.main_fragment_container, fragment);
            }
            if (fragment instanceof ResultsFragment) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment_container, fragment);
            }
            if (fragment instanceof DetailFragment) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment_container, fragment);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        Toolbar toolbarTop = findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbarTop);

        viewModel = new ViewModelProvider(this).get(MainSharedViewModel.class);
        viewModel.getUserSignedIn().observe(this, userSignedIn -> {
            viewModel.setSearchesListener();
            userId = viewModel.getUserId();
            startUpdatingResultsBroadcast();
//            retrieveSavedSettings(userId);
        });
        viewModel.getOpenSettingsScreen().observe(this, openSettings -> {
            SettingsFragment settingsFragment = new SettingsFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_fragment_container, settingsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // TODO -- Set dark mode here or elsewhere?
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean darkModeEnabled = sharedPreferences.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(darkModeEnabled ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        if (savedInstanceState == null) {
            SearchesFragment fragment = new SearchesFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.main_fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    //    public void retrieveSavedSettings(String userId) {
//        viewModel.getSavedSettings(userId);
//    }

    private void clearNotificationServices() {
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.cancelAll();
        Intent notifyIntent = new Intent(this, ResultsReceiver.class);
        notifyIntent.putExtra(USER_ID_EXTRA, userId);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(notifyPendingIntent);
    }

    private void startUpdatingResultsBroadcast() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(this, ResultsReceiver.class);
        notifyIntent.putExtra(USER_ID_EXTRA, userId);

        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(notifyPendingIntent);
        long repeatInterval = 1800000; // half hour
        long triggerTime = SystemClock.elapsedRealtime();
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, repeatInterval, notifyPendingIntent);

        createNotificationChannel();
    }

    public void createNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID,
                    "TrackerAppr",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setDescription("Notifies user that app is checking for new results");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment_container);

        // If fragment is not SearchesFragment or edit menu DNE or edit menu is closed
        if ((fragment instanceof SearchesFragment)
                && viewModel.getEditMenuOpen().getValue() != null
                && viewModel.getEditMenuOpen().getValue() == View.VISIBLE)
            viewModel.toggleEdit();
        else if ((fragment instanceof SearchesFragment))
            finish();
        else
            super.onBackPressed();
    }

}