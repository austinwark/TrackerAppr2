package com.sandboxcode.trackerappr2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.viewmodels.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private CoordinatorLayout coordinatorLayout;
    private TextInputLayout email;
    private TextInputLayout password;
    private TextInputLayout passwordConfirm;
    private Button registerButton;
    private Button loginButton;
    private AuthViewModel authViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        instantiateUI();

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getUserSignedIn().observe(this, isUserSignedIn -> {
            if (Boolean.TRUE.equals(isUserSignedIn)) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        authViewModel.getEmailError().observe(this, emailError -> {
            switch (emailError) {
                case EMAIL_REQUIRED:
                    email.setError("This is a required field");
                    break;
                case EMAIL_NOT_VALID:
                    email.setError("Must be in a valid format");
                    break;
                case NO_ERROR:
                    email.setError(null);
                    break;
            }
        });
        authViewModel.getPassError().observe(this, passError -> {
            switch (passError) {
                case PASSWORDS_MUST_MATCH:
                    password.setError("Passwords must match");
                    break;
                case PASS_REQUIRED:
                    password.setError("This is a required field");
                    break;
                case PASS_MIN:
                    password.setError("Must be at least 6 characters");
                    password.setCounterEnabled(true);
                    break;
                case PASS_MAX:
                    password.setError(" Can not be more than 128 characters");
                    password.setCounterEnabled(true);
                    break;
                case NO_ERROR:
                    password.setError(null);
                    password.setCounterEnabled(false);
                    break;
            }
        });
        authViewModel.getPassConfirmError().observe(this, passConfirmError -> {
            switch (passConfirmError) {
                case PASSWORDS_MUST_MATCH:
                    passwordConfirm.setError("Passwords must match");
                    break;
                case PASS_CONFIRM_REQUIRED:
                    passwordConfirm.setError("This is a required field");
                    break;
                case PASS_CONFIRM_MIN:
                    passwordConfirm.setError("Must be at least 6 characters");
                    passwordConfirm.setCounterEnabled(true);
                    break;
                case PASS_CONFIRM_MAX:
                    passwordConfirm.setError(" Can not be more than 128 characters");
                    passwordConfirm.setCounterEnabled(true);
                    break;
                case NO_ERROR:
                    passwordConfirm.setError(null);
                    passwordConfirm.setCounterEnabled(false);
                    break;
            }
        });

        authViewModel.getFirebaseError().observe(this, firebaseError -> {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, firebaseError,
                    BaseTransientBottomBar.LENGTH_LONG);
            snackbar.show();
        });

        authViewModel.getToastMessage().observe(this, message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show());

        registerButton.setOnClickListener(view -> {
            if (email.getEditText() != null
                    && password.getEditText() != null
                    && passwordConfirm.getEditText() != null) {

                String emailText = email.getEditText().getText().toString();
                String passwordText = password.getEditText().getText().toString();
                String passwordConfirmText = passwordConfirm.getEditText().getText().toString();

                authViewModel.createUser(emailText, passwordText, passwordConfirmText);
            }
        });

        loginButton.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

    }

    private void instantiateUI() {
        coordinatorLayout = findViewById(R.id.register_coordinator_layout);
        email = findViewById(R.id.register_edit_email);
        password = findViewById(R.id.register_edit_pass);
        passwordConfirm = findViewById(R.id.register_edit_pass_confirm);
        registerButton = findViewById(R.id.register_button_register);
        loginButton = findViewById(R.id.register_button_login);
    }
}