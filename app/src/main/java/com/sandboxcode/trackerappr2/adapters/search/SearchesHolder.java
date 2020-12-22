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

public class SearchesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView name;
    private final CheckBox checkBox;
    private SearchModel searchModel;
    private FragmentManager fragmentManager;

    public SearchesHolder(View itemView, SearchesFragment fragment) {
        super(itemView);

        this.fragmentManager = fragment.getParentFragmentManager();
        this.name = (TextView) itemView.findViewById(R.id.tv_search_item_name);
        this.checkBox = (CheckBox) itemView.findViewById(R.id.checkBox_edit);

        itemView.setOnClickListener(this);
    }

    public void bindSearch(SearchModel search) {
        this.searchModel = search;
        this.name.setText(search.getSearchName());
    }


    @Override
    public void onClick(View v) {
        if (this.searchModel != null)
            viewResults(this.searchModel);
    }

    public void viewResults(SearchModel search) {
        Bundle args = new Bundle();
        args.putString("ID", search.getId());

//        if (fragmentManager.findFragmentById(R.id.main_fragment_container) instanceof SearchesFragment)
//            ((SearchesFragment) fragmentManager.findFragmentById(R.id.main_fragment_container)).toggleEdit(false);

        ResultsFragment fragment = new ResultsFragment();
        fragment.setArguments(args);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setCheckBoxVisibility(int visible) {
        this.checkBox.setVisibility(visible);
        //        if (newState) {
//            this.checkBox.setVisibility(View.VISIBLE);
//        } else {
//            this.checkBox.setVisibility(View.INVISIBLE);
//        }
    }

    public CheckBox getCheckBox() {
        return this.checkBox;
    }


}
