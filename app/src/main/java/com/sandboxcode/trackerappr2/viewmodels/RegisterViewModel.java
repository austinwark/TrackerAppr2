package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.text.TextUtils;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterViewModel extends AndroidViewModel {

    private FirebaseAuth firebaseAuth;
    private MutableLiveData<String> toastMessage;
    private MutableLiveData<Boolean> userSignedIn;

    public RegisterViewModel(Application application) {
        super(application);
        firebaseAuth = FirebaseAuth.getInstance();
        toastMessage = new MutableLiveData<>();
        userSignedIn = new MutableLiveData<>();
        if (isUserSignedIn())
            userSignedIn.postValue(true);
    }

    private boolean isUserSignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public MutableLiveData<Boolean> getUserSignedIn() {
        return userSignedIn;
    }

    public void createUser(String email, String password) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            toastMessage.postValue("Please fill in the required fields.");
        else if (password.length() < 6)
            toastMessage.postValue("Password must be at least six characters.");
        else {

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful())
                            userSignedIn.postValue(true);
                        else
                            toastMessage.postValue("Error creating user. Please try again.");
                    });
        }
    }
}
