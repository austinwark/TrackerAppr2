package com.sandboxcode.trackerappr2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.OnBoardingFragment;
import com.sandboxcode.trackerappr2.fragments.OnBoardingPagerFragment;
import com.sandboxcode.trackerappr2.viewmodels.OnBoardingViewModel;

import java.util.ArrayList;
import java.util.Arrays;

public class OnBoardingActivity extends AppCompatActivity {

    private OnBoardingPagerFragment fragment;
    OnBoardingViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment = OnBoardingPagerFragment.newInstance();
        transaction.replace(R.id.on_boarding_layout_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        viewModel = new ViewModelProvider(this).get(OnBoardingViewModel.class);
        viewModel.getImageIds().observe(this, imageIds -> {
            fragment.setImages(imageIds);
        });
    }

    @Override
    public void onBackPressed() {
        fragment.handleOnBackPressed();
    }

}

