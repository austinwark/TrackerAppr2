package com.sandboxcode.trackerappr2.adapters.result;

import android.content.Context;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.DetailFragment;
import com.sandboxcode.trackerappr2.fragments.ResultsFragment;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.Locale;

public class ResultsHolder extends RecyclerView.ViewHolder {

    private FragmentManager fragmentManager;
    private Context context;
    private ResultModel resultModel;

    private TextView title;
    private TextView stock;
    private TextView price;
    private ImageView thumbnail;
    private Button detailsButton;
    private ImageView newIcon;
    private String searchId;

    public ResultsHolder(Context context, View itemView, FragmentManager fragmentManager,
                         String searchId) {
        super(itemView);

        this.context = context;

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
        stock.setText("#" + result.getStock());

        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setMaximumFractionDigits(0);
        String formattedPrice = formatter.format(Integer.parseInt(result.getPrice()));
        price.setText(formattedPrice);

        thumbnail.setTransitionName("result_to_detail_transition_" + position);
        Picasso.get().load(result.getImageUrl()).fit().into(thumbnail);
        this.detailsButton.setOnClickListener(buttonClickListener);

        newIcon.setVisibility(result.getIsNewResult() ? View.VISIBLE : View.INVISIBLE);
    }

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewDetails(resultModel, searchId);
        }
    };

    public void viewDetails(ResultModel result, String searchId) {

        Bundle args = new Bundle();
        args.putParcelable("RESULT", Parcels.wrap(result));
        args.putString("SEARCH_ID", searchId);
        ResultsFragment currentFragment = (ResultsFragment) fragmentManager.findFragmentById(R.id.main_fragment_container);
        DetailFragment fragment = new DetailFragment();

//        fragment.setSharedElementEnterTransition(new DetailsTransition());
//        fragment.setEnterTransition(new Slide(Gravity.TOP).setDuration(750).setStartDelay(500));
//        currentFragment.setExitTransition(new Fade().setDuration(500));
//        currentFragment.setSharedElementReturnTransition(new DetailsTransition());

        fragment.setArguments(args);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.addSharedElement(thumbnail, "transName");
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private class DetailsTransition extends TransitionSet {
        public DetailsTransition() {
            init();
        }

        public DetailsTransition(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds()).
                    addTransition(new ChangeTransform()).
                    addTransition(new ChangeImageTransform()).setDuration(500);
        }
    }

}