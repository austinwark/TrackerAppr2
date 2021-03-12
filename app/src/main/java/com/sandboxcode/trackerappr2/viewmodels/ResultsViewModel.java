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
    private final SingleLiveEvent<Integer> sortMenuOpen = new SingleLiveEvent<>();
    private final SingleLiveEvent<Boolean> sortCompleted = new SingleLiveEvent<>();
    private final SingleLiveEvent<Integer> openShareConfirmation = new SingleLiveEvent<>();
    private final MutableLiveData<Integer> editMenuVisibility = new MutableLiveData<>();
    private final SingleLiveEvent<List<ResultModel>> viewDetails = new SingleLiveEvent<>();

    private final ArrayList<ResultModel> checkedResults;


    public ResultsViewModel(@NonNull Application application) {
        super(application);
        searchRepository = new SearchRepository(application);
        checkedResults = new ArrayList<>();
        editMenuVisibility.postValue(View.INVISIBLE);
    }

    public void handleOnOptionsItemSelected(int itemId) {
        if (itemId == R.id.results_action_sort)
            toggleSortMenu();
        else if (itemId == R.id.results_action_share) {
            openShareConfirmation.setValue(checkedResults.size());
        } else if (itemId == R.id.results_action_edit) {
            toggleEdit();
        } else if (itemId ==  R.id.results_action_view_details) {
            startViewDetails();
        }
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "ONCLEARED======");
        super.onCleared();
    }

    // TODO -- Call SearchResults every time? OR only when null and nothing has changed?
    public MutableLiveData<ArrayList<ResultModel>> getSearchResults(String searchId) {
        if (searchResults == null || searchResults.getValue().isEmpty())
            Log.d(TAG, "NULL ------------");

        searchResults = searchRepository.getSearchResults(searchId);
        return searchResults;
    }

    public void setEditMenuVisibility(int visibility) {
        editMenuVisibility.postValue(visibility);
    }

    public MutableLiveData<Integer> getEditMenuVisibility() {
        return editMenuVisibility;
    }

    public void toggleEdit() {
        if (editMenuVisibility.getValue() != null && editMenuVisibility.getValue() == View.VISIBLE) {
            editMenuVisibility.postValue(View.INVISIBLE);
            clearCheckedResults();
        } else {
            editMenuVisibility.postValue(View.VISIBLE);
        }
    }

    public int addCheckedResult(ResultModel... results) {
        Collections.addAll(checkedResults, results);
        return checkedResults.size();
    }

    public int removeCheckedResult(ResultModel result) {
        checkedResults.remove(result);
        return checkedResults.size();
    }

    public void clearCheckedResults() {

        checkedResults.clear();
        editMenuVisibility.setValue(View.INVISIBLE);
    }

    public void restoreCheckedCardStates(ArrayList<ResultModel> results) {
        for (ResultModel checkedResult : checkedResults)
            for (ResultModel result : results)
                if (result.getDetailsLink().equalsIgnoreCase(checkedResult.getDetailsLink()))
                    result.setIsChecked(true);
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

    public List<ResultModel> getCheckedResults() { return checkedResults; }

    public SingleLiveEvent<Integer> getOpenShareConfirmation() { return openShareConfirmation; }

    public String buildShareConfirmationMessage() {
        int numberOfResults = checkedResults.size();
        return numberOfResults > 1
                ? "Share " + numberOfResults + " results?"
                : "Share 1 result?";
    }
    public String buildShareBodyText(List<String> resultLinks) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Check out these pre-owned Toyotas I found for you!" +
                " Click the links below to view each vehicle:<br /><br />");

        for (String link : resultLinks) {
            String linkString = String.format("<a href=\"%s\">%s</a><br /><br />", link, link);
            stringBuilder.append(linkString);
        }
        return stringBuilder.toString();
    }

    public SingleLiveEvent<List<ResultModel>> getViewDetails() { return viewDetails; }

    public void startViewDetails() {
        if (!checkedResults.isEmpty()) {
            List<ResultModel> deepCopiedResults = new ArrayList<>();
            for (ResultModel result : checkedResults)
                deepCopiedResults.add((ResultModel) ResultModel.deepCopy(result));
            viewDetails.setValue(deepCopiedResults);
        } else {

            // TODO -- show banner
        }
    }
}
