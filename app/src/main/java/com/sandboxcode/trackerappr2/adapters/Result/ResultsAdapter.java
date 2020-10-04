package com.sandboxcode.trackerappr2.adapters.Result;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.models.ResultModel;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsHolder> {

    private final List<ResultModel> resultsList;
    private Context context;
    private int itemResource;
    private FragmentManager fragmentManager;
    private String searchId;

    public ResultsAdapter(Context context,
                          int itemResource,
                          List<ResultModel> resultsList,
                          FragmentManager fragmentManager,
                          String searchId) {

        this.resultsList = resultsList;
        this.context = context;
        this.itemResource = itemResource;
        this.fragmentManager = fragmentManager;
        this.searchId = searchId;
    }

    @Override
    public ResultsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new ResultsHolder(this.context, view, fragmentManager, searchId);
    }

    @Override
    public void onBindViewHolder(ResultsHolder holder, int position) {

        ResultModel result = this.resultsList.get(position);
        Log.d("ResultsAdapter", result.getTitle());

        holder.bindResult(result);
    }

    @Override
    public int getItemCount() {

        return this.resultsList.size();
    }
}
