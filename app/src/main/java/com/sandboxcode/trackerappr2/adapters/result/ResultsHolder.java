package com.sandboxcode.trackerappr2.adapters.result;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
//import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialContainerTransform;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.DetailFragment;
import com.sandboxcode.trackerappr2.fragments.ResultsFragment;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.function.Predicate;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ResultsHolder extends RecyclerView.ViewHolder {

    private static final String TAG = ResultsHolder.class.getSimpleName();
    private final FragmentManager fragmentManager;
    private ResultModel resultModel;

    private final MaterialCardView cardView;
    private final TextView title;
    private final TextView stock;
    private final TextView price;
    private final ImageView thumbnail;
//    private final ImageView newIcon;
    private String searchId;

    private ResultsFragment resultsFragment;
    private int position;
    private View thumbnailView;

    public ResultsHolder(View itemView, FragmentManager fragmentManager,
                         String searchId, ResultsFragment resultsFragment) {
        super(itemView);
//        resultsFragment.setExitTransition(new Hold());
        cardView = itemView.findViewById(R.id.result_layout_card);
        title = itemView.findViewById(R.id.result_text_title);
        stock = itemView.findViewById(R.id.result_text_stock);
        price = itemView.findViewById(R.id.result_text_price);
        thumbnail = itemView.findViewById(R.id.result_image_thumbnail);
//        newIcon = itemView.findViewById(R.id.result_image_new);

        this.fragmentManager = fragmentManager;
        this.searchId = searchId;

        this.resultsFragment = resultsFragment;
//        thumbnailView = itemView.findViewById(R.id.result_view_thumbnail);

    }

    public void bindResult(ResultModel result, int position) {
        resultModel = result;
        title.setText(result.getTitle());
        stock.setText(String.format("#%s", result.getStock()));

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setMaximumFractionDigits(0);
        String formattedPrice = formatter.format(Integer.parseInt(result.getPrice()));
        price.setText(formattedPrice);

//        thumbnail.setTransitionName("result_to_detail_transition_" + position);
        Picasso.get().load(result.getImageUrl()).into(thumbnail);

        cardView.setChecked(result.isChecked());
        cardView.setOnClickListener(cardViewClickListener);
        cardView.setOnLongClickListener(cardViewLongClickListener);

//        newIcon.setVisibility(result.getIsNewResult() ? View.VISIBLE : View.INVISIBLE);

        this.position = position;
        thumbnail.setTransitionName(result.getVin());

    }

    private final View.OnClickListener cardViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewDetails(resultModel, searchId);
        }
    };

    private final View.OnLongClickListener cardViewLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            boolean isCurrentlyChecked = resultModel.isChecked();
            cardView.setChecked(!isCurrentlyChecked);
            resultModel.setIsChecked(!isCurrentlyChecked);

            if (!isCurrentlyChecked)
                resultsFragment.addCheckedResult(resultModel.getDetailsLink());
            else
                resultsFragment.removeCheckedResult(resultModel.getDetailsLink());

            return true;
        }
    };

    public void viewDetails(ResultModel result, String searchId) {
        Bundle args = new Bundle();
        args.putParcelable("RESULT", Parcels.wrap(result));
        args.putString("SEARCH_ID", searchId);

        DetailFragment fragment = new DetailFragment();
        fragment.setSharedElementEnterTransition(new MaterialContainerTransform());
        fragment.setArguments(args);

        fragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .addSharedElement(thumbnail, thumbnail.getTransitionName())
                .replace(R.id.main_fragment_container,
                        fragment,
                        DetailFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();
    }


    //    public void viewDetails(ResultModel result, String searchId) {
//
//        Bundle args = new Bundle();
//        args.putParcelable("RESULT", Parcels.wrap(result));
//        args.putString("SEARCH_ID", searchId);
//
//        DetailFragment fragment = new DetailFragment();
//
//        fragment.setArguments(args);
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.main_fragment_container, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }

    private class ResultWrapper {
        ResultModel result;
        boolean isChecked;
        ResultWrapper(ResultModel result, boolean isChecked) {
            this.result = result;
            this.isChecked = isChecked;
        }
        void toggleChecked() { isChecked = !isChecked; }
    }

}