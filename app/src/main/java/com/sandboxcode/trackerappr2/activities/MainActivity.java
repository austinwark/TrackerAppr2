package com.sandboxcode.trackerappr2.activities;

import android.content.Intent;
import android.os.Bundle;
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
import com.sandboxcode.trackerappr2.viewmodels.MainSharedViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private MainSharedViewModel viewModel;

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
            if (!userSignedIn)
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