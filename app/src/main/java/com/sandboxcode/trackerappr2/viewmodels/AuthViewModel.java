package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.sandboxcode.trackerappr2.repositories.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<String> toastMessage;
    private MutableLiveData<Boolean> userSignedIn;
    private final MutableLiveData<String> passwordResetErrorMessage;
    private final MutableLiveData<Boolean> passwordResetSuccess;

    // TODO -- Add AuthStateListener
    public AuthViewModel(Application application) {
        super(application);
        authRepository = new AuthRepository();
        firebaseAuth = FirebaseAuth.getInstance();
        toastMessage = new MutableLiveData<>();
        passwordResetErrorMessage = new MutableLiveData<>();
        passwordResetSuccess = new MutableLiveData<>();
    }

    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public MutableLiveData<Boolean> getUserSignedIn() {
        if (userSignedIn == null)
            userSignedIn = authRepository.getUserSignedIn();
        return userSignedIn;
    }

    public MutableLiveData<String> getPasswordResetErrorMessage() {
        return passwordResetErrorMessage;
    }

    public MutableLiveData<Boolean> getPasswordResetSuccess() {
        return passwordResetSuccess;
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
            toastMessage.postValue("Please fill in all fields.");
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            toastMessage.postValue("Email address must be in a correct format.");
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

    public void resetPassword(String email) {

        if (TextUtils.isEmpty(email))
            passwordResetErrorMessage.postValue("Email address is required.");
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            passwordResetErrorMessage.postValue("Email address must be in a correct format.");
        else {
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {

                if (task.isSuccessful())
                    passwordResetSuccess.postValue(true);
                else
                    passwordResetErrorMessage.postValue("Error sending password link to " + email + ".");
            });
        }
    }
}
