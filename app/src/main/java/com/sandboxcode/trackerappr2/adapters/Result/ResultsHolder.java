package com.sandboxcode.trackerappr2.adapters.Result;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.DetailFragment;
import com.sandboxcode.trackerappr2.models.ResultModel;

import org.parceler.Parcels;

public class ResultsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final TextView title;
    private final TextView stock;
    private final TextView price;
    private ResultModel resultModel;
    private Context context;
    private FragmentManager fragmentManager;
    private String searchId;

    public ResultsHolder(Context context, View itemView, FragmentManager fragmentManager, String searchId) {
        super(itemView);

        this.context = context;

        this.title = (TextView) itemView.findViewById(R.id.tv_result_item_title);
        this.stock = (TextView) itemView.findViewById(R.id.tv_result_item_stock);
        this.price = (TextView) itemView.findViewById(R.id.tv_result_item_price);
        this.fragmentManager = fragmentManager;
        this.searchId = searchId;

        itemView.setOnClickListener(this);
    }

    public void bindResult(ResultModel result) {
        this.resultModel = result;
        this.title.setText(result.getTitle());
        this.stock.setText(result.getStock());
        this.price.setText(result.getPrice());

        Log.d("ResultsHolder", result.getTitle());
    }

    @Override
    public void onClick(View v) {
        if (this.resultModel != null)
            viewDetails(this.resultModel, this.searchId);
    }

    // TODO - Change from view click to button click
    public void viewDetails(ResultModel result, String searchId) {

        Bundle args = new Bundle();
        args.putParcelable("RESULT", Parcels.wrap(result));
        args.putString("SEARCH_ID", searchId);

        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}