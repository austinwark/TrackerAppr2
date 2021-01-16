package com.sandboxcode.trackerappr2.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchesFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchesFragment extends Fragment {

    private static final String TAG = "SearchesFragment";
    private static final String RESULT_MESSAGE_TAG = "result_message";
    FloatingActionButton fab;

    private MainSharedViewModel viewModel;

    private BottomNavigationView toolbarBottom;
    private SearchesAdapter adapter;
    private RecyclerView recyclerView;
    private ConstraintLayout loaderLayout;
    private ConstraintLayout noSearchesLayout;
    private int shortAnimationDuration;
    private int numberOfSearches;

    final ActivityResultLauncher<Intent> startForResult =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(), result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            viewModel.refreshSearches();
                            requireActivity().invalidateOptionsMenu();
                        }

                        Toast.makeText(getActivity(),
                                result.getData().getStringExtra(RESULT_MESSAGE_TAG), Toast.LENGTH_LONG)
                                .show();
                    });

    public SearchesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> checkedItems;

        viewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);

        checkedItems = viewModel.getCheckedItems();
        adapter = new SearchesAdapter(R.layout.search_list_item, this, checkedItems);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        numberOfSearches = 0;
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
        inflater.inflate(R.menu.menu_searches, menu);

        Menu bottomMenu = toolbarBottom.getMenu();
        MenuItem editSearchItem = menu.findItem(R.id.action_edit);

        // enable edit menu button if there is at least one search in recyclerview
        editSearchItem.setEnabled((numberOfSearches > 0));

        // returns the onOptionsItemSelected method for each MenuItem
        for (int itemIndex = 0; itemIndex < bottomMenu.size(); itemIndex++) {
            bottomMenu.getItem(itemIndex).setOnMenuItemClickListener(this::onOptionsItemSelected);
        }
    }

       public void onItemCheckedChange(String searchId, boolean isChecked) {
        viewModel.updateCheckedSearchesList(searchId, isChecked);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        viewModel.handleOnOptionsItemSelected(item.getItemId());

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        instantiateUI(view);

        viewModel.getAllSearches().observe(getViewLifecycleOwner(), searches -> {
            adapter.setSearches(searches);
            if (searches.isEmpty())
                crossFade(noSearchesLayout, loaderLayout);
            else
                crossFade(recyclerView, loaderLayout);

            // Refresh Menu to potentially enable/disable editSearchItem
            numberOfSearches = searches.size();
            requireActivity().invalidateOptionsMenu();

        });
        viewModel.getEditMenuOpen().observe(getViewLifecycleOwner(), editMenuOpen -> {
            toolbarBottom.setVisibility(editMenuOpen);
            adapter.setCheckboxVisible(editMenuOpen);
            fab.setVisibility(editMenuOpen == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
        });
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message ->
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show());
        viewModel.getStartEditActivity().observe(getViewLifecycleOwner(), searchId -> {
            Intent intent = new Intent(getActivity(), EditActivity.class);
            intent.putExtra("searchId", searchId);
            startForResult.launch(intent);
        });
        viewModel.getConfirmDeleteSearches().observe(getViewLifecycleOwner(), numOfSearchesToDelete -> {
            String message = numOfSearchesToDelete > 1
                    ? "Delete " + numOfSearchesToDelete + " searches?" : "Delete search?";

            new AlertDialog.Builder(getActivity())
                    .setMessage(message)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        viewModel.deleteSearches();
                        requireActivity().invalidateOptionsMenu();
                    })
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
        noSearchesLayout = view.findViewById(R.id.searches_layout_no_searches);

        toolbarBottom = view.findViewById(R.id.toolbar_bottom);
        toolbarBottom.setItemIconTintList(null);

        toolbarBottom.setVisibility(View.INVISIBLE);
        adapter.setCheckboxVisible(View.INVISIBLE);

        fab = view.findViewById(R.id.searches_fab_create);
        fab.setImageResource(R.drawable.ic_create);
        fab.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), CreateActivity.class)));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity().getApplicationContext());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void crossFade(View view1, View view2) {
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

    @Override
    public void onDestroy() {
        viewModel.saveState();
        super.onDestroy();
    }

}