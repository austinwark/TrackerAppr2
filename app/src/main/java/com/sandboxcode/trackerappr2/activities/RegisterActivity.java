package com.sandboxcode.trackerappr2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sandboxcode.trackerappr2.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText email, password;
    private Button registerButton, loginButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = (EditText) findViewById(R.id.et_register_email);
        password = (EditText) findViewById(R.id.et_register_password);
        registerButton = (Button) findViewById(R.id.button_register_register);
        loginButton = (Button) findViewById(R.id.button_register_login);

        firebaseAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();

                if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)) {
                    Toast.makeText(getApplicationContext(),
                            "Please fill in the required fields",
                            Toast.LENGTH_SHORT).show();
                } else if (passwordText.length() < 6) {
                    Toast.makeText(getApplicationContext(),
                            "Password must be at least six characters",
                            Toast.LENGTH_SHORT).show();
                }

                firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getApplicationContext(),
                                            MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            "Email or password is incorrect",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        if (firebaseAuth.getCurrentUser() != null)
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}