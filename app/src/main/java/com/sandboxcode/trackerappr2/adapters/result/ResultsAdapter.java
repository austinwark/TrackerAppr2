package com.sandboxcode.trackerappr2.adapters.result;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.fragments.ResultsFragment;
import com.sandboxcode.trackerappr2.models.ResultModel;

import java.util.ArrayList;
import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsHolder> {

    private static final String TAG = ResultsAdapter.class.getSimpleName();
    private List<ResultModel> resultsList = new ArrayList<>();
    private final int itemResource;
    private final FragmentManager fragmentManager;
    private final String searchId;

    ResultsFragment resultsFragment;

    public ResultsAdapter(int itemResource, FragmentManager fragmentManager, String searchId,
                          ResultsFragment resultsFragment) {

        this.itemResource = itemResource;
        this.fragmentManager = fragmentManager;
        this.searchId = searchId;

        this.resultsFragment = resultsFragment;
    }

    @Override
    public ResultsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);

        return new ResultsHolder(view, fragmentManager, searchId, resultsFragment);
    }

    @Override
    public void onBindViewHolder(ResultsHolder holder, int position) {

        ResultModel result = this.resultsList.get(position);

        holder.bindResult(result, position);
    }

    public void setResults(List<ResultModel> results) {
        resultsList = results;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return this.resultsList.size();
    }
}
