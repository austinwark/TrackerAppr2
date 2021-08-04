package com.sandboxcode.trackerappr2.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.viewmodels.DetailViewModel;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailFragment extends Fragment {

    public static final String ARG_POSITION = "position";
    public static final String ARG_SEARCH_ID = "search_id";
    public static final String ARG_RESULT = "result";
    private static final String TAG = DetailFragment.class.getSimpleName();

    private int position;
    private String searchId;
    private ResultModel result;
    private ImageView image;

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(int position, String searchId, ResultModel result) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putString(ARG_SEARCH_ID, searchId);
        args.putParcelable(ARG_RESULT, Parcels.wrap(result));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            position = args.getInt(ARG_POSITION);
            searchId = args.getString(ARG_SEARCH_ID);
            result = Parcels.unwrap(args.getParcelable(ARG_RESULT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        instantiateUI(view);
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
}