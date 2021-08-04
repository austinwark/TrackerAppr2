package com.sandboxcode.trackerappr2.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.adapters.detail.DetailPagerAdapter;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.viewmodels.DetailViewModel;

import org.parceler.Parcels;

import java.util.List;

import static java.lang.Math.abs;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailPagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailPagerFragment extends Fragment {

    private static final String TAG = DetailPagerFragment.class.getSimpleName();
    private static final String ARG_RESULTS = "results";
    private static final String ARG_SEARCH_ID = "search_id";

    private DetailPagerAdapter detailPagerAdapter;
    private ViewPager2 viewPager;
    private TextView counterTextView;

    private List<ResultModel> results;
    private String searchId;

    public DetailPagerFragment() {
        // Required empty public constructor
    }

    public static DetailPagerFragment newInstance(List<ResultModel> results, String searchId) {
        DetailPagerFragment fragment = new DetailPagerFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RESULTS, Parcels.wrap(results));
        args.putString(ARG_SEARCH_ID, searchId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override // TODO -- Why is make always null????
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            results = Parcels.unwrap(args.getParcelable(ARG_RESULTS));
            searchId = args.getString(ARG_SEARCH_ID);
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
        DetailViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(DetailViewModel.class);

        viewPager = view.findViewById(R.id.details_view_pager);
        counterTextView = view.findViewById(R.id.detail_pager_text_counter);

        detailPagerAdapter = new DetailPagerAdapter(this, results, searchId);
        setUpViewPager(viewPager);

        viewPager.setAdapter(detailPagerAdapter);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {

                // Set counter above slide
                int pagePosition = position + 1;
                int total = results.size();
                String newText = pagePosition + "/" + total;
                counterTextView.setText(newText);

                // Set result as viewed in DB
                ResultModel selectedResult = results.get(position);
                viewModel.setResultHasBeenViewed(selectedResult.getVin(), searchId);
            }
        });
    }

    private void setUpViewPager(ViewPager2 viewPager) {
        viewPager.setOffscreenPageLimit(1);

//        viewPager.setPageTransformer(new CustomPageTransformer());
        viewPager.addItemDecoration(new HorizontalMarginItemDecoration(getContext()));
    }

    public void updateCounter(int position, int total) {

    }

    private class CustomPageTransformer implements ViewPager2.PageTransformer {

        @Override
        public void transformPage(@NonNull View page, float position) {

            float nextItemVisiblePx = getResources().getDimension(R.dimen.viewpager_next_item_visible);
            float currentItemHorizontalMarginPx = getResources().getDimension(R.dimen.viewpager_current_item_horizontal_margin);
            float pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx;

            page.setTranslationX(pageTranslationX * -1 * position);
            page.setScaleY(1 - (0.25f * abs(position)));
        }
    }

    private class HorizontalMarginItemDecoration extends RecyclerView.ItemDecoration {

        private int horizontalMarginInPx;
        private Context context;

        public HorizontalMarginItemDecoration(Context context) {
            this.context = context;
            this.horizontalMarginInPx = (int) context.getResources().getDimension(R.dimen.viewpager_current_item_horizontal_margin);
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = horizontalMarginInPx;
            outRect.right = horizontalMarginInPx;
        }
    }

}

