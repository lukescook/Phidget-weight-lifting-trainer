package com.example.hardwareanddevicesdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth userAuth;
    FirebaseFirestore db;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText confirmPasswordInput;
    private String email;
    private String password;
    private String confirmPassword;

    @Override
    public void onStart(){
        super.onStart();

        FirebaseUser currentUser = userAuth.getCurrentUser();
        if(currentUser != null) {
            System.out.println("LOGGED IN => " + currentUser.getEmail());
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = findViewById(R.id.registerEmail);
        passwordInput = findViewById(R.id.registerPassword);
        confirmPasswordInput = findViewById(R.id.reenterPassword);
        userAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }

    public void registerUser(View view){
        email = String.valueOf(emailInput.getText());
        password = String.valueOf(passwordInput.getText());
        confirmPassword = String.valueOf(confirmPasswordInput.getText());

        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();

        if(password.matches(confirmPassword)) {
            userAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Success",
                                        Toast.LENGTH_LONG).show();
                                addUserToDB();
                                changeToHomeScreen();

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, "FAILED: " + e.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });

        } else{
            Toast.makeText(RegisterActivity.this, "Passwords do not match",
                    Toast.LENGTH_SHORT).show();
        }
    }



    public void addUserToDB(){

        CollectionReference users = db.collection("users");
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        users.document(userAuth.getCurrentUser().getUid())
                .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegisterActivity.this, "User added to firestore",
                        Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Action Failed -> " + e.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    public void changeToHomeScreen() {
        Intent homeIntent = new Intent(this, WorkoutSelectActivity.class);
        startActivity(homeIntent);
    }
}
