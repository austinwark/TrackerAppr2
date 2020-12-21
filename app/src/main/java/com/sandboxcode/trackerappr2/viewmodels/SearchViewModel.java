package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.models.SearchModel;
import com.sandboxcode.trackerappr2.repositories.SearchRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends AndroidViewModel {

    private static final String TAG = "SearchViewModel";
    private SearchRepository repository;

    private MutableLiveData<List<SearchModel>> allSearches;
    private MutableLiveData<ArrayList<String>> checkedItems = new MutableLiveData<>();
    private MutableLiveData<EditMenuToggle> editMenuOpen = new MutableLiveData<>();

    public SearchViewModel(Application application) {
        super(application);
        repository = new SearchRepository();
        allSearches = repository.getAllSearches();
        checkedItems.postValue(new ArrayList<>());
        editMenuOpen.postValue(new EditMenuToggle(View.INVISIBLE, false));
    }

    public MutableLiveData<List<SearchModel>> getAllSearches() {
        if (allSearches == null)
            allSearches = repository.getAllSearches();
        return allSearches;
    }

    // TODO - RESET LIST WHEN NAVIGATING AWAY FROM SCREEN
    public void updateCheckedSearchesList(String searchId, boolean isChecked) {
        ArrayList<String> localCheckedItems = checkedItems.getValue();

        if (isChecked)
            localCheckedItems.add(searchId);
        else
            localCheckedItems.remove(searchId);

        checkedItems.postValue(localCheckedItems);
    }

    public String deleteSearch() {
        ArrayList<String> localCheckedItems = checkedItems.getValue();
        String searchId;

        if (localCheckedItems.isEmpty())
            return "A search must be selected before deletion";
        else if (localCheckedItems.size() > 1)
            return "Only one search can be deleted at a time";
        else {
            searchId = localCheckedItems.get(0);

            repository.delete(searchId, onDeleteListener);
            return "Deleting item";
        }
    }

    private OnCompleteListener<Void> onDeleteListener = new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            List<SearchModel> localAllSearches = allSearches.getValue();
            ArrayList<String> localCheckedItems = checkedItems.getValue();
            String searchId = localCheckedItems.get(0);

            if (task.isSuccessful()) {
                Log.d(TAG, "Delete Result: SUCCESS");
                int searchIndexToDelete = -1;
                // Confirm the search is deleted from list
                for (int itemIndex = 0; itemIndex < localAllSearches.size(); itemIndex++) {
                    if (localAllSearches.get(itemIndex).getId().equals(searchId)) {
                        searchIndexToDelete = itemIndex;
                    }
                }
                if (searchIndexToDelete == -1) {
                    localCheckedItems.remove(0);
                    checkedItems.postValue(localCheckedItems);
                }
            } else
                Log.d(TAG, "Delete Result: FAILURE");
        }
    };

    // TODO - CREATE
    public void create(String name, String model, String trim, String year, String minPrice, String maxPrice) {
        repository.create(name, model, trim, year, minPrice, maxPrice);
    }

    public void handleOnOptionsItemSelected(int itemId) {

        switch (itemId) {
            case R.id.action_edit:
                Log.D(TAG, "action edit");
                toggleEdit(true);
                break;
            case R.id.action_delete:
                Log.d(TAG, "action delete");
                deleteSearch();
                break;
            default:
                Log.d(TAG, "default");
                break;
        }
    }

    public void toggleEdit(boolean newState) {
        if (newState)
            editMenuOpen.postValue(new EditMenuToggle(View.VISIBLE, true));
        else
            editMenuOpen.postValue(new EditMenuToggle(View.INVISIBLE, false));
    }

    public MutableLiveData<EditMenuToggle> getEditMenuOpen() {
        return editMenuOpen;
    }

    /* Static class used to wrap data for toggling the bottom menu */
    public static class EditMenuToggle {
        private int visible;
        private boolean checkboxVisible;

        public EditMenuToggle(int v, boolean c) {
            visible = v;
            checkboxVisible = c;
        }

        public int getVisible() {
            return visible;
        }

        public boolean getCheckboxVisible() {
            return checkboxVisible;
        }
    }
}
