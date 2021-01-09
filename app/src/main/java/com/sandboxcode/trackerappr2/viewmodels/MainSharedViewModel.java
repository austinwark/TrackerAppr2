package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
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
    private  MutableLiveData<String> toastMessage = new MutableLiveData<>();
    private final MutableLiveData<Integer> editMenuOpen = new MutableLiveData<>();
    private final MutableLiveData<String> startEditActivity = new MutableLiveData<>();
    private  MutableLiveData<Integer> confirmDeleteSearches = new MutableLiveData<>();
    private final ArrayList<String> checkedItems = new ArrayList<>();
    private final OnCompleteListener<Void> onDeleteListener = task -> {

        if (task.isSuccessful()) {
            toggleEdit();
        }
    };
    /* SearchesFragment */
    private MutableLiveData<List<SearchModel>> allSearches;

    public MainSharedViewModel(Application application) {
        super(application);
        searchRepository = new SearchRepository();
        authRepository = new AuthRepository();
        allSearches = searchRepository.getAllSearches();

        userSignedIn = authRepository.getUserSignedIn();
        signUserOut = authRepository.getSignUserOut();

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

        if (isChecked)
            checkedItems.add(searchId);
        else
            checkedItems.remove(searchId);
    }

    public void handleOnOptionsItemSelected(int itemId) {

        switch (itemId) {
            /* ----- Top Toolbar Menu ----- */
            case R.id.action_edit:
                toggleEdit();
                break;
            case R.id.action_settings:

                // TODO -- fix this hack... When switching to dark mode the fragment observes the
                // LiveDatas again and gets the last value saved in there...
                checkedItems.clear();
                confirmDeleteSearches = new MutableLiveData<>();
                toastMessage = new MutableLiveData<>();

                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

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
                Log.d(TAG, String.valueOf(checkedItems.size()));
                handleDeleteSearches();
                break;
            default:
                break;
        }
    }

    public void toggleEdit() {

        if (getEditMenuOpen().getValue() != null && getEditMenuOpen().getValue() == View.VISIBLE)
            editMenuOpen.postValue(View.INVISIBLE);
        else
            editMenuOpen.postValue(View.VISIBLE);

    }

    public MutableLiveData<Integer> getConfirmDeleteSearches() {
        return confirmDeleteSearches;
    }

    public void handleDeleteSearches() {

        int numberOfSearchesToDelete = checkedItems.size();
        Log.d(TAG, String.valueOf(numberOfSearchesToDelete) + "--------------");
        if (numberOfSearchesToDelete < 1)
            setToastMessage("A search must be selected to delete.");
        else
            confirmDeleteSearches.postValue(numberOfSearchesToDelete);

    }

    public void deleteSearches() {
        Log.d(TAG, String.valueOf(checkedItems.size()));
        searchRepository.delete(checkedItems, onDeleteListener);
        checkedItems.clear();
        setToastMessage("Deleting search.");

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

    // TODO -- Call SearchResults every time? OR only when null and nothing has changed?
    public MutableLiveData<ArrayList<ResultModel>> getSearchResults(String searchId) {
        /* ResultsFragment */
        MutableLiveData<ArrayList<ResultModel>> searchResults = searchRepository.getSearchResults(searchId);
        return searchResults;
    }

    public void editSearch() {
        String searchId;

        if (checkedItems.isEmpty())
            setToastMessage("A search must be selected before editing.");
        else if (checkedItems.size() > 1)
            setToastMessage("Only one search can be edited at a time");
        else {
            searchId = checkedItems.get(0);
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
        checkedItems.clear(); // Reset checked items
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

}
