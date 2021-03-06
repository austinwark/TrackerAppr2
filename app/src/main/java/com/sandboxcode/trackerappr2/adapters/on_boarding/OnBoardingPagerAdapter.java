package com.sandboxcode.trackerappr2.adapters.on_boarding;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sandboxcode.trackerappr2.fragments.OnBoardingFragment;

public class OnBoardingPagerAdapter extends FragmentStateAdapter {

    private int[] images;
    private int itemCount;

    public OnBoardingPagerAdapter(Fragment fragment, int[] images) {
        super(fragment);
        this.images = images;
        this.itemCount = images.length;
    }

    // TODO -- CHANGE PAGERADAPTER TO RECYCLERVIEW LIKE IN EXAMPLE
    @Override
    public Fragment createFragment(int position) {
        OnBoardingFragment fragment = OnBoardingFragment.newInstance(images[position]);
        return fragment;
    }

    @Override
    public int getItemCount() { return itemCount; }
}
