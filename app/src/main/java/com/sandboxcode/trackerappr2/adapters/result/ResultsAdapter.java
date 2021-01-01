package com.sandboxcode.trackerappr2.adapters.result;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.models.ResultModel;

import java.util.ArrayList;
import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsHolder> {

    private List<ResultModel> resultsList = new ArrayList<>();
    private Context context;
    private int itemResource;
    private FragmentManager fragmentManager;
    private String searchId;

    public ResultsAdapter(Context context,
                          int itemResource,
                          FragmentManager fragmentManager,
                          String searchId) {

        this.context = context;
        this.itemResource = itemResource;
        this.fragmentManager = fragmentManager;
        this.searchId = searchId;
    }

    @Override
    public ResultsHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
//        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//        layoutParams.height = (int) (parent.getHeight() * .25);
//        view.setLayoutParams(layoutParams);
        return new ResultsHolder(this.context, view, fragmentManager, searchId);
    }

    @Override
    public void onBindViewHolder(ResultsHolder holder, int position) {

        ResultModel result = this.resultsList.get(position);
        Log.d("ResultsAdapter", result.getTitle());

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
