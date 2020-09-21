package com.sandboxcode.trackerappr2;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmail, loginPassword;
    private Button loginButton, registerButton, newPassButton;
    private static final int RC_SIGN_IN = 9001;
    private SignInButton signInButton;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = (EditText) findViewById(R.id.et_login_email);
        loginPassword = (EditText) findViewById(R.id.et_login_password);
        loginButton = (Button) findViewById(R.id.button_login_login);
        registerButton = (Button) findViewById(R.id.button_login_register);
        newPassButton = (Button) findViewById(R.id.button_login_forgot);

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String emailText = loginEmail.getText().toString();
                String passwordText = loginPassword.getText().toString();

                if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)) {
                    Toast.makeText(getApplicationContext(),
                            "Please fill in the required fields",
                            Toast.LENGTH_SHORT).show();
                }

                firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
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

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });

        if (firebaseAuth.getCurrentUser() != null)
            startActivity(new Intent(getApplicationContext(), MainActivity.class));


    }
}