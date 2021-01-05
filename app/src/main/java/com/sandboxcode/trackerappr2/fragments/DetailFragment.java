package com.sandboxcode.trackerappr2.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.viewmodels.MainSharedViewModel;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.text.NumberFormat;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    private static final String TAG = "DetailFragment";
    private MainSharedViewModel viewModel;
    private ResultModel result;
    private String searchId;

    ImageView image;
    String imageTransitionName;


    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            // Get result and search ID
            result = Parcels.unwrap(getArguments().getParcelable("RESULT"));
            searchId = getArguments().getString("SEARCH_ID");

            // Change isNew field indicating the user has seen the result
            viewModel = new ViewModelProvider(requireActivity()).get(MainSharedViewModel.class);
            viewModel.setResultHasBeenViewed(result.getVin(), searchId);
            result.setIsNewResult(false);

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
        image = v.findViewById(R.id.result_image_thumbnail);
//        Picasso.get().setLoggingEnabled(true);
        Picasso.get().load(result.getImageUrl()).fit().into(image);

        Button test = (Button) v.findViewById(R.id.testing);
//        test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Calendar cal = Calendar.getInstance();
//                int days = Calendar.SUNDAY + (7 - cal.get(Calendar.DAY_OF_WEEK));
//                cal.set(Calendar.DAY);
//                Log.d(TAG, "DAYS: " + days);
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Details");
        instantiateUI(view);
//        Log.d("DetailFragment", imageTransitionName);

    }

    public void setImageTransitionName(String name) {
        imageTransitionName = name;
    }
}