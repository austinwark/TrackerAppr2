package com.sandboxcode.trackerappr2.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.adapters.on_boarding.OnBoardingPagerAdapter;
import com.sandboxcode.trackerappr2.fragments.OnBoardingPagerFragment;

public class OnBoardingActivity extends AppCompatActivity {

    private OnBoardingPagerFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        fragment = OnBoardingPagerFragment.newInstance();
        transaction.replace(R.id.on_boarding_layout_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        fragment.handleOnBackPressed();
    }

}