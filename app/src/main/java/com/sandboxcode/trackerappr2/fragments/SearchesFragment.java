package com.sandboxcode.trackerappr2.fragments;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.activities.CreateActivity;
import com.sandboxcode.trackerappr2.adapters.decorators.ShadowVerticalSpaceItemDecorator;
import com.sandboxcode.trackerappr2.adapters.decorators.VerticalSpaceItemDecorator;
import com.sandboxcode.trackerappr2.adapters.search.SearchesAdapter;
import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.viewmodels.SearchViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchesFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchesFragment extends Fragment {

    private static final String TAG = "SearchesFragment";
    private Context activityContext;

    private RecyclerView searchListView;
    private SearchViewModel searchViewModel;
    private BottomNavigationView toolbarBottom;
    private MenuItem deleteMenuItem;
    private SearchesAdapter adapter;

    public SearchesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityContext = getActivity().getApplicationContext();
        adapter = new SearchesAdapter(activityContext, R.layout.search_list_item, this);

        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        searchViewModel.getAllSearches().observe(this, searches -> {
            Log.d(TAG, "onChanged");
            adapter.setSearches(searches);
        });
        searchViewModel.getEditMenuOpen().observe(this, editMenuToggle -> {
            this.toolbarBottom.setVisibility(editMenuToggle.getVisible());
            this.adapter.setCheckboxVisible(editMenuToggle.getCheckboxVisible());
        });
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
        deleteMenuItem = bottomMenu.findItem(R.id.action_delete);

        // returns the onOptionsItemSelected method for each MenuItem
        for (int itemIndex = 0; itemIndex < bottomMenu.size(); itemIndex++) {
            bottomMenu.getItem(itemIndex).setOnMenuItemClickListener(this::onOptionsItemSelected);
        }
    }

    public void onItemCheckedChange(String searchId, boolean isChecked) {

        searchViewModel.updateCheckedSearchesList(searchId, isChecked);
        getActivity().invalidateOptionsMenu();
    }

    /* Disables delete button if checked list is empty */
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//
//        if (!checkedItems.isEmpty()) {
//            deleteMenuItem.setEnabled(true);
//            deleteMenuItem.getIcon().mutate().setAlpha(255);
//            deleteMenuItem.getIcon().setAlpha(255);
//        } else {
//            deleteMenuItem.setEnabled(false);
//            deleteMenuItem.getIcon().mutate().setAlpha(5);
//            deleteMenuItem.getIcon().setAlpha(5);
//        }
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getEditActive())
            outState.putBoolean("EDIT_ACTIVE", getEditActive());

        super.onSaveInstanceState(outState);
    }

    public void toggleEdit(boolean newState) {
        if (newState) {
            this.toolbarBottom.setVisibility(View.VISIBLE);
            this.adapter.setCheckboxVisible(true);
        } else {
            this.toolbarBottom.setVisibility(View.INVISIBLE);
            this.adapter.setCheckboxVisible(false);
        }
        this.adapter.notifyDataSetChanged();
    }

    public boolean getEditActive() {
        return this.toolbarBottom.getVisibility() == View.VISIBLE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, String.valueOf(item.getItemId()));
        switch (item.getItemId()) {
            case R.id.action_edit:
                searchViewModel.toggleEdit(true);
                return true;
            case R.id.action_delete:
                Log.d(TAG, "delete Search");
                deleteSearch();
                return true;
            default:
                Log.d(TAG, "Default");
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteSearch() {
        String message = searchViewModel.deleteSearch();
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Queries");

        toolbarBottom = view.findViewById(R.id.toolbar_bottom);
        toolbarBottom.setItemIconTintList(null);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_create);
        fab.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), CreateActivity.class)));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activityContext);

        int verticalSpacing = 20;
        VerticalSpaceItemDecorator itemDecorator = new VerticalSpaceItemDecorator(verticalSpacing);
        ShadowVerticalSpaceItemDecorator shadowItemDecorator = new ShadowVerticalSpaceItemDecorator(activityContext, R.drawable.drop_shadow);

        searchListView = view.findViewById(R.id.searches_view);

        searchListView.setHasFixedSize(true);

        searchListView.setLayoutManager(layoutManager);

        searchListView.addItemDecoration(shadowItemDecorator);
        searchListView.addItemDecoration(itemDecorator);

        searchListView.setAdapter(adapter);

        //TODO - REMOVE LOGIC
        if (savedInstanceState != null) {
            toggleEdit(savedInstanceState.getBoolean("EDIT_ACTIVE"));
        } else {
            // no need to call adapter.notifyDataSetChanged
            toolbarBottom.setVisibility(View.INVISIBLE);
        }

    }

    public void makeToastMessage(String message, int length) {
        if (length == 1 || length == 2)
            Toast.makeText(getActivity(), message, length).show();
    }

    public void viewResults(SearchModel search) {
        Bundle args = new Bundle();
        args.putString("ID", search.getId());
        ResultsFragment fragment = new ResultsFragment();
        fragment.setArguments(args);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}