package com.sandboxcode.trackerappr2.repositories;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

public class AuthRepository {

    private static final FirebaseAuth AUTH_REF = FirebaseAuth.getInstance();
    private final MutableLiveData<Boolean> userSignedIn = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUserOut = new MutableLiveData<>();

    public AuthRepository() {
    }

    public boolean isUserSignedIn() {
        return AUTH_REF.getCurrentUser() != null;
    }

    public MutableLiveData<Boolean> getUserSignedIn() {
        if (isUserSignedIn())
            userSignedIn.postValue(true);
        else
            signUserOut();
        return userSignedIn;
    }

    public void setUserSignedIn() {
        this.userSignedIn.setValue(isUserSignedIn());
    }

    public void signUserOut() {
        AUTH_REF.signOut();
        signUserOut.setValue(true);
    }

    public MutableLiveData<Boolean> getSignUserOut() {
        return signUserOut;
    }
}
