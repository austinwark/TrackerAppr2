package com.sandboxcode.trackerappr2.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.adapters.result.ResultsAdapter;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.viewmodels.MainSharedViewModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

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
        activityContext = getActivity().getApplicationContext();
        FragmentManager fragmentManager = getParentFragmentManager();
        // TODO - add searchID
        adapter = new ResultsAdapter(activityContext, R.layout.result_list_item, fragmentManager, searchId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Results");

        MainSharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);
        viewModel.getSearchResults(searchId)
                .observe(getViewLifecycleOwner(), results -> {
                    adapter.setResults(results);
                    crossFade();
                });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activityContext);

        resultRecyclerView = view.findViewById(R.id.results_view);
        resultRecyclerView.setVisibility(View.GONE);
        resultRecyclerView.setHasFixedSize(true);
        resultRecyclerView.setLayoutManager(layoutManager);

        loaderLayout = view.findViewById(R.id.results_layout_loader);

        // TODO -- Why doesn't this work???
        LinearSnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(resultRecyclerView);
        resultRecyclerView.setAdapter(adapter);

    }

    // TODO - Fix slight lag when choosing a different search (previous results show for 1/2 a sec)
    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        adapter.setResults(null);
        super.onDestroyView();
    }

    public void crossFade() {
        resultRecyclerView.setAlpha(0f);
        resultRecyclerView.setVisibility(View.VISIBLE);

        resultRecyclerView.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        loaderLayout.animate()
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
