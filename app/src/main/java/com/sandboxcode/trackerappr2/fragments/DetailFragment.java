package com.sandboxcode.trackerappr2.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.viewmodels.DetailViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class DetailFragment extends Fragment {

    @SuppressWarnings("unused")
    private static final String TAG = DetailFragment.class.getSimpleName();
    ImageView image;
    private ResultModel result;
    private String searchId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            postponeEnterTransition();
            // Get result and search ID
            result = Parcels.unwrap(getArguments().getParcelable("RESULT"));
            searchId = getArguments().getString("SEARCH_ID");

        } else {
            // TODO -- restart activity? somehow go back to previous fragment?
        }

    }


    private void instantiateUI(View v) {
        TextView title = v.findViewById(R.id.tv_details_title);
        title.setText(result.getTitle());

        TextView price = v.findViewById(R.id.tv_details_price);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setMaximumFractionDigits(0);
        String formattedPrice = formatter.format(Integer.parseInt(result.getPrice()));
        price.setText(formattedPrice);

        TextView stock = v.findViewById(R.id.tv_details_stock);
        stock.setText(result.getStock());
        TextView miles = v.findViewById(R.id.tv_details_miles);
        miles.setText(result.getMiles());
        TextView extColor = v.findViewById(R.id.tv_details_ext_color);
        extColor.setText(result.getExtColor());
        TextView intColor = v.findViewById(R.id.tv_details_int_color);
        intColor.setText(result.getIntColor());
        TextView vin = v.findViewById(R.id.tv_details_vin);
        vin.setText(result.getVin());
        TextView dealer = v.findViewById(R.id.tv_details_dealer);
        dealer.setText(result.getDealer());
        TextView engine = v.findViewById(R.id.tv_details_engine);
        engine.setText(result.getEngine());
        TextView transmission = v.findViewById(R.id.tv_details_transmission);
        transmission.setText(result.getTransmission());
        image = v.findViewById(R.id.detail_image_thumbnail);
        Picasso.get().load(result.getImageUrl()).fit().into(image);
        //        Picasso.get().load(result.getImageUrl()).fit().into(image, new Callback() {
//            @Override
//            public void onSuccess() {
//                startPostponedEnterTransition();
//            }
//
//            @Override
//            public void onError(Exception e) {
//                startPostponedEnterTransition();
//            }
//        });

        ImageButton carfaxImageButton = v.findViewById(R.id.detail_button_carfax);
        carfaxImageButton.setOnClickListener(view -> {
            String carfaxUrl = "https:" + result.getCarfaxLink();
            Uri webpage = Uri.parse(carfaxUrl);
            Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
            if (webIntent.resolveActivity(getActivity().getPackageManager()) != null)
                startActivity(webIntent);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (((AppCompatActivity) getActivity()) != null
                && ((AppCompatActivity) getActivity()).getSupportActionBar() != null) {

            Objects.requireNonNull(((AppCompatActivity) getActivity())
                    .getSupportActionBar()).setTitle("Details");
        }

        // Change isNew field indicating the user has seen the result
        DetailViewModel viewModel = new ViewModelProvider(requireActivity())
                .get(DetailViewModel.class);
        viewModel.setResultHasBeenViewed(result.getVin(), searchId);
        result.setIsNewResult(false);

        instantiateUI(view);
    }
}