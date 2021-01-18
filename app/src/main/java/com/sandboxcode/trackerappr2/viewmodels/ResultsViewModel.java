package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.ResultsFragment;
import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.repositories.AuthRepository;
import com.sandboxcode.trackerappr2.repositories.SearchRepository;
import com.sandboxcode.trackerappr2.utils.SingleLiveEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ResultsViewModel extends AndroidViewModel {

    private static final String TAG = ResultsViewModel.class.getSimpleName();
    private final SearchRepository searchRepository;

    private SingleLiveEvent<ArrayList<ResultModel>> searchResults;
    private SingleLiveEvent<Integer> sortMenuOpen = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> sortCompleted = new SingleLiveEvent<>();
    private SingleLiveEvent<Integer> openShareConfirmation = new SingleLiveEvent<>();

    private ArrayList<String> checkedResults;


    public ResultsViewModel(@NonNull Application application) {
        super(application);
        searchRepository = new SearchRepository();
        checkedResults = new ArrayList<>();
    }

    public void handleOnOptionsItemSelected(int itemId) {
        if (itemId == R.id.results_action_sort)
            toggleSortMenu();
        else if (itemId == R.id.results_action_share)
            openShareConfirmation.setValue(checkedResults.size());
    }

    // TODO -- Call SearchResults every time? OR only when null and nothing has changed?
    public MutableLiveData<ArrayList<ResultModel>> getSearchResults(String searchId) {
        if (searchResults == null || searchResults.getValue().isEmpty())
            Log.d(TAG, "NULL ------------");

        searchResults = searchRepository.getSearchResults(searchId);
        return searchResults;
    }

    public SingleLiveEvent<Boolean> getSortCompleted() { return sortCompleted; }

    public SingleLiveEvent<Integer> getSortMenuOpen() { return sortMenuOpen; }

    public void toggleSortMenu() {
        if (sortMenuOpen.getValue() != null && sortMenuOpen.getValue() == View.VISIBLE)
            sortMenuOpen.setValue(View.GONE);
        else
            sortMenuOpen.setValue(View.VISIBLE);
    }

    public void handleSortSelection(ResultsFragment.SortOption sortOption) {
        List<ResultModel> currentSearchResults = searchResults.getValue();
        if (currentSearchResults == null)
            return;

        switch (sortOption) {
            case PRICE_ASC:
                Collections.sort(currentSearchResults, (o1, o2) ->
                        Integer.compare(Integer.parseInt(o1.getPrice()),
                                Integer.parseInt(o2.getPrice())));
                break;
            case PRICE_DESC:
                Collections.sort(currentSearchResults, (o1, o2) ->
                        Integer.compare(Integer.parseInt(o2.getPrice()),
                                Integer.parseInt(o1.getPrice())));
                break;
            case YEAR_ASC:
                Collections.sort(currentSearchResults, (o1, o2) ->
                        Integer.compare(Integer.parseInt(o1.getYear()),
                                Integer.parseInt(o2.getYear())));
                break;
            case YEAR_DESC:
                Collections.sort(currentSearchResults, (o1, o2) ->
                        Integer.compare(Integer.parseInt(o2.getYear()),
                                Integer.parseInt(o1.getYear())));
                break;
        }
        sortCompleted.setValue(true);
    }

    public void clearCheckedResults() {
        checkedResults.clear();
    }

    public int removeCheckedResult(String link) {
        checkedResults.remove(link);
        return checkedResults.size();
    }

    public int addCheckedResult(String... links) {
        Collections.addAll(checkedResults, links);
        return checkedResults.size();
    }

    public List<String> getCheckedResults() { return checkedResults; }

    public SingleLiveEvent<Integer> getOpenShareConfirmation() { return openShareConfirmation; }
}
