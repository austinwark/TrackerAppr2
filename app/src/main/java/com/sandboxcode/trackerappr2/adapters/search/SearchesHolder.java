package com.sandboxcode.trackerappr2.adapters.search;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.SearchesFragment;
import com.sandboxcode.trackerappr2.models.SearchModel;

public class SearchesHolder extends RecyclerView.ViewHolder {

    private final TextView name;
    private final CheckBox checkBox;
    private final TextView createdAt;
    private final TextView lastEditedAt;
    private final TextView numberOfResults;
    private final FragmentManager fragmentManager;

    final View itemView;

    public SearchesHolder(View itemView, SearchesFragment fragment) {
        super(itemView);

        fragmentManager = fragment.getParentFragmentManager();
        name = itemView.findViewById(R.id.search_text_name);
        checkBox = itemView.findViewById(R.id.search_checkbox_edit);
        createdAt = itemView.findViewById(R.id.search_text_created);
        lastEditedAt = itemView.findViewById(R.id.search_text_edited);
        numberOfResults = itemView.findViewById(R.id.search_text_results);

        this.itemView = itemView;
    }

    public void bindSearch(SearchModel search) {
        name.setText(search.getSearchName());
        createdAt.setText(search.getCreatedDate());
        lastEditedAt.setText(search.getLastEditedDate());
        numberOfResults.setText(String.format("%s Results", search.getNumberOfResults()));
    }

    public void setEditActive(int editActive) {
        checkBox.setVisibility(editActive);
        checkBox.setChecked(editActive == View.VISIBLE && checkBox.isChecked());
    }

    public CheckBox getCheckBox() {
        return this.checkBox;
    }

    public View getItemView() {
        return itemView;
    }


}
