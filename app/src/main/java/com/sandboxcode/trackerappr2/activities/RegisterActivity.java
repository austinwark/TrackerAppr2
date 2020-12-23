package com.sandboxcode.trackerappr2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.sandboxcode.trackerappr2.R;
import com.sandboxcode.trackerappr2.viewmodels.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
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
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        authViewModel.getToastMessage().observe(this, message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show());

        registerButton.setOnClickListener(view -> {
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            authViewModel.createUser(emailText, passwordText);
        });

        loginButton.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });

    }

    private void instantiateUI() {
        email = findViewById(R.id.et_register_email);
        password = findViewById(R.id.et_register_password);
        registerButton = findViewById(R.id.button_register_register);
        loginButton = findViewById(R.id.button_register_login);
    }
}