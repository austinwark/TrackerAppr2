package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.text.TextUtils;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

public class LoginViewModel extends AndroidViewModel {

    private FirebaseAuth firebaseAuth;
    private MutableLiveData<String> toastMessage;
    private MutableLiveData<Boolean> userSignedIn;

    public LoginViewModel(Application application) {
        super(application);
        firebaseAuth = FirebaseAuth.getInstance();
        toastMessage = new MutableLiveData<>();
        userSignedIn = new MutableLiveData<>();
        if (isUserSignedIn())
            userSignedIn.postValue(true);
    }

    public boolean isUserSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public MutableLiveData<Boolean> getUserSignedIn() {
        return userSignedIn;
    }

    public void loginUser(String email, String password) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            toastMessage.postValue("All required fields must be filled in.");

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful())
                userSignedIn.postValue(true);
            else
                toastMessage.postValue("Email or password is incorrect.");
        });
    }
}
