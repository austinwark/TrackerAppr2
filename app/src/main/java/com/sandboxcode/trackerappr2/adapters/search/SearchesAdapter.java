package com.sandboxcode.trackerappr2.adapters.search;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.ResultsFragment;
import com.sandboxcode.trackerappr2.fragments.SearchesFragment;
import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.ArrayList;
import java.util.List;

public class SearchesAdapter extends RecyclerView.Adapter<SearchesHolder> {

    private static final String TAG = "SearchesAdapter";
    private List<SearchModel> searchList = new ArrayList<>();
    private Context context;
    private int itemResource;
    private SearchesFragment fragment;
    private FragmentManager fragmentManager;
    private int editActive;


    public SearchesAdapter(Context context,
                           int itemResource,
                           SearchesFragment fragment) {

        this.context = context;
        this.itemResource = itemResource;
        this.fragment = fragment;
        this.fragmentManager = fragment.getParentFragmentManager();
        this.editActive = View.INVISIBLE;
    }

    @Override
    public SearchesHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new SearchesHolder(view, fragment);
    }

    @Override
    public void onBindViewHolder(SearchesHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder");
        CheckBox checkBox = holder.getCheckBox();

        final SearchModel SEARCH = this.searchList.get(position);

        holder.bindSearch(SEARCH);
        holder.setEditActive(editActive);

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "onCheckedChange");
            fragment.onItemCheckedChange(SEARCH.getId(), isChecked);
        });

        holder.getItemView().setOnClickListener(v -> {
            if (editActive == View.VISIBLE) {
                checkBox.setChecked(!checkBox.isChecked()); // toggle checked
            } else {
                if (SEARCH != null)
                    viewResults(SEARCH.getId());
            }
        });
    }

    public void viewResults(String searchId) {
        Bundle args = new Bundle();
        args.putString("ID", searchId);

        ResultsFragment resultsFragment = new ResultsFragment();
        resultsFragment.setArguments(args);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment_container, resultsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setCheckboxVisible(int checkboxVisible) {
        this.editActive = checkboxVisible;
        notifyDataSetChanged();
    }

    public void setSearches(List<SearchModel> searches) {
        searchList = searches;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return this.searchList.size();
    }

}
