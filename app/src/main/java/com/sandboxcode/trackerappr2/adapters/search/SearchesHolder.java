package com.sandboxcode.trackerappr2.adapters.search;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.ResultsFragment;
import com.sandboxcode.trackerappr2.fragments.SearchesFragment;
import com.sandboxcode.trackerappr2.models.SearchModel;

public class SearchesHolder extends RecyclerView.ViewHolder {

    private final TextView name;
    private final CheckBox checkBox;
    private SearchModel searchModel;
    private SearchesFragment fragment;
    private FragmentManager fragmentManager;
    private int editActive;

    View itemView;

    public SearchesHolder(View itemView, SearchesFragment fragment) {
        super(itemView);

        this.fragment = fragment;
        this.fragmentManager = fragment.getParentFragmentManager();
        this.name = itemView.findViewById(R.id.tv_search_item_name);
        this.checkBox = itemView.findViewById(R.id.checkBox_edit);

        this.itemView = itemView;
    }

    public void bindSearch(SearchModel search) {
        this.searchModel = search;
        this.name.setText(search.getSearchName());
    }

    public void setEditActive(int editActive) {
        this.editActive = editActive;
        this.checkBox.setVisibility(editActive);
        this.checkBox.setChecked(editActive == View.VISIBLE && checkBox.isChecked());
    }

    public void viewResults(SearchModel search) {
        Bundle args = new Bundle();
        args.putString("ID", search.getId());

        ResultsFragment resultsFragment = new ResultsFragment();
        resultsFragment.setArguments(args);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment_container, resultsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setCheckBoxVisibility(int visible) {

        this.checkBox.setVisibility(visible);
    }

    public CheckBox getCheckBox() {
        return this.checkBox;
    }

    public View getItemView() {
        return itemView;
    }


}
