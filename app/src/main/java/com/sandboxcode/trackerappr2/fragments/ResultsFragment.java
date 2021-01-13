package com.sandboxcode.trackerappr2.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.adapters.result.ResultsAdapter;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.utils.GravitySnapHelper;
import com.sandboxcode.trackerappr2.viewmodels.MainSharedViewModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {

    private static final String TAG = "ResultsFragment";
    private Context activityContext;

    private RecyclerView resultRecyclerView;
    private ConstraintLayout loaderLayout;
    private ConstraintLayout noResultsLayout;

    private final List<ResultModel> resultList = new ArrayList<>();
    private String searchId;
    private ResultsAdapter adapter;


    private int shortAnimationDuration;

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("ARRAY1", Parcels.wrap(resultList));
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchId = getArguments().getString("ID");
        }
        FragmentManager fragmentManager = getParentFragmentManager();

        adapter = new ResultsAdapter(R.layout.result_list_item, fragmentManager, searchId, this);

    }

//    public void viewDetails(ResultModel result, String searchId, ImageView thumbnail) {
//        Bundle args = new Bundle();
//        args.putParcelable("RESULT", Parcels.wrap(result));
//        args.putString("SEARCH_ID", searchId);
//
//        DetailFragment fragment = new DetailFragment();
//        fragment.setArguments(args);
//
//        fragment.getParentFragmentManager()
//                .beginTransaction()
//                .setReorderingAllowed(true)
//                .addSharedElement(thumbnail, thumbnail.getTransitionName())
//                .replace(R.id.main_fragment_container,
//                        fragment,
//                        DetailFragment.class.getSimpleName())
//                .addToBackStack(null)
//                .commit();
//    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (((AppCompatActivity) getActivity()) != null
                && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {

            Objects.requireNonNull(((AppCompatActivity) getActivity())
                    .getSupportActionBar()).setTitle("Results");
        }

        noResultsLayout = view.findViewById(R.id.results_layout_no_results);
        resultRecyclerView = view.findViewById(R.id.results_view);
        loaderLayout = view.findViewById(R.id.results_layout_loader);

        MainSharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);
        viewModel.getSearchResults(searchId).observe(getViewLifecycleOwner(), results -> {
                    adapter.setResults(results);
                    if (results.isEmpty())
                        crossFade(noResultsLayout, loaderLayout);
                    else
                        crossFade(resultRecyclerView, loaderLayout);
                });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activityContext, 2);
        resultRecyclerView.setVisibility(View.GONE);
        resultRecyclerView.setHasFixedSize(true);
        resultRecyclerView.setLayoutManager(layoutManager);

        GravitySnapHelper gravitySnapHelper = new GravitySnapHelper(Gravity.TOP);
        gravitySnapHelper.attachToRecyclerView(resultRecyclerView);

        resultRecyclerView.setAdapter(adapter);

    }

    // TODO - Fix slight lag when choosing a different search (previous results show for 1/2 a sec)
    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        adapter.setResults(null);
        super.onDestroyView();
    }

    public void crossFade(View view1, View view2) {
        view1.setAlpha(0f);
        view1.setVisibility(View.VISIBLE);

        view1.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        view2.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loaderLayout.setVisibility(View.GONE);
                    }
                });
    }

}
