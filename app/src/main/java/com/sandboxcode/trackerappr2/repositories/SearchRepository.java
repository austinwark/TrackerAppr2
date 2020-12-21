package com.sandboxcode.trackerappr2.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.utils.WebScraper;

import java.util.ArrayList;
import java.util.List;

public class SearchRepository {

    private static final String TAG = "SearchRepository";
    private static final FirebaseAuth AUTH_REF = FirebaseAuth.getInstance();
    private static final DatabaseReference DATABASE_REF = FirebaseDatabase.getInstance().getReference();
//    private static final DatabaseReference DATABASE_REF = FirebaseDatabase.getInstance()
//            .getReference().child("queries").child(AUTH_REF.getCurrentUser().getUid());

    private final MyValueEventListener listener = new MyValueEventListener();
    private MutableLiveData<List<SearchModel>> allSearches = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> checkedItems = new MutableLiveData<>();

    public SearchRepository() {
        setListeners();
//        allSearches.postValue(new ArrayList<SearchModel>());
    }

    private void setListeners() {
        Log.d(TAG, "setListeners");
        DATABASE_REF.child("queries").child(AUTH_REF.getCurrentUser().getUid())
                .addValueEventListener(listener);
    }

    @NonNull
    public MutableLiveData<List<SearchModel>> getAllSearches() {
        return allSearches;
    }

    // TODO - Change to ChildEventListener
    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<SearchModel> searchList = new ArrayList<>();
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                searchList.add(child.getValue(SearchModel.class));
                Log.d(TAG, "searchList.add");
            }

            // TODO - setValue?
            allSearches.postValue(searchList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "Can't listen to query " + DATABASE_REF, databaseError.toException());
        }
    }

    public void delete(String searchId, OnCompleteListener<Void> onCompleteListener) {
        DATABASE_REF.child("queries").child(AUTH_REF.getCurrentUser().getUid())
                .child(searchId).removeValue().addOnCompleteListener(onCompleteListener);

        DATABASE_REF.child("results").child(searchId).removeValue();

    }

    public void create(String name, String model, String trim, String year, String minPrice, String maxPrice) {
        //TODO-- Add completed check and return boolean to confirm success
        final String KEY = DATABASE_REF.child("queries").child(AUTH_REF.getCurrentUser().getUid()).push().getKey();
        SearchModel searchModel = new SearchModel(KEY, name, model, trim, year, minPrice, maxPrice);
        WebScraper scraper = new WebScraper(searchModel, DATABASE_REF, AUTH_REF.getCurrentUser().getUid());
        scraper.execute();
    }
}
