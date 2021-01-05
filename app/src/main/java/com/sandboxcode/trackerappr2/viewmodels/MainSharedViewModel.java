package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.repositories.AuthRepository;
import com.sandboxcode.trackerappr2.repositories.SearchRepository;

import java.util.ArrayList;
import java.util.List;

public class MainSharedViewModel extends AndroidViewModel {

    private static final String TAG = "SearchViewModel";
    private SearchRepository searchRepository;
    private AuthRepository authRepository;
    private FirebaseAuth firebaseAuth;
    /* MainActivity */
    private MutableLiveData<Boolean> userSignedIn;
    private MutableLiveData<Boolean> signUserOut;
    /* SearchesFragment */
    private MutableLiveData<List<SearchModel>> allSearches;
    private MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private MutableLiveData<ArrayList<String>> checkedItems = new MutableLiveData<>();
    private MutableLiveData<Integer> editMenuOpen = new MutableLiveData<>();
    private MutableLiveData<String> startEditActivity = new MutableLiveData<>();
    /* ResultsFragment */
    private MutableLiveData<ArrayList<ResultModel>> searchResults;

    public MainSharedViewModel(Application application) {
        super(application);
        searchRepository = new SearchRepository();
        authRepository = new AuthRepository();
        allSearches = searchRepository.getAllSearches();
        firebaseAuth = FirebaseAuth.getInstance();

//        searchRepository.setListeners();
        userSignedIn = authRepository.getUserSignedIn();
        signUserOut = authRepository.getSignUserOut();

        checkedItems.setValue(new ArrayList<>());
        editMenuOpen.setValue(View.INVISIBLE);
    }

    public MutableLiveData<Boolean> getUserSignedIn() {
        return userSignedIn;
    }

    public void setSearchesListener() {
        searchRepository.setListeners();
    }
//    public MutableLiveData<Boolean> getUserSignedIn() {
//        userSignedIn = authRepository.getUserSignedIn();
//
//        if (userSignedIn.getValue() != null && userSignedIn.getValue()) {
//                searchRepository.setListeners();
//                Log.d(TAG, "setListen");
//        } else
//            Log.d(TAG, "not setListen");
//
//
//        return userSignedIn;
//    }

    public MutableLiveData<List<SearchModel>> getAllSearches() {
        if (allSearches == null)
            allSearches = searchRepository.getAllSearches();
        return allSearches;
    }

    // TODO - RESET LIST WHEN NAVIGATING AWAY FROM SCREEN
    public void updateCheckedSearchesList(String searchId, boolean isChecked) {
        ArrayList<String> localCheckedItems = checkedItems.getValue();

        if (isChecked) {
            localCheckedItems.add(searchId);
            Log.d(TAG, "is checked");
        } else {
            localCheckedItems.remove(searchId);
            Log.d(TAG, "is not checked");
        }

        checkedItems.postValue(localCheckedItems);
    }

    public void handleOnOptionsItemSelected(int itemId) {

        switch (itemId) {
            /* ----- Top Toolbar Menu ----- */
            case R.id.action_edit:
                Log.d(TAG, "action edit");
                toggleEdit();
                break;
            case R.id.action_settings:
                break;
            case R.id.action_logout:
//                authRepository.setUserSignedIn();
                authRepository.signUserOut();
                break;

            /* ----- Bottom Toolbar Menu ----- */
            case R.id.action_search_edit:
                Log.d(TAG, "action search edit");
                editSearch();
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

    public void deleteSearch() {
        ArrayList<String> localCheckedItems = checkedItems.getValue();
        String searchId;

        if (localCheckedItems.isEmpty())
            setToastMessage("A search must be selected before deletion.");
        else if (localCheckedItems.size() > 1)
            setToastMessage("Only one search can be deleted at a time.");
        else {
            searchId = localCheckedItems.get(0);

            searchRepository.delete(searchId, onDeleteListener);
            checkedItems.postValue(new ArrayList<>()); // Reset checked items
            setToastMessage("Deleting search.");
        }
    }

    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public void setToastMessage(String message) {
        toastMessage.postValue(message);
    }

    public MutableLiveData<Integer> getEditMenuOpen() {
        return editMenuOpen;
    }

    public MutableLiveData<ArrayList<String>> getCheckedItems() {
        return checkedItems;
    }

    // TODO -- Call SearchResults every time? OR only when null and nothing has changed?
    public MutableLiveData<ArrayList<ResultModel>> getSearchResults(String searchId) {
        searchResults = searchRepository.getSearchResults(searchId);
        return searchResults;
    }

    public void editSearch() {
        String searchId;
        ArrayList<String> localCheckedItems = getCheckedItems().getValue();

        if (localCheckedItems.isEmpty())
            setToastMessage("A search must be selected before editing.");
        else if (localCheckedItems.size() > 1)
            setToastMessage("Only one search can be edited at a time");
        else {
            searchId = localCheckedItems.get(0);
            setStartEditActivity(searchId);
        }

    }

    public MutableLiveData<String> getStartEditActivity() {
        return startEditActivity;
    }

    public void setStartEditActivity(String searchId) {
        startEditActivity.postValue(searchId);
    }

    public void refreshSearches() {
        searchRepository.getAllSearches();
        toggleEdit(); // hide edit menu
        checkedItems.postValue(new ArrayList<>()); // Reset checked items
    }

    public void refreshResults(String searchId) {
        searchRepository.getSearchResults(searchId);
    }

    public String getUserId() {
        return searchRepository.getUserId();
    }

    public MutableLiveData<Boolean> getSignUserOut() {
        return signUserOut;
    }

    public void setResultHasBeenViewed(String vin, String searchId) {
        searchRepository.setResultHasBeenViewed(vin, searchId);
    }

    @Override
    protected void onCleared() {
        // TODO -- unsubscribe listeners in repository
    }

    private OnCompleteListener<Void> onDeleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {

            if (task.isSuccessful()) {
                Log.d(TAG, "Delete Result: SUCCESS");
                toggleEdit();
            } else
                Log.d(TAG, "Delete Result: FAILURE");
        }
    };

}
