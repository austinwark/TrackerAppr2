package com.sandboxcode.trackerappr2.viewmodels;

import android.app.Application;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.sandboxcode.trackerappr2.repositories.AuthRepository;



public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<String> toastMessage;
    private final MutableLiveData<ValidationError> emailError;
    private final MutableLiveData<ValidationError> passError;
    private final MutableLiveData<ValidationError> passConfirmError;
    private final MutableLiveData<String> firebaseError;
    private final MutableLiveData<String> passwordResetErrorMessage;
    private final MutableLiveData<Boolean> passwordResetSuccess;
    private MutableLiveData<Boolean> userSignedIn;

    // TODO -- Add AuthStateListener
    public AuthViewModel(Application application) {
        super(application);
        authRepository = new AuthRepository();
        firebaseAuth = FirebaseAuth.getInstance();

        toastMessage = new MutableLiveData<>();
        emailError = new MutableLiveData<>();
        passError = new MutableLiveData<>();
        passConfirmError = new MutableLiveData<>();
        firebaseError = new MutableLiveData<>();

        passwordResetErrorMessage = new MutableLiveData<>();
        passwordResetSuccess = new MutableLiveData<>();
    }

    public enum ValidationError {
        EMAIL_REQUIRED,
        EMAIL_NOT_VALID,
        PASSWORDS_MUST_MATCH,
        PASS_REQUIRED,
        PASS_MIN,
        PASS_MAX,
        PASS_CONFIRM_REQUIRED,
        PASS_CONFIRM_MIN,
        PASS_CONFIRM_MAX,
        NO_ERROR;
    }

    public MutableLiveData<String> getToastMessage() {
        return toastMessage;
    }

    public MutableLiveData<ValidationError> getEmailError() {
        return emailError;
    }

    public MutableLiveData<ValidationError> getPassError() {
        return passError;
    }

    public MutableLiveData<ValidationError> getPassConfirmError() {
        return passConfirmError;
    }

    public MutableLiveData<String> getFirebaseError() {
        return firebaseError;
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
            firebaseError.postValue("All required fields must be filled in.");
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            firebaseError.postValue("No matching account found");
        else {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> authRepository.setUserSignedIn())
                    .addOnFailureListener(e -> {
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            String errorCode =
                                    ((FirebaseAuthInvalidUserException) e).getErrorCode();

                            if (errorCode.equals("ERROR_USER_NOT_FOUND"))
                                firebaseError.postValue("No matching account found");
                            else if (errorCode.equals("ERROR_USER_DISABLED"))
                                firebaseError.postValue("User account has been disabled");
                        }
                        else if (e instanceof FirebaseAuthInvalidCredentialsException)
                            firebaseError.postValue("Invalid password");
                        else
                            firebaseError.postValue(e.getLocalizedMessage());
                    });
        }
    }


    public void createUser(String email, String password, String passwordConfirm) {

        boolean emailErr = validatePasswordConfirm(password, passwordConfirm);
        boolean passwordErr = validateEmail(email);
        boolean passwordConfirmErr = validatePassword(password, passwordConfirm);

        if (!emailErr && !passwordErr && !passwordConfirmErr) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> authRepository.setUserSignedIn())
                    .addOnFailureListener(e -> {
                        if (e instanceof FirebaseAuthInvalidCredentialsException)
                            firebaseError.postValue("Invalid password");
                        else if (e instanceof FirebaseAuthInvalidUserException) {
                            String errorCode =
                                    ((FirebaseAuthInvalidUserException) e).getErrorCode();

                            if (errorCode.equals("ERROR_USER_NOT_FOUND"))
                                firebaseError.postValue("No matching account found");
                            else if (errorCode.equals("ERROR_USER_DISABLED"))
                                firebaseError.postValue("User account has been disabled");
                            else
                                firebaseError.postValue(e.getLocalizedMessage());
                        }
                    });
        }
    }

    private boolean validateEmail(String email) {

        if (TextUtils.isEmpty(email)) {
            emailError.setValue(ValidationError.EMAIL_REQUIRED);
            return true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError.setValue(ValidationError.EMAIL_NOT_VALID);
            return true;
        } else
            emailError.setValue(ValidationError.NO_ERROR);

        return false;
    }

    private boolean validatePassword(String password, String passwordConfirm) {
        if (TextUtils.isEmpty(password)) {
            passError.setValue(ValidationError.PASS_REQUIRED);
            return true;
        } else if (!TextUtils.equals(password, passwordConfirm)) {
            passError.setValue(ValidationError.PASSWORDS_MUST_MATCH);
            return true;
        } else if (password.length() < 6) {
            passError.setValue(ValidationError.PASS_MIN);
            return true;
        } else if (password.length() > 128) {
            passError.setValue(ValidationError.PASS_MAX);
            return true;
        } else
            passError.setValue(ValidationError.NO_ERROR);

        return false;
    }

    private boolean validatePasswordConfirm(String password, String passwordConfirm) {

        if (TextUtils.isEmpty(passwordConfirm)) {
            passConfirmError.setValue(ValidationError.PASS_CONFIRM_REQUIRED);
            return true;
        } else if (!TextUtils.equals(password, passwordConfirm)) {
            passConfirmError.setValue(ValidationError.PASSWORDS_MUST_MATCH);
            return true;
        } else if (passwordConfirm.length() < 6) {
            passConfirmError.setValue(ValidationError.PASS_CONFIRM_MIN);
            return true;
        } else if (passwordConfirm.length() > 128) {
            passConfirmError.setValue(ValidationError.PASS_CONFIRM_MAX);
            return true;
        } else {
            passConfirmError.setValue(ValidationError.NO_ERROR);
        }

        return false;
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
