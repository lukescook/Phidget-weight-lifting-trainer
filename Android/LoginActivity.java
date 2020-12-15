package com.example.hardwareanddevicesdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivitiy" ;
    FirebaseAuth userAuth;
    Button registerBtn;
    EditText emailInput;
    EditText passwordInput;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        db = FirebaseFirestore.getInstance();
        registerBtn = findViewById(R.id.registerBtn);
        emailInput = findViewById(R.id.emailBox);
        passwordInput = findViewById(R.id.passwordBox);
        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();

        if(currentUser != null){
            changeToHomeScreen();
        }


    }

    public void loginBtnClick(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Empty fields not allowed",
                    Toast.LENGTH_SHORT).show();
        }else{
            login(email, password);
        }

    }

    public void login(String email, String password) {
        userAuth.signOut();
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();

        userAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Success",
                                    Toast.LENGTH_LONG).show();
                            currentUser = userAuth.getCurrentUser();
                            if (currentUser != null) {
                                Toast.makeText(LoginActivity.this, "Logged in: " +
                                        currentUser.getEmail(), Toast.LENGTH_LONG).show();
                                changeToHomeScreen();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void changeToRegister(View view){
        Intent register = new Intent(this, RegisterActivity.class);
        startActivity(register);
    }

    public void changeToHomeScreen() {
        Intent home = new Intent(this, WorkoutSelectActivity.class);
        startActivity(home);
    }
}
