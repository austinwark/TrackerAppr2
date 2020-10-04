package com.sandboxcode.trackerappr2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.adapters.ResultsAdapter;
import com.sandboxcode.trackerappr2.models.SearchResultModel;

import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {

    private static final String TAG = "ResultsFragment";
    private Context activityContext;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private String searchId;
    private ListView mListView;
    private ResultsAdapter adapter;
    private ArrayList<SearchResultModel> resultList = new ArrayList<>();

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ARRAY1", Parcels.wrap(resultList));
    }

    private void getDbReferences() {
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("results")
                .child(searchId);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchId = getArguments().getString("ID");
        }
        activityContext = getActivity().getApplicationContext();
        FragmentManager fm = getParentFragmentManager();
        adapter = new ResultsAdapter(activityContext, resultList, fm, searchId);
        getDbReferences();

        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                SearchResultModel result = snapshot.getValue(SearchResultModel.class);

                resultList.add(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Results");
        mListView = view.findViewById(R.id.lv_results);
        mListView.setAdapter(adapter);


    }

    private View.OnClickListener detailClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Log.d(TAG, String.valueOf(position));
        }
    };
}
