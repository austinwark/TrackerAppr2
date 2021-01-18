package com.sandboxcode.trackerappr2.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.activities.LoginActivity;
import com.sandboxcode.trackerappr2.viewmodels.MainViewModel;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";
    private SharedPreferences sharedPreferences;
    private MainViewModel viewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());

    }

    private boolean toggleDarkMode(Preference preference) {
        boolean isAlreadyEnabled = sharedPreferences
                .getBoolean(preference.getKey(), true);

        AppCompatDelegate.setDefaultNightMode(isAlreadyEnabled ?
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        return true;
    }

    private boolean showChangePasswordDialog() {
        ChangePasswordFragment changePasswordFragment = ChangePasswordFragment.newInstance();
        changePasswordFragment.show(getParentFragmentManager(), "ChangePasswordFragment");
        return true;
    }

    public boolean signUserOut() {
        viewModel.signUserOut();
        return true;
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case "dark_mode":
                return toggleDarkMode(preference);
            case "change_password":
                return showChangePasswordDialog();
            case "logout":
                return signUserOut();
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        if (((AppCompatActivity) getActivity()) != null) {

            ActionBar supportActionBar = ((AppCompatActivity) getActivity())
                    .getSupportActionBar();

            if (supportActionBar != null) {
                supportActionBar.setTitle("Settings");
                supportActionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        viewModel.getSignUserOut().observe(this, signOut ->
                startActivity(new Intent(getActivity(), LoginActivity.class)));

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        viewModel.handleOnOptionsItemSelected(item.getItemId());
//        return super.onOptionsItemSelected(item);
//    }

}
