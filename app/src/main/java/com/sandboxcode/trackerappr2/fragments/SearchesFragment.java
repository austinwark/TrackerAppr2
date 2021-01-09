package com.sandboxcode.trackerappr2.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.activities.CreateActivity;
import com.sandboxcode.trackerappr2.activities.EditActivity;
import com.sandboxcode.trackerappr2.adapters.search.SearchesAdapter;
import com.sandboxcode.trackerappr2.viewmodels.MainSharedViewModel;

import java.util.Objects;

import static android.util.Log.d;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchesFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchesFragment extends Fragment {

    private static final String TAG = "SearchesFragment";
    private static final String RESULT_MESSAGE_TAG = "result_message";
    FloatingActionButton fab;
    private Context activityContext;
    private MainSharedViewModel viewModel;
    final ActivityResultLauncher<Intent> startForResult =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), result -> {
                        if (result.getResultCode() == Activity.RESULT_OK)
                            viewModel.refreshSearches();

                        Toast.makeText(getActivity(), result.getData()
                                .getStringExtra(RESULT_MESSAGE_TAG), Toast.LENGTH_LONG)
                                .show();
                    });
    private BottomNavigationView toolbarBottom;
    private SearchesAdapter adapter;
    private RecyclerView recyclerView;
    private ConstraintLayout loaderLayout;
    private int shortAnimationDuration;

    public SearchesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        d(TAG, "onCreate");

        activityContext = getActivity().getApplicationContext();
        adapter = new SearchesAdapter(R.layout.search_list_item, this);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_searches, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);

        Menu bottomMenu = toolbarBottom.getMenu();

        // returns the onOptionsItemSelected method for each MenuItem
        for (int itemIndex = 0; itemIndex < bottomMenu.size(); itemIndex++) {
            bottomMenu.getItem(itemIndex).setOnMenuItemClickListener(this::onOptionsItemSelected);
        }
    }

    // TODO -- save checked status after orientation change
    public void onItemCheckedChange(String searchId, boolean isChecked) {

        viewModel.updateCheckedSearchesList(searchId, isChecked);
//        getActivity().invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        viewModel.handleOnOptionsItemSelected(item.getItemId());

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        instantiateUI(view);

        viewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);
        viewModel.getAllSearches().observe(getViewLifecycleOwner(), searches -> {
            d(TAG, "getAllSearches");
            adapter.setSearches(searches);
            crossFade();
        });
        viewModel.getEditMenuOpen().observe(getViewLifecycleOwner(), editMenuOpen -> {
            Log.d(TAG, "getEditMenuOpen");
            toolbarBottom.setVisibility(editMenuOpen);
            adapter.setCheckboxVisible(editMenuOpen);
            toggleFabVisibility();
        });
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message ->
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show());
        viewModel.getStartEditActivity().observe(getViewLifecycleOwner(), searchId -> {
            Intent intent = new Intent(getActivity(), EditActivity.class);
            intent.putExtra("searchId", searchId);
            startForResult.launch(intent);
        });
        viewModel.getConfirmDeleteSearches().observe(getViewLifecycleOwner(), numberOfSearches -> {
            Log.d(TAG, "deletesearches OBSERVED");

            String message = numberOfSearches > 1
                    ? "Delete " + numberOfSearches + " searches?" : "Delete search?";

            new AlertDialog.Builder(getActivity())
                    .setMessage(message)
                    .setPositiveButton("Yes", (dialog, which) -> viewModel.deleteSearches())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel()).show();
        });
    }

    private void instantiateUI(View view) {
        if (((AppCompatActivity) getActivity()) != null
                && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {

            Objects.requireNonNull(((AppCompatActivity) getActivity())
                    .getSupportActionBar()).setTitle("Searches");
        }

        recyclerView = view.findViewById(R.id.searches_view);
        recyclerView.setVisibility(View.GONE);

        loaderLayout = view.findViewById(R.id.searches_layout_loader);

        toolbarBottom = view.findViewById(R.id.toolbar_bottom);
        toolbarBottom.setItemIconTintList(null);

        toolbarBottom.setVisibility(View.INVISIBLE);
        adapter.setCheckboxVisible(View.INVISIBLE);

        fab = view.findViewById(R.id.searches_fab_create);
        fab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_create);
        fab.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), CreateActivity.class)));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activityContext);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void crossFade() {
        recyclerView.setAlpha(0f);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.animate()
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

    private void toggleFabVisibility() {
        fab.setVisibility(fab.getVisibility() == View.INVISIBLE ? View.VISIBLE : View.INVISIBLE);
    }

}