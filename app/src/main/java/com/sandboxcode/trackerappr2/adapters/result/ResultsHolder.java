package com.sandboxcode.trackerappr2.adapters.result;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.DetailFragment;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.Locale;

public class ResultsHolder extends RecyclerView.ViewHolder {

    private final FragmentManager fragmentManager;
    private ResultModel resultModel;

    private final TextView title;
    private final TextView stock;
    private final TextView price;
    private final ImageView thumbnail;
    private final Button detailsButton;
    private final ImageView newIcon;
    private final String searchId;

    public ResultsHolder(View itemView, FragmentManager fragmentManager,
                         String searchId) {
        super(itemView);

        title = itemView.findViewById(R.id.result_text_title);
        stock = itemView.findViewById(R.id.result_text_stock);
        price = itemView.findViewById(R.id.result_text_price);
        thumbnail = itemView.findViewById(R.id.result_image_thumbnail);
        newIcon = itemView.findViewById(R.id.result_image_new);

        detailsButton = itemView.findViewById(R.id.result_button_details);
        this.fragmentManager = fragmentManager;
        this.searchId = searchId;

    }

    public void bindResult(ResultModel result, int position) {
        resultModel = result;
        title.setText(result.getTitle());
        stock.setText(String.format("#%s", result.getStock()));

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setMaximumFractionDigits(0);
        String formattedPrice = formatter.format(Integer.parseInt(result.getPrice()));
        price.setText(formattedPrice);

        thumbnail.setTransitionName("result_to_detail_transition_" + position);
        Picasso.get().load(result.getImageUrl()).fit().into(thumbnail);
        this.detailsButton.setOnClickListener(buttonClickListener);

        newIcon.setVisibility(result.getIsNewResult() ? View.VISIBLE : View.INVISIBLE);
    }

    private final View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewDetails(resultModel, searchId);
        }
    };

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