package com.sandboxcode.trackerappr2.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.activities.CreateActivity;
import com.sandboxcode.trackerappr2.adapters.Search.SearchesAdapter;
import com.sandboxcode.trackerappr2.adapters.ShadowVerticalSpaceItemDecorator;
import com.sandboxcode.trackerappr2.adapters.VerticalSpaceItemDecorator;
import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchesFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchesFragment extends Fragment {

    private static final String TAG = "SearchesFragment";
    private Context activityContext;

    private RecyclerView searchListView;
    private List<SearchModel> searchList = new ArrayList<>();

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private SearchesAdapter adapter;
    private int searchCount;

    public SearchesFragment() {
        // Required empty public constructor
    }

    private void getDbReferences() {
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("queries")
                .child(mAuth.getCurrentUser().getUid());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDbReferences();
        activityContext = getActivity().getApplicationContext();
        FragmentManager fragmentManager = getParentFragmentManager();

        adapter = new SearchesAdapter(activityContext, R.layout.search_list_item, searchList, fragmentManager);
//        adapter = new SearchAdapter(activityContext, searchList);
        searchCount = 0;
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                String key = snapshot.child("id").getValue(String.class);
//                String searchName = snapshot.child("searchName").getValue(String.class);
//                String model = snapshot.child("model").getValue(String.class);
//                String trim = snapshot.child("trim").getValue(String.class);
//                String year = snapshot.child("year").getValue(String.class);
//                String minPrice = snapshot.child("minPrice").getValue(String.class);
//                String maxPrice = snapshot.child("maxPrice").getValue(String.class);
//                SearchModel searchModel = new SearchModel(key, searchName, model, trim, year, minPrice, maxPrice);
                SearchModel searchModel = snapshot.getValue(SearchModel.class);
                searchList.add(searchModel);
                searchCount++;
                adapter.notifyItemInserted(searchCount);
                Log.d(TAG, String.valueOf(searchList.size()));
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

        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_searches, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_searches, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_edit:
                Log.d(TAG, "Action Edit");
                return true;
            default:
                Log.d(TAG, "Default");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Queries");

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_create);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreateActivity.class));
            }
        });

        // 3

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activityContext);

        int verticalSpacing = 20;
        VerticalSpaceItemDecorator itemDecorator = new VerticalSpaceItemDecorator(verticalSpacing);
        ShadowVerticalSpaceItemDecorator shadowItemDecorator = new ShadowVerticalSpaceItemDecorator(activityContext, R.drawable.drop_shadow);

        // 5
        searchListView = (RecyclerView) view.findViewById(R.id.searches_view);

        // 6
        searchListView.setHasFixedSize(true);

        searchListView.setLayoutManager(layoutManager);

        searchListView.addItemDecoration(shadowItemDecorator);
        searchListView.addItemDecoration(itemDecorator);

        // 9
        searchListView.setAdapter(adapter);

    }


    public void viewResults(SearchModel search) {
        Bundle args = new Bundle();
        args.putString("ID", search.getId());
        ResultsFragment fragment = new ResultsFragment();
        fragment.setArguments(args);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }



}