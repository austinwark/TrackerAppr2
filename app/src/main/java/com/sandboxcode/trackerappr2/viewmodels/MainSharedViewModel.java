package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.repositories.SearchRepository;

import java.util.ArrayList;
import java.util.List;

public class MainSharedViewModel extends AndroidViewModel {

    private static final String TAG = "SearchViewModel";
    private SearchRepository repository;

    private MutableLiveData<Boolean> userSignedOut;
    private MutableLiveData<List<SearchModel>> allSearches;
    private MutableLiveData<String> toastMessage;
    private MutableLiveData<ArrayList<String>> checkedItems;
    private MutableLiveData<Integer> editMenuOpen;
    private MutableLiveData<SearchModel> search;
    private MutableLiveData<ArrayList<ResultModel>> searchResults;

    public MainSharedViewModel(Application application) {
        super(application);
        repository = new SearchRepository();
        allSearches = repository.getAllSearches();
//        checkedItems.postValue(new ArrayList<>());
//        editMenuOpen.postValue(View.INVISIBLE);
    }

    public MutableLiveData<List<SearchModel>> getAllSearches() {
        if (allSearches == null)
            allSearches = repository.getAllSearches();
        return allSearches;
    }

    // TODO - RESET LIST WHEN NAVIGATING AWAY FROM SCREEN
    public void updateCheckedSearchesList(String searchId, boolean isChecked) {
        ArrayList<String> localCheckedItems;

        if (checkedItems == null) {
            checkedItems = new MutableLiveData<>();
            localCheckedItems = new ArrayList<>();
        } else {
            localCheckedItems = checkedItems.getValue();
        }

        if (isChecked)
            localCheckedItems.add(searchId);
        else
            localCheckedItems.remove(searchId);

        checkedItems.postValue(localCheckedItems);
    }

    public void deleteSearch() {
        ArrayList<String> localCheckedItems = checkedItems.getValue();
        String searchId;

        if (localCheckedItems.isEmpty())
            setToastMessage("A search must be selected before deletion.");
        else if (localCheckedItems.size() > 1)
            setToastMessage("Only one search can be deleted at a time.");
        else {
            searchId = localCheckedItems.get(0);

            repository.delete(searchId, onDeleteListener);
            setToastMessage("Deleting search.");
        }
    }

    public void handleBottomOnOptionsItemSelected(int itemId) {

        switch (itemId) {
            case R.id.action_edit:
                Log.d(TAG, "action edit");
                toggleEdit();
                break;
            case R.id.action_delete:
                Log.d(TAG, "action delete");
                deleteSearch();
                break;
            default:
                Log.d(TAG, "default");
                break;
        }
    }

    public void toggleEdit() {

        if (getEditMenuOpen().getValue() == View.VISIBLE)
            editMenuOpen.postValue(View.INVISIBLE);
        else
            editMenuOpen.postValue(View.VISIBLE);

    }

    public MutableLiveData<String> getToastMessage() {
        if (toastMessage == null) {
            toastMessage = new MutableLiveData<>();
        }
        return toastMessage;
    }

    public void setToastMessage(String message) {
        toastMessage.postValue(message);
    }

    public MutableLiveData<Integer> getEditMenuOpen() {
        if (editMenuOpen == null) {
            editMenuOpen = new MutableLiveData<>();
            editMenuOpen.postValue(View.INVISIBLE);
        }
        return editMenuOpen;
    }

    public MutableLiveData<ArrayList<String>> getCheckedItems() {
        if (checkedItems == null) {
            checkedItems = new MutableLiveData<>();
            checkedItems.postValue(new ArrayList<>());
        }
        return checkedItems;
    }

    public MutableLiveData<Boolean> getUserSignedOut() {
        if (userSignedOut == null) {
            userSignedOut = new MutableLiveData<>();
        }
        return userSignedOut;
    }

    public void setUserSignedOut(boolean signedOut) {
        userSignedOut.postValue(signedOut);
    }

    public void setSearch(SearchModel search) {
        if (this.search == null)
            this.search = new MutableLiveData<>();

        this.search.postValue(search);
    }

    public MutableLiveData<ArrayList<ResultModel>> getSearchResults(String searchId) {
        if (searchResults == null)
            searchResults = repository.getSearchResults(searchId);
        return searchResults;
    }

    private OnCompleteListener<Void> onDeleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            List<SearchModel> localAllSearches = allSearches.getValue();
            ArrayList<String> localCheckedItems = checkedItems.getValue();
            String searchId = localCheckedItems.get(0);

            if (task.isSuccessful()) {
                Log.d(TAG, "Delete Result: SUCCESS");
                int searchIndexToDelete = -1;
                // Confirm the search is deleted from list
                for (int itemIndex = 0; itemIndex < localAllSearches.size(); itemIndex++) {
                    if (localAllSearches.get(itemIndex).getId().equals(searchId)) {
                        searchIndexToDelete = itemIndex;
                    }
                }
                if (searchIndexToDelete == -1) {
                    localCheckedItems.remove(0);
                    checkedItems.postValue(localCheckedItems);
                }
            } else
                Log.d(TAG, "Delete Result: FAILURE");
        }
    };

}
