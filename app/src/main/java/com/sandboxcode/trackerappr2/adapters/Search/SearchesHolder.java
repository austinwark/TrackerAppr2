package com.sandboxcode.trackerappr2.adapters.Search;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.ResultsFragment;
import com.sandboxcode.trackerappr2.models.SearchModel;

public class SearchesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView name;
    private SearchModel searchModel;
    private Context context;
    private FragmentManager fragmentManager;

    public SearchesHolder(Context context, View itemView, FragmentManager fragmentManager) {
        super(itemView);

        this.context = context;
        this.fragmentManager = fragmentManager;
        this.name = (TextView) itemView.findViewById(R.id.tv_search_item_name);

        itemView.setOnClickListener(this);
    }

    public void bindSearch(SearchModel search) {
        this.searchModel = search;
        this.name.setText(search.getSearchName());
        Log.d("SearchesHolder", search.getSearchName());
    }

    @Override
    public void onClick(View v) {
        if (this.searchModel != null)
            viewResults(this.searchModel);
    }

    public void viewResults(SearchModel search) {
        Bundle args = new Bundle();
        args.putString("ID", search.getId());
        ResultsFragment fragment = new ResultsFragment();
        fragment.setArguments(args);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
