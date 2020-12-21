package com.sandboxcode.trackerappr2.adapters.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.fragments.SearchesFragment;
import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.ArrayList;
import java.util.List;

public class SearchesAdapter extends RecyclerView.Adapter<SearchesHolder> {

    private List<SearchModel> searchList = new ArrayList<>();
    private Context context;
    private int itemResource;
    private SearchesFragment fragment;
    private FragmentManager fragmentManager;
    private boolean editActive;


    public SearchesAdapter(Context context,
                           int itemResource,
                           SearchesFragment fragment) {

        this.context = context;
        this.itemResource = itemResource;
        this.fragment = fragment;
        this.fragmentManager = fragment.getParentFragmentManager();
        this.editActive = false;
    }

    @Override
    public SearchesHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new SearchesHolder(this.context, view, fragment);
    }

    @Override
    public void onBindViewHolder(SearchesHolder holder, final int position) {

        final SearchModel SEARCH = this.searchList.get(position);
        holder.setCheckBoxVisibility(editActive);
        holder.getCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fragment.onItemCheckedChange(SEARCH.getId(), isChecked);
            }
        });
        holder.bindSearch(SEARCH);
    }

    public void setCheckboxVisible(boolean checkboxVisible) {
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
