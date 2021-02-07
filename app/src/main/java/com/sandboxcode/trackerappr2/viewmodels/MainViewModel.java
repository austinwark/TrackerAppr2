package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.sandboxcode.trackerappr2.BuildConfig;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.ResultsFragment;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.repositories.AuthRepository;
import com.sandboxcode.trackerappr2.repositories.SearchRepository;
import com.sandboxcode.trackerappr2.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private final SearchRepository searchRepository;
    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> userSignedIn;
    private final MutableLiveData<Boolean> signUserOut;
    private final SingleLiveEvent<Boolean> startOnBoarding = new SingleLiveEvent<>();

    public MainViewModel(Application application) {
        super(application);
        searchRepository = new SearchRepository();
        authRepository = new AuthRepository();

        userSignedIn = authRepository.getUserSignedIn();
        signUserOut = authRepository.getSignUserOut();
    }

    public MutableLiveData<Boolean> getUserSignedIn() {
        return userSignedIn;
    }

    public void signUserOut() {
        authRepository.signUserOut();
    }

    public String getUserId() {
        return authRepository.getUserId();
    }

    public MutableLiveData<Boolean> getSignUserOut() {
        return signUserOut;
    }

    public SingleLiveEvent<Boolean> getStartOnBoarding() { return startOnBoarding; }


        public void checkFirstRun() {
        final String PREFS_NAME = "PrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOES_NOT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getApplication()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOES_NOT_EXIST);

        // Check for first run or update
        if (currentVersionCode == savedVersionCode) {
            Log.d(TAG, "Normal Run");
            return;
        } else if (savedVersionCode == DOES_NOT_EXIST) {
            Log.d(TAG, "New Install");
            startOnBoarding.setValue(true);
        } else if (currentVersionCode > savedVersionCode) {
            Log.d(TAG, "Update");
        }

        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

}
