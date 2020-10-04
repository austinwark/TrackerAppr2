package com.sandboxcode.trackerappr2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.adapters.Result.ResultsAdapter;
import com.sandboxcode.trackerappr2.adapters.ShadowVerticalSpaceItemDecorator;
import com.sandboxcode.trackerappr2.adapters.VerticalSpaceItemDecorator;
import com.sandboxcode.trackerappr2.models.ResultModel;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultsFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultsFragment extends Fragment {

    private static final String TAG = "ResultsFragment";
    private Context activityContext;

    private RecyclerView resultListView;
    private List<ResultModel> resultList = new ArrayList<>();

    private DatabaseReference databaseRef;
    private String searchId;
    private ResultsAdapter adapter;
    private int resultCount;

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("ARRAY1", Parcels.wrap(resultList));
    }

    private void getDbReferences() {
        databaseRef = FirebaseDatabase.getInstance().getReference().child("results")
                .child(searchId);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            searchId = getArguments().getString("ID");
        }

        getDbReferences();
        activityContext = getActivity().getApplicationContext();
        FragmentManager fragmentManager = getParentFragmentManager();
        // TODO - add searchID
        adapter = new ResultsAdapter(activityContext, R.layout.result_list_item, resultList, fragmentManager, searchId);
        resultCount = 0;
//        adapter = new ResultsAdapterOld(activityContext, resultList, fm, searchId);

        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ResultModel result = snapshot.getValue(ResultModel.class);
                resultList.add(result);
                resultCount++;
                adapter.notifyItemInserted(resultCount);
                Log.d(TAG, String.valueOf(resultList.size()));
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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activityContext);

        int verticalSpacing = 20;
        VerticalSpaceItemDecorator itemDecorator = new VerticalSpaceItemDecorator(verticalSpacing);
        ShadowVerticalSpaceItemDecorator shadowItemDecorator = new ShadowVerticalSpaceItemDecorator(activityContext, R.drawable.drop_shadow);

        resultListView = (RecyclerView) view.findViewById(R.id.results_view);

        resultListView.setHasFixedSize(true);
        resultListView.setLayoutManager(layoutManager);
        resultListView.addItemDecoration(shadowItemDecorator);
        resultListView.addItemDecoration(itemDecorator);

        resultListView.setAdapter(adapter);
    }

    private View.OnClickListener detailClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            Log.d(TAG, String.valueOf(position));
        }
    };
}
