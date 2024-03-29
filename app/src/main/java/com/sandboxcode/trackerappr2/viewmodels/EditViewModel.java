package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.repositories.SearchRepository;
import com.sandboxcode.trackerappr2.utils.SingleLiveEvent;

public class EditViewModel extends AndroidViewModel {

    private final SearchRepository repository;
    private String searchId;
    private final MutableLiveData<SearchModel> search;
    private LiveData<SearchModel> roomSearch;
    private SingleLiveEvent<String> toastMessage;
    private final MutableLiveData<Boolean> changesSaved;
    private final MutableLiveData<String> errorMessage;

    public EditViewModel(Application application) {
        super(application);
        repository = new SearchRepository(application);
        search = repository.getSingleSearch();
        changesSaved = repository.getChangesSaved();
        errorMessage = repository.getErrorMessage();
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    // TODO -- If null, perhaps close EditActivity?
    public LiveData<SearchModel> getRoomSearch() {
        roomSearch = repository.retrieveRoomSearch(searchId);
        return roomSearch;
    }

    public SingleLiveEvent<String> getToastMessage() {
        if (toastMessage == null)
            toastMessage = new SingleLiveEvent<>();
        return toastMessage;
    }

    @SuppressWarnings("unused")
    public void setToastMessage(String message) {
        toastMessage.setValue(message);
    }

//    public void saveChanges(String name, String model, String trim, String minYear, String maxYear,
//                            String minPrice, String maxPrice, String allDealerships,
//                            String createdDate) {
//
//        SearchModel searchModel = new SearchModel(searchId, name, model, trim, minYear,
//                maxYear, minPrice, maxPrice, allDealerships);
//        searchModel.setLastEditedDate();
//        searchModel.setCreatedDate(createdDate);
//        repository.saveChanges(searchModel);
//    }

    public void saveRoomChanges(String name, String model, String trim, String minYear, String maxYear,
                            String minPrice, String maxPrice, String allDealerships,
                            String createdDate) {

        String userId = repository.getUserId();
        SearchModel searchModel = new SearchModel(searchId, userId, name, model, trim, minYear,
                maxYear, minPrice, maxPrice, allDealerships);
        searchModel.setLastEditedDate();
        searchModel.setCreatedDate(createdDate);
        repository.saveRoomChanges(searchModel);
    }


    public MutableLiveData<Boolean> getChangesSaved() {
        return changesSaved;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
