package com.sandboxcode.trackerappr2.adapters.detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.sandboxcode.trackerappr2.fragments.DetailFragment;
import com.sandboxcode.trackerappr2.fragments.DetailPagerFragment;
import com.sandboxcode.trackerappr2.models.ResultModel;

import java.util.List;

public class DetailPagerAdapter extends FragmentStateAdapter {

    private DetailPagerFragment fragment;
    private List<ResultModel> results;
    private int itemCount;
    private String searchId;

    public DetailPagerAdapter(Fragment fragment, List<ResultModel> results, String searchId) {
        super(fragment);
        this.fragment = (DetailPagerFragment) fragment;
        this.results = results;
        itemCount = results.size();
        this.searchId = searchId;
    }



    @NonNull
    @Override
    public Fragment createFragment(int position) {
        DetailFragment fragment = DetailFragment.newInstance(position, searchId, results.get(position));
        return fragment;
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }
}
