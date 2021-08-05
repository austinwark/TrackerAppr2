package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
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

public class SearchesViewModel extends AndroidViewModel implements SearchRepository.CustomOnCompleteListener {

    private static final String TAG = SearchesViewModel.class.getSimpleName();
    private final SearchRepository searchRepository;
    private final AuthRepository authRepository;

//    private MutableLiveData<List<SearchModel>> allSearches;
    private LiveData<List<SearchModel>> allRoomSearches;
    private final SingleLiveEvent<String> toastMessage = new SingleLiveEvent<>();
    private final MutableLiveData<Integer> editMenuOpen = new MutableLiveData<>();
    private final SingleLiveEvent<String> startEditActivity = new SingleLiveEvent<>();
    private final SingleLiveEvent<Integer> confirmDeleteSearches = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> openSettingsScreen = new SingleLiveEvent<>();
    private final ArrayList<String> checkedItems = new ArrayList<>();

    private final MutableLiveData<Boolean> userSignedIn;
    private final MutableLiveData<Boolean> signUserOut;

    public SearchesViewModel(@NonNull Application application) {
        super(application);
        searchRepository = new SearchRepository(application);
        authRepository = new AuthRepository();
//        allSearches = searchRepository.getAllSearches();
        allRoomSearches = searchRepository.getAllRoomSearches();

        userSignedIn = authRepository.getUserSignedIn();
        signUserOut = authRepository.getSignUserOut();
    }

    public ArrayList<String> getCheckedItems() { return checkedItems; }

    public MutableLiveData<Boolean> getUserSignedIn() {
        return userSignedIn;
    }

//    public void setSearchesListener() {
//        searchRepository.setListeners();
//    }

//    public MutableLiveData<List<SearchModel>> getAllSearches() {
//        if (allSearches == null)
//            allSearches = searchRepository.getAllSearches();
//        return allSearches;
//    }

    public LiveData<List<SearchModel>> getAllRoomSearches() {
//        if (allRoomSearches == null)
//            allRoomSearches = searchRepository.getAllRoomSearches();
        return allRoomSearches;
    }

    // TODO - RESET LIST WHEN NAVIGATING AWAY FROM SCREEN
    public void updateCheckedSearchesList(String searchId, boolean isChecked) {

        if (isChecked)
            checkedItems.add(searchId);
        else
            checkedItems.remove(searchId);
    }

    public void handleOnOptionsItemSelected(int itemId) {

        if (itemId == R.id.action_edit)
            toggleEdit();
        else if (itemId == R.id.action_settings)
            openSettingsScreen.setValue(true);
        else if (itemId == R.id.action_search_edit)
            editSearch();
        else if (itemId == R.id.action_delete)
            handleDeleteSearches();
    }

    public void signUserOut() {
        authRepository.signUserOut();
    }

    public void toggleEdit() {

        if (editMenuOpen.getValue() != null && editMenuOpen.getValue() == View.VISIBLE)
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
        searchRepository.delete(checkedItems, this);
        checkedItems.clear();
        setToastMessage("Deleting search.");

    }

    public SingleLiveEvent<String> getToastMessage() {
        return toastMessage;
    }

    private void setToastMessage(String message) {
        toastMessage.setValue(message);
    }

    public MutableLiveData<Integer> getEditMenuOpen() {
        return editMenuOpen;
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

    public SingleLiveEvent<String> getStartEditActivity() {
        return startEditActivity;
    }

    public void setStartEditActivity(String searchId) {
        startEditActivity.setValue(searchId);
    }

    // TODO -- Do I need to call getAllSearches here?
    public void refreshSearches() {
//        searchRepository.getAllSearches();
        toggleEdit(); // hide edit menu
        checkedItems.clear(); // Reset checked items
    }

    public String getUserId() {
        return searchRepository.getUserId();
    }

    public MutableLiveData<Boolean> getSignUserOut() {
        return signUserOut;
    }

    public SingleLiveEvent<Boolean> getOpenSettingsScreen() { return openSettingsScreen; }

    public void saveState() {
        // Not implemented yet...
    }

    public void backupDataToFirebase() {
        searchRepository.backupDataToFirebase();
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

    @Override
    public void onComplete(Boolean success) {
        if (success)
            toggleEdit();
        else
            setToastMessage("Deletion error");
    }

}
