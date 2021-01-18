package com.sandboxcode.trackerappr2.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Intent;
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

import androidx.annotation.NonNull;
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
import com.sandboxcode.trackerappr2.viewmodels.ResultsViewModel;

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
    private static final String CHECKED_RESULTS_TAG = "checked_results";

    private ResultsViewModel viewModel;
    private ArrayList<ResultModel> unsortedResults;
    private int numberOfResults;
    private int numberOfCheckedResults;

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

    private MenuItem shareResultsItem;

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> checkedResults = (ArrayList<String>) viewModel.getCheckedResults();
        if (!checkedResults.isEmpty())
            outState.putStringArrayList(CHECKED_RESULTS_TAG, checkedResults);
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


        viewModel = new ViewModelProvider(requireActivity()).get(ResultsViewModel.class);
//        viewModel.clearCheckedResults();

        instantiateUI(view);

        viewModel.getSearchResults(searchId).observe(getViewLifecycleOwner(), results -> {
            unsortedResults = results;

            if (savedInstanceState != null &&
                    savedInstanceState.getStringArrayList(CHECKED_RESULTS_TAG) != null) {
                // Restore UI state after config. change
                restoreCheckedCardStates(unsortedResults, savedInstanceState.getStringArrayList(CHECKED_RESULTS_TAG));
            } else {
                viewModel.clearCheckedResults(); // reset checkedResults array in ViewModel
            }

            adapter.setResults(results);
            if (results.isEmpty())
                crossFade(noResultsLayout, loaderLayout);
            else
                crossFade(resultRecyclerView, loaderLayout);

            // Refresh menu to potentially enable/disable sortResultsItem & shareResultsItem
            numberOfResults = results.size();
            requireActivity().invalidateOptionsMenu();
        });
        viewModel.getSortCompleted().observe(getViewLifecycleOwner(), completed ->
                adapter.notifyDataSetChanged()
        );
        viewModel.getSortMenuOpen().observe(getViewLifecycleOwner(), open ->
                sortLayout.setVisibility(open)
        );
        viewModel.getOpenShareConfirmation().observe(getViewLifecycleOwner(),
                this::showShareConfirmationDialog);

    }

    private void restoreCheckedCardStates(ArrayList<ResultModel> results,
                                      ArrayList<String> checkedResultsLinks) {

        for (String resultLink : checkedResultsLinks)
            for (ResultModel result : results)
                if (result.getDetailsLink().equalsIgnoreCase(resultLink))
                    result.setIsChecked(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_results, menu);

        MenuItem sortResultsItem = menu.findItem(R.id.results_action_sort);
        shareResultsItem = menu.findItem(R.id.results_action_share);
        sortResultsItem.setEnabled((numberOfResults > 0));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

         menu.findItem(R.id.results_action_share).setEnabled(numberOfCheckedResults > 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        viewModel.handleOnOptionsItemSelected(menuItem.getItemId());
        return false;
    }

    public void instantiateUI(View view) {
        noResultsLayout = view.findViewById(R.id.results_text_no_results);
        resultRecyclerView = view.findViewById(R.id.results_view);
        loaderLayout = view.findViewById(R.id.results_layout_loader);
        sortLayout = view.findViewById(R.id.results_layout_sort_options);
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

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        resultRecyclerView.setVisibility(View.GONE);
        resultRecyclerView.setHasFixedSize(true);
        resultRecyclerView.setLayoutManager(layoutManager);

        GravitySnapHelper gravitySnapHelper = new GravitySnapHelper(Gravity.TOP);
        gravitySnapHelper.attachToRecyclerView(resultRecyclerView);

        resultRecyclerView.setAdapter(adapter);
    }

    public void addCheckedResult(String link) {
        numberOfCheckedResults = viewModel.addCheckedResult(link);
        shareResultsItem.setEnabled(numberOfCheckedResults > 0);
    }

    public void removeCheckedResult(String link) {
        numberOfCheckedResults = viewModel.removeCheckedResult(link);
        shareResultsItem.setEnabled(numberOfCheckedResults > 0);
    }

    public void showShareConfirmationDialog(int numberOfResults) {
        String message = numberOfResults > 1
                ? ("Share " + numberOfResults + " results?")
                : "Share 1 result?";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setPositiveButton("Share", (dialog, which) -> shareResults());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void shareResults() {
        List<String> resultLinks = viewModel.getCheckedResults();
//        String subjectText = "Check Out These Pre-Owned Toyotas I Found for You!";
        String bodyText = buildBodyText(resultLinks);
        if (!resultLinks.isEmpty()) {
//
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
//            intent.putExtra(Intent.EXTRA_SUBJECT, subjectText);
            intent.putExtra(Intent.EXTRA_TEXT, bodyText);
            intent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(intent, "Share Search Results");
            startActivity(shareIntent);
        }
    }

    private String buildBodyText(List<String> resultLinks) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Check out these pre-owned Toyotas I found for you! Click the links below to view each vehicle: \n\n");
        for (String link : resultLinks)
            stringBuilder.append(link).append("\n");

        return stringBuilder.toString();
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

        SortOption(String value) {
            this.value = value;
        }

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
