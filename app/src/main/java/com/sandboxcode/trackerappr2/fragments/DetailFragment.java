package com.sandboxcode.trackerappr2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.models.SearchResultModel;

import org.parceler.Parcels;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    private static final String TAG = "DetailFragment";
    private Context activityContext;
    private DatabaseReference databaseRef;
    private String searchId;
    private SearchResultModel result;


    public DetailFragment() {
    }

    private void getDbReferences() {
        databaseRef = FirebaseDatabase.getInstance().getReference().child("results")
                .child(searchId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            searchId = getArguments().getString("SEARCH_ID");
            result = Parcels.unwrap(getArguments().getParcelable("RESULT"));
            getDbReferences();
            Log.d(TAG, result.toString());
        } else
            Log.d(TAG, "NULL");
    }

    private void instantiateUI(View v) {
        TextView title = v.findViewById(R.id.tv_details_title);
        title.setText(result.getTitle());
        TextView price = v.findViewById(R.id.tv_details_price);
        price.setText(result.getPrice());
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Details");
        instantiateUI(view);

    }
}