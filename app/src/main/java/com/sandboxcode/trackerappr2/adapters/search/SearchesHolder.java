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

    private TextView name;
    private CheckBox checkBox;
    private TextView createdAt;
    private TextView lastEditedAt;
    private TextView numberOfResults;
    private SearchModel searchModel;
    private SearchesFragment fragment;
    private FragmentManager fragmentManager;
    private int editActive;

    View itemView;

    public SearchesHolder(View itemView, SearchesFragment fragment) {
        super(itemView);

        this.fragment = fragment;
        fragmentManager = fragment.getParentFragmentManager();
        name = itemView.findViewById(R.id.search_text_name);
        checkBox = itemView.findViewById(R.id.search_checkbox_edit);
        createdAt = itemView.findViewById(R.id.search_text_created);
        lastEditedAt = itemView.findViewById(R.id.search_text_edited);
        numberOfResults = itemView.findViewById(R.id.search_text_results);

        this.itemView = itemView;
    }

    public void bindSearch(SearchModel search) {
        searchModel = search;
        name.setText(search.getSearchName());
        createdAt.setText(search.getCreatedDate());
        lastEditedAt.setText(search.getLastEditedDate());
        numberOfResults.setText(search.getNumberOfResults() + " Results");
    }

    public void setEditActive(int editActive) {
        this.editActive = editActive;
        checkBox.setVisibility(editActive);
        checkBox.setChecked(editActive == View.VISIBLE && checkBox.isChecked());
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

        checkBox.setVisibility(visible);
    }

    public CheckBox getCheckBox() {
        return this.checkBox;
    }

    public View getItemView() {
        return itemView;
    }


}
