package com.example.hardwareanddevicesdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class WorkoutSelectActivity extends AppCompatActivity {
    private EditText weightSelection;
    private double currentWeight;
    private FirebaseAuth userAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_select);
        weightSelection = findViewById(R.id.editText2);
        userAuth = FirebaseAuth.getInstance();
    }

    public void startWorkout(View view) {
        if (weightSelection.getText().toString().isEmpty()) {
            Toast.makeText(WorkoutSelectActivity.this, "Please select a weight before working out",
                    Toast.LENGTH_SHORT).show();

        } else {
            currentWeight = Double.parseDouble(weightSelection.getText().toString());
            Intent workout = new Intent(this, MainActivity.class);
            workout.putExtra("weight", currentWeight);
            startActivity(workout);
        }
    }

    public void viewWorkout(View view) {
        Intent workoutView = new Intent(this, WorkoutHistoryActivity.class);
        startActivity(workoutView);
    }

    public void logOut(View view){
        userAuth.signOut();
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }
}
