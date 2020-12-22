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
import com.sandboxcode.trackerappr2.viewmodels.MainSharedViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchesFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchesFragment extends Fragment {

    private static final String TAG = "SearchesFragment";
    private Context activityContext;

    private RecyclerView searchListView;
    private MainSharedViewModel mainSharedViewModel;
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

        mainSharedViewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);
        mainSharedViewModel.getAllSearches().observe(this, searches -> {
            Log.d(TAG, "onChanged");
            adapter.setSearches(searches);
        });
        mainSharedViewModel.getEditMenuOpen().observe(this, editMenuOpen -> {
            this.toolbarBottom.setVisibility(editMenuOpen);
            this.adapter.setCheckboxVisible(editMenuOpen);
        });
        mainSharedViewModel.getToastMessage().observe(this, message -> {
            Log.d(TAG, "TOAST");
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        });
        mainSharedViewModel.getCheckedItems().observe(this, checkedItems -> {

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

    // TODO -- save checked status after orientation change
    public void onItemCheckedChange(String searchId, boolean isChecked) {

        mainSharedViewModel.updateCheckedSearchesList(searchId, isChecked);
        getActivity().invalidateOptionsMenu();
    }

    public boolean getEditActive() {
        return this.toolbarBottom.getVisibility() == View.VISIBLE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mainSharedViewModel.handleBottomOnOptionsItemSelected(item.getItemId());

        return super.onOptionsItemSelected(item);
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