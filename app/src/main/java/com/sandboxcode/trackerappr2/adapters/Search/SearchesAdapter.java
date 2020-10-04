package com.sandboxcode.trackerappr2.adapters.Search;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.List;

public class SearchesAdapter extends RecyclerView.Adapter<SearchesHolder> {

    private final List<SearchModel> searchList;
    private Context context;
    private int itemResource;
    private FragmentManager fragmentManager;

    public SearchesAdapter(Context context,
                           int itemResource,
                           List<SearchModel> searchList,
                           FragmentManager fragmentManager) {

        this.searchList = searchList;
        this.context = context;
        this.itemResource = itemResource;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public SearchesHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new SearchesHolder(this.context, view, fragmentManager);
    }

    @Override
    public void onBindViewHolder(SearchesHolder holder, int position) {

        SearchModel search = this.searchList.get(position);
        Log.d("SearchesAdapter", search.toString());

        holder.bindSearch(search);
    }

    @Override
    public int getItemCount() {

        return this.searchList.size();
    }
}
