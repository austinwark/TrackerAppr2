package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.text.TextUtils;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.sandboxcode.trackerappr2.repositories.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private AuthRepository authRepository;
    private FirebaseAuth firebaseAuth;
    private MutableLiveData<String> toastMessage;
    private MutableLiveData<Boolean> userSignedIn;

    public AuthViewModel(Application application) {
        super(application);
        authRepository = new AuthRepository();
        firebaseAuth = FirebaseAuth.getInstance();
        toastMessage = new MutableLiveData<>();
    }

    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public MutableLiveData<Boolean> getUserSignedIn() {
        if (userSignedIn == null)
            userSignedIn = authRepository.getUserSignedIn();
        return userSignedIn;
    }

    public void loginUser(String email, String password) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
            toastMessage.postValue("All required fields must be filled in.");
        else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                if (task.isSuccessful())
                    authRepository.setUserSignedIn();
                else
                    toastMessage.postValue("Email or password is incorrect.");
            });
        }

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
                            authRepository.setUserSignedIn();
                        else
                            toastMessage.postValue("Error creating user. Please try again.");
                    });
        }
    }
}
