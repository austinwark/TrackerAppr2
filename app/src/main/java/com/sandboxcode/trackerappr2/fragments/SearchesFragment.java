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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.activities.CreateActivity;
import com.sandboxcode.trackerappr2.adapters.decorators.ShadowVerticalSpaceItemDecorator;
import com.sandboxcode.trackerappr2.adapters.decorators.VerticalSpaceItemDecorator;
import com.sandboxcode.trackerappr2.adapters.search.SearchesAdapter;
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
    private BottomNavigationView toolbarBottom;

    private DatabaseReference databaseRef;
    private FirebaseAuth mAuth;
    private SearchesAdapter adapter;
    private int searchCount;

    private MenuItem deleteMenuItem;
    private ArrayList<String> checkedItems;

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
        checkedItems = new ArrayList<>();

        adapter = new SearchesAdapter(activityContext, R.layout.search_list_item, searchList, this);
        searchCount = 0;
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_searches, menu);

        Menu bottomMenu = toolbarBottom.getMenu();
        deleteMenuItem = bottomMenu.findItem(R.id.action_delete);

        for (int itemIndex = 0; itemIndex < bottomMenu.size(); itemIndex++) {
            bottomMenu.getItem(itemIndex).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });
        }
    }

    public void onItemCheckedChange(String searchId, boolean isChecked) {
//        Log.d(TAG, "Checked: " + checkedItems.indexOf(String.valueOf(searchId)));
        if (isChecked) {
            checkedItems.add(searchId);
        } else {
            checkedItems.remove(String.valueOf(searchId));
        }
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (!checkedItems.isEmpty()) {
            deleteMenuItem.setEnabled(true);
            deleteMenuItem.getIcon().mutate().setAlpha(255);
            deleteMenuItem.getIcon().setAlpha(255);
        } else {
            deleteMenuItem.setEnabled(false);
            deleteMenuItem.getIcon().mutate().setAlpha(5);
            deleteMenuItem.getIcon().setAlpha(5);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getEditActive())
            outState.putBoolean("EDIT_ACTIVE", getEditActive());

        super.onSaveInstanceState(outState);
    }

    public void toggleEdit(boolean newState) {
        if (newState) {
            this.toolbarBottom.setVisibility(View.VISIBLE);
            this.adapter.setCheckboxVisible(true);
        } else {
            this.toolbarBottom.setVisibility(View.INVISIBLE);
            this.adapter.setCheckboxVisible(false);
        }
        this.adapter.notifyDataSetChanged();
    }

    public boolean getEditActive() {
        return this.toolbarBottom.getVisibility() == View.VISIBLE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_edit:
                Log.d(TAG, "Action Edit");
                toggleEdit(true);
                return true;
            case R.id.action_delete:
                deleteSearch();
                return true;
            default:
                Log.d(TAG, "Default");
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteSearch() {

        if (checkedItems.isEmpty())
            Toast.makeText(getActivity(), "A search must be selected before deletion", Toast.LENGTH_SHORT).show();
        else if (checkedItems.size() > 1)
            Toast.makeText(getActivity(), "Only one search can be deleted at a time", Toast.LENGTH_SHORT).show();
        else if (checkedItems.get(0) != null) {
            final String searchId = checkedItems.get(0);
            Toast.makeText(getActivity(), "Deleting " + searchId, Toast.LENGTH_SHORT).show();
            databaseRef.child(searchId).removeValue().addOnCompleteListener(onDeleteListener);
        }
    }

    private OnCompleteListener<Void> onDeleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task task) {
            final String searchId = checkedItems.get(0);
            int searchIndexToDelete = -1;
            if (task.isSuccessful()) {
                for (int searchIndex = 0; searchIndex < searchList.size(); searchIndex++) {
                    if (searchList.get(searchIndex).getId().equals(searchId)) {
                        searchIndexToDelete = searchIndex;
                    }
                }
                if (searchIndexToDelete != -1) {
                    searchList.remove(searchIndexToDelete);
                    adapter.notifyItemRemoved(searchIndexToDelete);
                    checkedItems.remove(0);
                    toggleEdit(false);
                    Toast.makeText(getActivity(), "Search deleted successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Queries");

        toolbarBottom = view.findViewById(R.id.toolbar_bottom);
        toolbarBottom.setItemIconTintList(null);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_create);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreateActivity.class));
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activityContext);

        int verticalSpacing = 20;
        VerticalSpaceItemDecorator itemDecorator = new VerticalSpaceItemDecorator(verticalSpacing);
        ShadowVerticalSpaceItemDecorator shadowItemDecorator = new ShadowVerticalSpaceItemDecorator(activityContext, R.drawable.drop_shadow);

        searchListView = (RecyclerView) view.findViewById(R.id.searches_view);

        searchListView.setHasFixedSize(true);

        searchListView.setLayoutManager(layoutManager);

        searchListView.addItemDecoration(shadowItemDecorator);
        searchListView.addItemDecoration(itemDecorator);

        searchListView.setAdapter(adapter);

        if (savedInstanceState != null) {
            toggleEdit(savedInstanceState.getBoolean("EDIT_ACTIVE"));
        } else {
            // no need to call adapter.notifyDataSetChanged
            toolbarBottom.setVisibility(View.INVISIBLE);
        }

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