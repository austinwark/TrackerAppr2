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
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.activities.CreateActivity;
import com.sandboxcode.trackerappr2.adapters.SearchAdapter;
import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchesFragment #newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchesFragment extends Fragment {

    private static final String TAG = "SearchesFragment";
    private Context activityContext;
    private ListView mListView;
    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private ArrayList<SearchModel> searchList = new ArrayList<>();
    private SearchAdapter adapter;

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
        adapter = new SearchAdapter(activityContext, searchList);
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key = snapshot.child("id").getValue(String.class);
                String searchName = snapshot.child("searchName").getValue(String.class);
                String model = snapshot.child("model").getValue(String.class);
                String trim = snapshot.child("trim").getValue(String.class);
                String year = snapshot.child("year").getValue(String.class);
                String minPrice = snapshot.child("minPrice").getValue(String.class);
                String maxPrice = snapshot.child("maxPrice").getValue(String.class);
                SearchModel searchModel = new SearchModel(key, searchName, model, trim, year, minPrice, maxPrice);
                searchList.add(searchModel);
                adapter.notifyDataSetChanged();
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

        mListView = view.findViewById(R.id.lv_searches);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchModel searchModel = (SearchModel) parent.getItemAtPosition(position);

                viewResults(searchModel);
            }
        });
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