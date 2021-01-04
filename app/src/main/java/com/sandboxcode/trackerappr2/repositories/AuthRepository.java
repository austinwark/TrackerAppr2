package com.sandboxcode.trackerappr2.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

public class AuthRepository {

    private static final FirebaseAuth AUTH_REF = FirebaseAuth.getInstance();
    private MutableLiveData<Boolean> userSignedIn = new MutableLiveData<>();

    public AuthRepository() {
        this.userSignedIn.postValue(isUserSignedIn());
    }

    public boolean isUserSignedIn() {
        return AUTH_REF.getCurrentUser() != null;
    }

    public MutableLiveData<Boolean> getUserSignedIn() {
        return userSignedIn;
    }

    public void setUserSignedIn() {
        this.userSignedIn.postValue(isUserSignedIn());
    }

    public void resetPassword() {

    }
}
