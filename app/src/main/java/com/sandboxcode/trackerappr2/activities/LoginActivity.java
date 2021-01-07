package com.sandboxcode.trackerappr2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.fragments.PasswordResetFragment;
import com.sandboxcode.trackerappr2.viewmodels.AuthViewModel;

public class LoginActivity extends AppCompatActivity implements PasswordResetFragment.PasswordResetDialogListener {

    private CoordinatorLayout coordinatorLayout;
    private TextInputLayout email;
    private TextInputLayout password;
    private Button loginButton;
    private Button registerButton;
    private Button forgotPasswordButton;
    private AuthViewModel authViewModel;
    private PasswordResetFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        instantiateUI();

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getUserSignedIn().observe(this, isUserSignedIn -> {
            if (Boolean.TRUE.equals(isUserSignedIn)) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        authViewModel.getFirebaseError().observe(this, firebaseError -> {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, firebaseError,
                    BaseTransientBottomBar.LENGTH_LONG);
            snackbar.show();
        });

        authViewModel.getToastMessage().observe(this, message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show());

        authViewModel.getPasswordResetErrorMessage().observe(this, message -> {
            if (dialog != null)
                dialog.setPasswordErrorText(message);
        });

        authViewModel.getPasswordResetSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success) && dialog != null) {
                dialog.dismiss();
                Toast.makeText(this, "Password reset link sent to email.",
                        Toast.LENGTH_LONG).show();
            }
        });

        loginButton.setOnClickListener(v -> {
            if (email.getEditText() != null && password.getEditText() != null) {

                String emailText = email.getEditText().getText().toString();
                String passwordText = password.getEditText().getText().toString();
                authViewModel.loginUser(emailText, passwordText);
            }
        });

        registerButton.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        });

        forgotPasswordButton.setOnClickListener(view -> {
            dialog = new PasswordResetFragment();
            dialog.show(getSupportFragmentManager(), "PasswordResetFragment");
        });

    }

    @Override
    public void resetPassword(String email) {
        authViewModel.resetPassword(email);
    }

    public void instantiateUI() {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        coordinatorLayout = findViewById(R.id.login_coordinator_layout);
        email = findViewById(R.id.login_edit_email);
        password = findViewById(R.id.login_edit_pass);
        loginButton = findViewById(R.id.login_button_login);
        registerButton = findViewById(R.id.login_button_register);
        forgotPasswordButton = findViewById(R.id.login_button_forgot);
    }
}