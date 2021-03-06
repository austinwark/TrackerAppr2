package com.sandboxcode.trackerappr2.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.activities.MainActivity;
import com.sandboxcode.trackerappr2.adapters.on_boarding.OnBoardingPagerAdapter;

public class OnBoardingPagerFragment extends Fragment {

    private static final String TAG = OnBoardingPagerFragment.class.getSimpleName();

    private OnBoardingPagerAdapter onBoardingPagerAdapter;
    private ViewPager2 viewPager;
    private int[] images = {R.drawable.screenshot1, R.drawable.screenshot2};
    private LinearLayout dotsLayout;
    private Button skipButton;
    private Button nextButton;
    private TextView[] dots;

    public OnBoardingPagerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OnBoardingPagerFragment newInstance() {
        OnBoardingPagerFragment fragment = new OnBoardingPagerFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_on_boarding_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        hideSystemUI();
        // Hide action bar
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        viewPager = view.findViewById(R.id.on_boarding_view_pager);
        dotsLayout = view.findViewById(R.id.on_boarding_layout_dots);
        skipButton = view.findViewById(R.id.on_boarding_skip_button);
        nextButton = view.findViewById(R.id.on_boarding_next_button);
        initializeViewPager();
    }

    private void initializeViewPager() {

        onBoardingPagerAdapter = new OnBoardingPagerAdapter(this, images);
        viewPager.setAdapter(onBoardingPagerAdapter);

        addBottomDots(0);
        viewPager.registerOnPageChangeCallback(pageChangeCallback);
        viewPager.setOffscreenPageLimit(1);

        skipButton.setOnClickListener(v -> {
            launchHomeScreen();
        });
        nextButton.setOnClickListener(v -> {
            int nextPage = getItem(1);
            if (nextPage < images.length)
                viewPager.setCurrentItem(nextPage);
            else
                launchHomeScreen();
        });
    }

    private void launchHomeScreen() {
        startActivity(new Intent(getContext(), MainActivity.class));
    }

    OnPageChangeCallback pageChangeCallback = new OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            addBottomDots(position);

            if (position == images.length - 1) {
                nextButton.setText("GOT IT");
                skipButton.setVisibility(View.GONE);
            } else {
                nextButton.setText("NEXT");
                skipButton.setVisibility(View.VISIBLE);
            }
        }
    };

    private void addBottomDots(int currentPage) {
        dots = new TextView[images.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(getContext());
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.on_boarding_dots_inactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.on_boarding_dots_active));
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // TODO -- Move logic to ViewModel
        if (!((AppCompatActivity) getActivity()).getSupportActionBar().isShowing())
            // Make sure action bar is visible
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        Log.d(TAG, "onDestroy -------------------");
    }

    public void handleOnBackPressed() {
        int currentPage = viewPager.getCurrentItem();
        if (currentPage > 0)
            viewPager.setCurrentItem((currentPage - 1));
        else
            launchHomeScreen();
    }

}