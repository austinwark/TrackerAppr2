package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sandboxcode.trackerappr2.repositories.SearchRepository;

public class CreateViewModel extends AndroidViewModel {

    @SuppressWarnings("unused")
    private static final String TAG = "CreateViewModel";
    private final SearchRepository repository;
    private MutableLiveData<String> toastMessage;
    private final MutableLiveData<Boolean> createCancelled;

    public CreateViewModel(Application application) {
        super(application);
        repository = new SearchRepository();
        createCancelled = new MutableLiveData<>();
    }

    public void create(String name, String model, String trim, String minYear, String maxYear,
                       String minPrice, String maxPrice, String allDealerships) {
        repository.create(name, model, trim, minYear, maxYear, minPrice, maxPrice, allDealerships);
    }

    public MutableLiveData<String> getToastMessage() {
        if (toastMessage == null) {
            toastMessage = new MutableLiveData<>();
        }
        return toastMessage;
    }

    @SuppressWarnings("unused")
    public void setToastMessage(String message) {
        toastMessage.postValue(message);
    }

    public MutableLiveData<Boolean> getCreateCancelled() {
        return createCancelled;
    }

    public void handleOnOptionsItemSelected(int itemId) {
        if (itemId == android.R.id.home)
            createCancelled.postValue(true);
    }
}
