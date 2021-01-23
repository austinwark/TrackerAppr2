package com.sandboxcode.trackerappr2.adapters.detail;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sandboxcode.trackerappr2.fragments.DetailDemoFragment;
import com.sandboxcode.trackerappr2.fragments.DetailPagerFragment;

public class DetailPagerAdapter extends FragmentStateAdapter {

    public DetailPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new DetailDemoFragment();
        Bundle args = new Bundle();
        args.putInt(DetailDemoFragment.ARG_OBJECT, position + 1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 100;
    }
}
