package com.sandboxcode.trackerappr2.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sandboxcode.trackerappr2.models.SettingsModel;

public class AuthRepository {

    private static final FirebaseAuth AUTH_REF = FirebaseAuth.getInstance();
    private static final DatabaseReference DATABASE_REF =
            FirebaseDatabase.getInstance().getReference();
    private final MutableLiveData<Boolean> userSignedIn = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUserOut = new MutableLiveData<>();

    public AuthRepository() {
    }

    public String getUserId() {
        if (AUTH_REF.getCurrentUser() != null)
            return AUTH_REF.getCurrentUser().getUid();
        else
            return null;
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
