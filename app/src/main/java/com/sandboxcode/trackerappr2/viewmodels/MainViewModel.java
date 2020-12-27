package com.sandboxcode.trackerappr2.viewmodels;
//

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

//
public class MainViewModel extends AndroidViewModel {
    //
//    public enum MainActivityActions {
//        SETTINGS(LoginActivity.class),
//        LOGOUT(LoginActivity.class);
//
//        private final Class route;
//
//        MainActivityActions(Class route) {
//            this.route = route;
//        }
//    }
//
//    private static final String TAG = "MainViewModel";
//    private MutableLiveData<MainActivityActions> action = new MutableLiveData<>();
//
    public MainViewModel(Application application) {
        super(application);
    }
//
//    public MutableLiveData<MainActivityActions> getAction() {
//        return action;
//    }
//
//    public void onOptionsItemSelected(int id) {
//
//        if (id == R.id.action_settings) {
//            Log.d(TAG, "Action Settings");
//
//        } else if (id == R.id.action_logout) {
//            action.setValue(MainActivityActions.LOGOUT);
//
//        }
//    }
//
}
