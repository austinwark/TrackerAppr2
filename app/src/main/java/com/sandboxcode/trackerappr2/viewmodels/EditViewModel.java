package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.repositories.SearchRepository;

public class EditViewModel extends AndroidViewModel {

    private SearchRepository repository;
    private String searchId;
    private MutableLiveData<SearchModel> search;
    private MutableLiveData<String> toastMessage;
    private MutableLiveData<Boolean> changesSaved;
    private MutableLiveData<String> errorMessage;

    public EditViewModel(Application application) {
        super(application);
        repository = new SearchRepository();
        search = repository.getSingleSearch();
        changesSaved = repository.getChangesSaved();
        errorMessage = repository.getErrorMessage();
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public MutableLiveData<SearchModel> getSearch() {
        repository.retrieveSearch(searchId);
        return search;
    }

    public MutableLiveData<String> getToastMessage() {
        if (toastMessage == null)
            toastMessage = new MutableLiveData<>();
        return toastMessage;
    }

    public void setToastMessage(String message) {
        toastMessage.postValue(message);
    }

    public void saveChanges(String name, String model, String trim, String minYear, String maxYear,
                            String minPrice, String maxPrice, String allDealerships) {

        SearchModel searchModel = new SearchModel(searchId, name, model, trim, minYear,
                maxYear, minPrice, maxPrice, allDealerships);

        repository.saveChanges(searchModel);
    }

    public MutableLiveData<Boolean> getChangesSaved() {
        return changesSaved;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }
}
