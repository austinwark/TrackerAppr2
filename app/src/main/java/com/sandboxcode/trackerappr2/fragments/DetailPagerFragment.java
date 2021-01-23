package com.sandboxcode.trackerappr2.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.adapters.detail.DetailPagerAdapter;
import com.sandboxcode.trackerappr2.models.ResultModel;

import org.parceler.Parcels;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailPagerFragment extends Fragment {
    private static final String TAG = DetailPagerFragment.class.getSimpleName();
    private static final String RESULTS_BUNDLE_KEY = "results_key";

    private DetailPagerAdapter detailPagerAdapter;
    private ViewPager2 viewPager;

    private List<ResultModel> results;

    public DetailPagerFragment() {
        // Required empty public constructor
    }

    // TODO -- pass arraylist of ResultModels to display in Pager!!!!!!
    public static DetailPagerFragment newInstance(List<ResultModel> results) {
        DetailPagerFragment fragment = new DetailPagerFragment();
        Bundle args = new Bundle();
        args.putParcelable(RESULTS_BUNDLE_KEY, Parcels.wrap(results));
        fragment.setArguments(args);
        return fragment;
    }

    @Override // TODO -- Why is make always null????
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            results = Parcels.unwrap(getArguments().getParcelable(RESULTS_BUNDLE_KEY));
            for (ResultModel result : results)
                Log.d(TAG, result.toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_pager, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        detailPagerAdapter = new DetailPagerAdapter(this);
        viewPager = view.findViewById(R.id.details_view_pager);
        viewPager.setAdapter(detailPagerAdapter);
    }
}

