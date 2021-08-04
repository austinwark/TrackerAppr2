package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.repositories.SearchRepository;

public class DetailViewModel extends AndroidViewModel {

    private static final String TAG = DetailViewModel.class.getSimpleName();
    private final SearchRepository searchRepository;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        searchRepository = new SearchRepository(application);

    }

    public void setResultHasBeenViewed(String vin, String searchId) {
        ResultModel viewedResult = searchRepository.getSingleSearchResult(vin);
        if (viewedResult != null && viewedResult.getIsNewResult())
            searchRepository.setResultHasBeenViewed(vin, searchId);
    }
}
