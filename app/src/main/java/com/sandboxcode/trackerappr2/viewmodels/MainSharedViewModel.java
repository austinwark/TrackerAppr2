package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.repositories.AuthRepository;
import com.sandboxcode.trackerappr2.repositories.SearchRepository;

import java.util.ArrayList;
import java.util.List;

public class MainSharedViewModel extends AndroidViewModel {

    private static final String TAG = "SearchViewModel";
    private final SearchRepository searchRepository;
    private final AuthRepository authRepository;
    /* MainActivity */
    private final MutableLiveData<Boolean> userSignedIn;
    private final MutableLiveData<Boolean> signUserOut;
    /* SearchesFragment */
    private MutableLiveData<List<SearchModel>> allSearches;
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<String>> checkedItems = new MutableLiveData<>();
    private final MutableLiveData<Integer> editMenuOpen = new MutableLiveData<>();
    private final MutableLiveData<String> startEditActivity = new MutableLiveData<>();

    public MainSharedViewModel(Application application) {
        super(application);
        searchRepository = new SearchRepository();
        authRepository = new AuthRepository();
        allSearches = searchRepository.getAllSearches();

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

    public MutableLiveData<List<SearchModel>> getAllSearches() {
        if (allSearches == null)
            allSearches = searchRepository.getAllSearches();
        return allSearches;
    }

    // TODO - RESET LIST WHEN NAVIGATING AWAY FROM SCREEN
    public void updateCheckedSearchesList(String searchId, boolean isChecked) {
        ArrayList<String> localCheckedItems = checkedItems.getValue();

        if (isChecked && localCheckedItems != null) {
            localCheckedItems.add(searchId);
        } else if (localCheckedItems != null) {
            localCheckedItems.remove(searchId);
        }

        checkedItems.postValue(localCheckedItems);
    }

    public void handleOnOptionsItemSelected(int itemId) {

        switch (itemId) {
            /* ----- Top Toolbar Menu ----- */
            case R.id.action_edit:
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
                editSearch();
                break;
            case R.id.action_delete:
                deleteSearch();
                break;
            default:
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
        /* ResultsFragment */
        MutableLiveData<ArrayList<ResultModel>> searchResults = searchRepository.getSearchResults(searchId);
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

    private final OnCompleteListener<Void> onDeleteListener = task -> {

        if (task.isSuccessful()) {
            toggleEdit();
        }
    };

}
