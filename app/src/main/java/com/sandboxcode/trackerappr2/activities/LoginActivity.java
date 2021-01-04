package com.sandboxcode.trackerappr2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.viewmodels.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button loginButton;
    private Button registerButton;
    private Button forgotPasswordButton;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        instantiateUI();

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        authViewModel.getUserSignedIn().observe(this, isUserSignedIn -> {
            if (isUserSignedIn) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

        authViewModel.getToastMessage().observe(this, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show());

        loginButton.setOnClickListener(v -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            Log.d("LoginActivity", emailText + " " + passwordText);
            authViewModel.loginUser(emailText, passwordText);
        });

        registerButton.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            finish();
        });

        forgotPasswordButton.setOnClickListener(view -> {

        });

    }

    public void instantiateUI() {
        email = findViewById(R.id.et_login_email);
        password = findViewById(R.id.et_login_password);
        loginButton = findViewById(R.id.button_login_login);
        forgotPasswordButton = findViewById(R.id.button_login_register);
        forgotPasswordButton = findViewById(R.id.button_login_forgot);
    }
}