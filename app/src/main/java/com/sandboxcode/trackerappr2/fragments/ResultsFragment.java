package com.sandboxcode.trackerappr2.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.adapters.result.ResultsAdapter;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.utils.GravitySnapHelper;
import com.sandboxcode.trackerappr2.viewmodels.ResultsViewModel;

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
    private static final String CHECKED_RESULTS_TAG = "checked_results";
    FragmentManager fragmentManager;

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

    private LinearLayout sortLayout;
    private ChipGroup sortChipGroup;

    private MenuItem shareResultsItem;
    private BottomNavigationView toolbarBottom;

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchId = getArguments().getString("ID");
        }
        fragmentManager = getParentFragmentManager();

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

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

        adapter = new ResultsAdapter(R.layout.result_list_item, fragmentManager, searchId, this);

        viewModel = new ViewModelProvider(this).get(ResultsViewModel.class);
//        viewModel.clearCheckedResults();

        instantiateUI(view);

        viewModel.getSearchResults(searchId).observe(getViewLifecycleOwner(), results -> {
            unsortedResults = (ArrayList) results;

            viewModel.restoreCheckedCardStates(unsortedResults);
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
        viewModel.getOpenShareConfirmation().observe(getViewLifecycleOwner(), numOfResults ->
                showShareConfirmationDialog());
        viewModel.getEditMenuVisibility().observe(getViewLifecycleOwner(), visibility -> {
            toolbarBottom.setVisibility(visibility);
            adapter.setEditActive(visibility);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            );

            int toolbarHeight = toolbarBottom.getHeight();
            Log.d(TAG, String.valueOf(toolbarHeight));
            params.setMargins(0, 0, 0, (visibility == View.INVISIBLE ? 0 : toolbarHeight));
            resultRecyclerView.setLayoutParams(params);

        });
        viewModel.getViewDetails().observe(getViewLifecycleOwner(), results -> {
            viewDetails(results, searchId);
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_results, menu);

        MenuItem sortResultsItem = menu.findItem(R.id.results_action_sort);
        shareResultsItem = menu.findItem(R.id.results_action_share);
        sortResultsItem.setEnabled((numberOfResults > 0));

        Menu bottomMenu = toolbarBottom.getMenu();

        for (int itemIndex = 0; itemIndex < bottomMenu.size(); itemIndex++)
            bottomMenu.getItem(itemIndex).setOnMenuItemClickListener(this::onOptionsItemSelected);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);

//         menu.findItem(R.id.results_action_share).setEnabled(numberOfCheckedResults > 0);
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
        toolbarBottom = view.findViewById(R.id.results_toolbar_bottom);

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

        toolbarBottom.setVisibility(View.INVISIBLE);
    }

    public void setEditMenuActive() {
        viewModel.setEditMenuVisibility(View.VISIBLE);
    }

    public void addCheckedResult(ResultModel result) {
        numberOfCheckedResults = viewModel.addCheckedResult(result);
//        shareResultsItem.setEnabled(numberOfCheckedResults > 0);
    }

    public void removeCheckedResult(ResultModel result) {
        numberOfCheckedResults = viewModel.removeCheckedResult(result);
//        shareResultsItem.setEnabled(numberOfCheckedResults > 0);
    }

    public void showShareConfirmationDialog() {
        String message = viewModel.buildShareConfirmationMessage();

        new AlertDialog.Builder(getActivity())
            .setMessage(message)
            .setPositiveButton("Share", (dialog, which) -> shareResults())
            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
            .create()
            .show();
    }

    public void shareResults() {
        List<String> resultLinks = new ArrayList<>();
        for (ResultModel checkedResult : viewModel.getCheckedResults())
            resultLinks.add(checkedResult.getDetailsLink());

        String bodyText = viewModel.buildShareBodyText(resultLinks);

        if (!resultLinks.isEmpty()) {
            Intent intent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, HtmlCompat.fromHtml(bodyText, HtmlCompat.FROM_HTML_MODE_LEGACY))
                .putExtra(Intent.EXTRA_HTML_TEXT, HtmlCompat.fromHtml(bodyText, HtmlCompat.FROM_HTML_MODE_LEGACY))
                .setType("text/html");

            startActivity(Intent.createChooser(intent, "Share Search Results"));
        }
    }

    public void viewDetails(List<ResultModel> results, String searchId) {
        DetailPagerFragment fragment = DetailPagerFragment.newInstance(results, searchId);
        fragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment, DetailPagerFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();
        viewModel.clearCheckedResults();
    }

//    private void adjustBottomMargin

    @Override
    public void onResume() {
        Log.d(TAG, "ONRESUME");
        super.onResume();
    }

    // TODO - Results are reloaded on orientation change????
    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        adapter.setResults(null);
        super.onDestroyView();
    }

    public void handleBackPressed() {
        if (viewModel.getEditMenuVisibility().getValue() == null ||
                viewModel.getEditMenuVisibility().getValue() == View.INVISIBLE) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(this);
            transaction.commit();
            fragmentManager.popBackStack();
        } else
            viewModel.toggleEdit();
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
