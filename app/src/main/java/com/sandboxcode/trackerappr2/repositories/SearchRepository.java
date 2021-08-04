package com.sandboxcode.trackerappr2.repositories;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.room_components.ResultDao;
import com.sandboxcode.trackerappr2.room_components.SearchDao;
import com.sandboxcode.trackerappr2.room_components.SearchRoomDatabase;
import com.sandboxcode.trackerappr2.utils.AsyncResponse;
import com.sandboxcode.trackerappr2.utils.SingleLiveEvent;
import com.sandboxcode.trackerappr2.utils.WebScraper;

import java.util.ArrayList;
import java.util.List;

public class SearchRepository implements AsyncResponse {

    private static final String TAG = "SearchRepository";
    private static final FirebaseAuth AUTH_REF = FirebaseAuth.getInstance();
    private static final DatabaseReference DATABASE_REF = FirebaseDatabase.getInstance().getReference();
    private final AuthRepository authRepository = new AuthRepository();

    private final SearchDao searchDao;
    private final ResultDao resultDao;

    /* Fragment Searches */
    private final SearchesListener searchesListener = new SearchesListener();
    private final MutableLiveData<List<SearchModel>> allSearches = new MutableLiveData<>();
    private final LiveData<List<SearchModel>> allRoomSearches;
    /* Fragment Results */
    private final SingleLiveEvent<List<ResultModel>> searchResults = new SingleLiveEvent<>();

    /* Activity Edit */
    private final MutableLiveData<SearchModel> singleSearch = new MutableLiveData<>();
    private final MutableLiveData<Boolean> changesSaved = new MutableLiveData<>();

    /* Both */
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    /* TODO - Get rid of Application parameter to ease unit testing.
       See --> https://developer.android.com/codelabs/android-room-with-a-view#8 */
    public SearchRepository(Application application) {
        SearchRoomDatabase db = SearchRoomDatabase.getDatabase(application);
        searchDao = db.getSearchDao();
        resultDao = db.getResultDao();

        allRoomSearches = searchDao.loadAllSearches();
    }

    public void setListeners() {

        DATABASE_REF.child("queries").child(authRepository.getUserId())
                .addValueEventListener(searchesListener);
    }

    public void retrieveSearch(String searchId) {

        DATABASE_REF.child("queries").child(authRepository.getUserId())
                .child(searchId).addListenerForSingleValueEvent(new SingleSearchListener());
    }

    public LiveData<SearchModel> retrieveRoomSearch(String searchId) {
          return searchDao.loadSingleSearch(searchId);
    }

    @NonNull
    public MutableLiveData<List<SearchModel>> getAllSearches() {
        return allSearches;
    }

    public LiveData<List<SearchModel>> getAllRoomSearches() { return allRoomSearches; }

    public MutableLiveData<SearchModel> getSingleSearch() {
        return singleSearch;
    }

    public void delete(List<String> searchesToDelete, OnCompleteListener<Void> onCompleteListener) {
        if (authRepository.getUserId() != null) {


            for (String searchId : searchesToDelete) {
                DATABASE_REF.child("queries").child(authRepository.getUserId())
                    .child(searchId).removeValue().addOnCompleteListener(onCompleteListener);

                DATABASE_REF.child("results").child(authRepository.getUserId()).child(searchId)
                        .removeValue();

                // Delete search in local DB
                searchDao.deleteById(searchId);
            }
        }

    }

    public SingleLiveEvent<List<ResultModel>> getSearchResults(String searchId) {
        List<ResultModel> results = resultDao.loadAllResults(searchId);
        searchResults.setValue(results);
        return searchResults;
    }

    public ResultModel getSingleSearchResult(String vin) {
        ResultModel result = resultDao.loadSingleResult(vin);
        return result;
    }

    //    public SingleLiveEvent<ArrayList<ResultModel>> getSearchResults(String searchId) {
//        DATABASE_REF.child("results").child(authRepository.getUserId()).child(searchId)
//                .addListenerForSingleValueEvent(new ResultsListener());
//
//        return searchResults;
//    }

    public void create(String name, String model, String trim, String minYear, String maxYear,
                       String minPrice, String maxPrice, String allDealerships) {

        //TODO-- Add completed check and return boolean to confirm success
        final String KEY = DATABASE_REF.child("queries")
                .child(authRepository.getUserId()).push().getKey();

        SearchModel searchModel = new SearchModel(KEY, name, model, trim, minYear,
                maxYear, minPrice, maxPrice, allDealerships);
        searchModel.setCreatedDate();
        searchModel.setLastEditedDate();

        DATABASE_REF.child("queries").child(authRepository.getUserId()).child(KEY)
                .setValue(searchModel).addOnSuccessListener(aVoid -> {

            WebScraper scraper = new WebScraper(searchModel,
                    DATABASE_REF, authRepository.getUserId());
            scraper.setDelegate(this);
            scraper.execute();
        });

        searchDao.insertSearches(searchModel);
    }

    public void saveChanges(SearchModel search) {
        DATABASE_REF.child("queries").child(authRepository.getUserId())
                .child(search.getId()).setValue(search)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        setChangesSaved(true);
                        WebScraper scraper = new WebScraper(search,
                                DATABASE_REF, authRepository.getUserId());
                        scraper.setDelegate(this);
                        scraper.execute();

                    } else {
                        setErrorMessage("Error saving changes.");
                    }
                });
    }

    public void saveRoomChanges(SearchModel search) {
        int numberOfRowsUpdated = searchDao.updateSearch(search);

        if (numberOfRowsUpdated >= 1) {
            setChangesSaved(true);
            WebScraper scraper = new WebScraper(search,
                    DATABASE_REF, authRepository.getUserId());
            scraper.setDelegate(this);
            scraper.execute();

        } else {
            setErrorMessage("Error saving changes.");
        }
    }

    public MutableLiveData<Boolean> getChangesSaved() {
        return changesSaved;
    }

    public void setChangesSaved(boolean saved) {
        changesSaved.postValue(saved);
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String message) {
        errorMessage.postValue(message);
    }

    public String getUserId() {
        if (AUTH_REF.getCurrentUser() != null)
            return authRepository.getUserId();
        else
            return null;
    }

    // Called by DetailFragment to mark a result as viewed
    public void setResultHasBeenViewed(String vin, String searchId) {

        // Set isNewResult value in result document to false
        DATABASE_REF.child("results").child(authRepository.getUserId())
                .child(searchId).child(vin).child("isNewResult").setValue(false);

        // Update isNewResult field in Room DB
        ResultModel viewedResult = resultDao.loadSingleResult(vin);
        viewedResult.setIsNewResult(false);
        resultDao.updateResult(viewedResult);

        // Update the search's numberOfNewResults in Room DB
        SearchModel viewedSearch = searchDao.getSingleSearch(searchId);
        viewedSearch.setNumberOfNewResults(viewedSearch.getNumberOfNewResults() - 1);
        searchDao.updateSearch(viewedSearch);

        // Get numberOfNewResults from correlated search document to update its numberOfNewResults
        DATABASE_REF.child("queries").child(authRepository.getUserId()).child(searchId)
                .child("numberOfNewResults").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    long numberOfNewResults = (Long) snapshot.getValue();
                    numberOfNewResults--;
                    DATABASE_REF.child("queries").child(authRepository.getUserId()).child(searchId)
                            .child("numberOfNewResults").setValue(numberOfNewResults);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void processResults(List<ResultModel> searchResults, SearchModel search) {

        List<ResultModel> currentResults;
        int numberOfResults = 0;
        int numberOfNewResults = 0;

        // Save thg search's current results in an ArrayList
        currentResults = resultDao.loadAllResultsOnce();

        if (currentResults != null && !currentResults.isEmpty()) {

            // Check each newly scraped result against each current result
            for (ResultModel scrapedResult : searchResults) {

                Log.d(TAG, "---- Scraped Result ----");

                if (currentResults.contains(scrapedResult)) {

                    ResultModel matchingCurrentResult =
                            currentResults.get(currentResults.indexOf(scrapedResult));

                    // If result is not new -- set newly scraped result to false
                    if (!matchingCurrentResult.getIsNewResult()) {
                        scrapedResult.setIsNewResult(false); // this is true by default

                      // Otherwise -- increment # of new results
                    } else {
                        numberOfNewResults++;
                    }
                } else {
                     numberOfNewResults++;
                }
            }
        } else {
            numberOfNewResults = searchResults.size();
        }

        // TODO -- will this delete all results with same searchId??
        resultDao.deleteAll(search.getId());

        for (ResultModel result : searchResults) {
            resultDao.insertResults(result);
            numberOfResults++;
        }

        // Set number of total & new results in search document
        search.setNumberOfResults(numberOfResults);
        search.setNumberOfNewResults(numberOfNewResults);
        searchDao.updateSearch(search);

    }

//    @Override
//    public void processResults(ArrayList<ResultModel> searchResults, String searchId) {
//        String userUid = authRepository.getUserId();
//
//        DatabaseReference resultsRef = DATABASE_REF.child("results").child(userUid).child(searchId);
//        resultsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ArrayList<ResultModel> currentResults = new ArrayList<>();
//                int numberOfResults = 0;
//                int numberOfNewResults = 0;
//
//                // Save the search's current results in an ArrayList
//                if (snapshot.hasChildren())
//                    for (DataSnapshot currentResult : snapshot.getChildren())
//                        currentResults.add(currentResult.getValue(ResultModel.class));
//
//                if (!currentResults.isEmpty()) {
//
//                    // Check each newly scraped result against each current result
//                    for (ResultModel scrapedResult : searchResults) {
//                        if (currentResults.contains(scrapedResult)) {
//
//                            ResultModel matchingCurrentResult =
//                                    currentResults.get(currentResults.indexOf(scrapedResult));
//
//                            if (!matchingCurrentResult.getIsNewResult()) {
//                                scrapedResult.setIsNewResult(false); // this is true by default
//                            } else {
//                                numberOfNewResults++;
//                            }
//                        } else {
//                            numberOfNewResults++;
//                        }
//                    }
//
//                } else {
//                    numberOfNewResults = searchResults.size();
//                }
//
//                // Reset the currentResults document in firebase
//                resultsRef.setValue(null);
//
//                // Reset the current results in room DB
//                resultDao.deleteAll(searchId);
//
//                // Save each new result and keep track of the total count
//                for (ResultModel result : searchResults) {
//                    resultsRef.child(result.getVin()).setValue(result);
//                    resultDao.insertResults(result);
//                    numberOfResults++;
//                }
//
//                // Set number of total & new results in search document
//                DATABASE_REF.child("queries").child(userUid).child(searchId)
//                        .child("numberOfResults").setValue(numberOfResults);
//                DATABASE_REF.child("queries").child(userUid).child(searchId)
//                        .child("numberOfNewResults").setValue(numberOfNewResults);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//            }
//        });
//    }

    /* Load all saved searches in firebase to local database */
    /* TODO -- Updates RoomSearches LiveData TWICE, fix so it only does it once */
    private void updateRoomDatabase(List<SearchModel> searches) {

        Log.d(TAG, "Updating database");

        for (SearchModel search : searches) {
            Log.d(TAG, "search: " + search.getId());
            searchDao.insertSearches(search);
        }

        List<SearchModel> localSearches = searchDao.loadAllSearchesOnce();
  
        if (localSearches != null) {
            Log.d(TAG, "local searches not null");
            Log.d(TAG, "SIZE OF LOCAL SEARCHES: " + localSearches.size());

            for (SearchModel search : localSearches) {

                // Check if local search is in firebase
                DATABASE_REF.child("queries").child(authRepository.getUserId())
                        .child(search.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // If local search does not exist in firebase, delete it
                        if (!snapshot.exists()) {
                            Log.d(TAG, "snapshot " + search.getSearchName() + " does not exist");
                            searchDao.deleteSearches(search);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }

                });
            }
        }
    }

    private class SingleSearchListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            SearchModel search = snapshot.getValue(SearchModel.class);
            singleSearch.postValue(search);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    }

    // TODO - Change to ChildEventListener
    private class SearchesListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            List<SearchModel> searchList = new ArrayList<>();
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                searchList.add(child.getValue(SearchModel.class));
            }

            allSearches.postValue(searchList);

            // TODO -- Decide how often to sync with firebase (maybe change to updateFirebaseDatabase)
//            updateRoomDatabase(searchList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    }

    private class ResultsListener implements ValueEventListener {

        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            ArrayList<ResultModel> results = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                results.add(child.getValue(ResultModel.class));
            }

            searchResults.setValue(results);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    }

}
