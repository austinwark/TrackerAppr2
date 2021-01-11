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
import com.sandboxcode.trackerappr2.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.List;

public class MainSharedViewModel extends AndroidViewModel {

    private static final String TAG = "SearchViewModel";
    private final SearchRepository searchRepository;
    private final AuthRepository authRepository;
    /* MainActivity */
    private final MutableLiveData<Boolean> userSignedIn;
    private final MutableLiveData<Boolean> signUserOut;
    /* Searches Fragment */
    private final SingleLiveEvent<String> toastMessage = new SingleLiveEvent<>();
    private final MutableLiveData<Integer> editMenuOpen = new MutableLiveData<>();
    private final MutableLiveData<String> startEditActivity = new MutableLiveData<>();
    private final SingleLiveEvent<Integer> confirmDeleteSearches = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> openSettingsScreen = new SingleLiveEvent<>();
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
        Log.d(TAG, "viewmodel CONSTRUCTOR=========");
        searchRepository = new SearchRepository();
        authRepository = new AuthRepository();
        allSearches = searchRepository.getAllSearches();

        userSignedIn = authRepository.getUserSignedIn();
        signUserOut = authRepository.getSignUserOut();

    }

    public void saveState() {

    }

    public ArrayList<String> getCheckedItems() {
        return checkedItems;
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
                openSettingsScreen.setValue(true);
                break;
            case R.id.action_logout:
                authRepository.signUserOut();
                break;

            /* ----- Bottom Toolbar Menu ----- */
            case R.id.action_search_edit:
                editSearch();
                break;
            case R.id.action_delete:
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

    public SingleLiveEvent<Integer> getConfirmDeleteSearches() {
        return confirmDeleteSearches;
    }

    public void handleDeleteSearches() {

        int numberOfSearchesToDelete = checkedItems.size();
        if (numberOfSearchesToDelete < 1)
            setToastMessage("A search must be selected to delete.");
        else
            confirmDeleteSearches.setValue(numberOfSearchesToDelete);

    }

    public void deleteSearches() {
        Log.d(TAG, String.valueOf(checkedItems.size()));
        searchRepository.delete(checkedItems, onDeleteListener);
        checkedItems.clear();
        setToastMessage("Deleting search.");

    }

    public SingleLiveEvent<String> getToastMessage() {
        return toastMessage;
    }

    public void setToastMessage(String message) {
        toastMessage.setValue(message);
    }

    public MutableLiveData<Integer> getEditMenuOpen() {
        return editMenuOpen;
    }

    // TODO -- Call SearchResults every time? OR only when null and nothing has changed?
    public MutableLiveData<ArrayList<ResultModel>> getSearchResults(String searchId) {
        /* ResultsFragment */
        return searchRepository.getSearchResults(searchId);
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

    public SingleLiveEvent<Boolean> getOpenSettingsScreen() { return openSettingsScreen; }

    //    public void getSavedSettings(String userId) {
//        authRepository.getSavedSettings(userId);
//    }

    @Override
    protected void onCleared() {
        // TODO -- unsubscribe listeners in repository
    }

}
