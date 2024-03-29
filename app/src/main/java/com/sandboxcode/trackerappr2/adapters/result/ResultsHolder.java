package com.sandboxcode.trackerappr2.adapters.result;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.DetailPagerFragment;
import com.sandboxcode.trackerappr2.fragments.ResultsFragment;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    private View emptyCheckbox;
    private int editActive;

    public ResultsHolder(View itemView, FragmentManager fragmentManager,
                         String searchId, ResultsFragment resultsFragment) {
        super(itemView);
        cardView = itemView.findViewById(R.id.result_layout_card);
        title = itemView.findViewById(R.id.result_text_title);
        stock = itemView.findViewById(R.id.result_text_stock);
        price = itemView.findViewById(R.id.result_text_price);
        thumbnail = itemView.findViewById(R.id.result_image_thumbnail);
        emptyCheckbox = itemView.findViewById(R.id.result_shape_ring);
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

        Picasso.get().load(result.getImageUrl()).into(thumbnail);

        cardView.setChecked(result.isChecked());
        cardView.setOnClickListener(cardViewClickListener);
        cardView.setOnLongClickListener(cardViewLongClickListener);

//        newIcon.setVisibility(result.getIsNewResult() ? View.VISIBLE : View.INVISIBLE);

        this.position = position;

    }

    private final View.OnClickListener cardViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (editActive == View.VISIBLE) {
                boolean isCurrentlyChecked = resultModel.isChecked();
                cardView.setChecked(!isCurrentlyChecked);
                resultModel.setIsChecked(!isCurrentlyChecked);
                if (!isCurrentlyChecked)
                    resultsFragment.addCheckedResult(resultModel);
                else
                    resultsFragment.removeCheckedResult(resultModel);
            } else {
                viewDetails(resultModel, searchId);
            }

            //            viewDetails(resultModel, searchId);

        }
    };

    private final View.OnLongClickListener cardViewLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            boolean isCurrentlyChecked = resultModel.isChecked();
            cardView.setChecked(!isCurrentlyChecked);
            resultModel.setIsChecked(!isCurrentlyChecked);

            if (!isCurrentlyChecked) {
                resultsFragment.addCheckedResult(resultModel);
                resultsFragment.setEditMenuActive();
            }
            else
                resultsFragment.removeCheckedResult(resultModel);

            return true;
        }
    };

    public void viewDetails(ResultModel result, String searchId) {
        List<ResultModel> results = new ArrayList<>(1);
        results.add(result);
        DetailPagerFragment fragment = DetailPagerFragment.newInstance(results, searchId);

        fragmentManager
                .beginTransaction()
                .replace(R.id.main_fragment_container, fragment, DetailPagerFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();

    }

    public void setEditActive(int editActive) {
        this.editActive = editActive;
        emptyCheckbox.setVisibility(editActive);
        if (editActive == View.INVISIBLE) {
            cardView.setChecked(false);
            resultModel.setIsChecked(false);
        }
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

}