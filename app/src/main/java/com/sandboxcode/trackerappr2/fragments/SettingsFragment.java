package com.sandboxcode.trackerappr2.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.viewmodels.AuthViewModel;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "SettingsFragment";
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());

        Preference darkModePreference = findPreference("dark_mode");

//        if (darkModePreference != null) {
//            darkModePreference.setOnPreferenceClickListener(preference -> {
//
//                boolean isAlreadyEnabled = sharedPreferences
//                        .getBoolean(preference.getKey(), true);
//
//                AppCompatDelegate.setDefaultNightMode(isAlreadyEnabled ?
//                        AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
//
//                return true;
//            });
//        }
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

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case "dark_mode":
                return toggleDarkMode(preference);
            case "change_password":
                return showChangePasswordDialog();
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        if (((AppCompatActivity) getActivity()) != null
                && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {

            Objects.requireNonNull(((AppCompatActivity) getActivity())
                    .getSupportActionBar()).setTitle("Settings");
        }

    }

}
