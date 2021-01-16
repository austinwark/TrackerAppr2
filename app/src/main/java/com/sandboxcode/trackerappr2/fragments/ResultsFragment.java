package com.sandboxcode.trackerappr2.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
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
    private int numberOfResults;
    private MainSharedViewModel viewModel;
    private ArrayList<ResultModel> unsortedResults;

    private RecyclerView resultRecyclerView;
    private ConstraintLayout loaderLayout;
    private TextView noResultsLayout;

    private String searchId;
    private ResultsAdapter adapter;

    private int shortAnimationDuration;
    private AutoCompleteTextView sortDropdown;

    private LinearLayout sortLayout;
    private ChipGroup sortChipGroup;
    private Chip sortChipPriceAsc;
    private Chip getSortChipPriceDesc;

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchId = getArguments().getString("ID");
        }
        FragmentManager fragmentManager = getParentFragmentManager();

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        adapter = new ResultsAdapter(R.layout.result_list_item, fragmentManager, searchId, this);

        setHasOptionsMenu(true);
        numberOfResults = 0;
    }

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


        viewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);

        noResultsLayout = view.findViewById(R.id.results_text_no_results);
        resultRecyclerView = view.findViewById(R.id.results_view);
        loaderLayout = view.findViewById(R.id.results_layout_loader);
        sortLayout = view.findViewById(R.id.results_layout_sort_options);

//        sortDropdown = view.findViewById(R.id.results_dropdown_sort);
//        List<String> sortOptions = SortOption.getValues();
//        ArrayAdapter<String> sortOptionsAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_list_item, sortOptions);
//        sortDropdown.setAdapter(sortOptionsAdapter);
//
//        sortDropdown.setOnItemClickListener((parent, view1, position, id) -> {
//            String selection = (String) parent.getItemAtPosition(position);
//            viewModel.handleSortDropdownSelection(SortOption.getSortOption(selection));
//        });

        sortChipGroup = view.findViewById(R.id.results_chip_group_sort);

        sortChipGroup.setOnCheckedChangeListener((group, id) -> {
            Chip sortChip = view.findViewById(id);
            if (sortChip == null || !sortChip.isChecked()) {
                adapter.setResults(unsortedResults);
                adapter.notifyDataSetChanged();
            } else {
                String selection = view.findViewById(id).getTag().toString();
                SortOption sortOption = SortOption.getSortOption(selection);
                if (sortOption != null)
                    viewModel.handleSortSelection(sortOption);
            }
        });

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(activityContext, 2);
        resultRecyclerView.setVisibility(View.GONE);
        resultRecyclerView.setHasFixedSize(true);
        resultRecyclerView.setLayoutManager(layoutManager);

        GravitySnapHelper gravitySnapHelper = new GravitySnapHelper(Gravity.TOP);
        gravitySnapHelper.attachToRecyclerView(resultRecyclerView);

        resultRecyclerView.setAdapter(adapter);


        viewModel.getSearchResults(searchId).observe(getViewLifecycleOwner(), results -> {
            unsortedResults = results;
            adapter.setResults(results);
            if (results.isEmpty())
                crossFade(noResultsLayout, loaderLayout);
            else
                crossFade(resultRecyclerView, loaderLayout);

            // Refresh menu to potentially enable/disable sortResultsItem
            numberOfResults = results.size();
            requireActivity().invalidateOptionsMenu();
        });
        viewModel.getSortCompleted().observe(getViewLifecycleOwner(), completed ->
                adapter.notifyDataSetChanged()
                );
        viewModel.getSortMenuOpen().observe(getViewLifecycleOwner(), open -> {
            sortLayout.setVisibility(open);
        });

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_results, menu);

        MenuItem sortResultsItem = menu.findItem(R.id.results_action_sort);
        sortResultsItem.setEnabled((numberOfResults > 0));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        viewModel.handleOnOptionsItemSelected(menuItem.getItemId());
        return false;
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

    public enum SortOption {
        PRICE_ASC("Price asc."),
        PRICE_DESC("Price desc."),
        YEAR_ASC("Year asc."),
        YEAR_DESC("Year desc.");

        public final String value;

        SortOption(String value) { this.value = value; }

        public static SortOption getSortOption(String selection) {
            SortOption desiredSortOption = null;
            for (SortOption sortOption : SortOption.values()) {
                if (sortOption.value.equalsIgnoreCase(selection))
                    desiredSortOption = sortOption;
            }
            return desiredSortOption;
        }
    }
}
