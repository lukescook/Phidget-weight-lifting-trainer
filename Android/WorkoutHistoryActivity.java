package com.example.hardwareanddevicesdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class WorkoutHistoryActivity extends AppCompatActivity {

    String date;
    double weight;
    private ListView lv;
    private FirebaseUser currentUser;
    private FirebaseAuth userAuth;
    private FirebaseFirestore db;
    private ArrayList<Workout> workouts = new ArrayList<>();
    private ArrayList<Double> values = new ArrayList<>();
    private double temperature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);

        db = FirebaseFirestore.getInstance();
        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        workouts = fetchWorkouts();
        lv = findViewById(R.id.listview);

    }

    public ArrayList fetchWorkouts(){
        db.collection("users").document(currentUser.getUid()).collection("workouts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){

                        weight = document.getDouble("Weight");
                        values = (ArrayList<Double>) document.get("Values");
                        date = document.getString("Date");
                        temperature = document.getDouble("Temperature");

                        Workout workout = new Workout(weight, values, date, temperature);
                        workouts.add(workout);

                    }
                    displayWorkouts(workouts);

                }
            }
        });
        return workouts;
    }

    public void displayWorkouts(ArrayList<Workout> userWorkouts){
        if (userWorkouts.size() != 0) {

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                //Allows the group in the list to be clicked on and opens up the group with
                //all its details
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Workout w = (Workout) parent.getAdapter().getItem(position);
                    Intent workoutItem = new Intent(getApplicationContext(), SingleWorkoutView.class);
                    workoutItem.putExtra("date", date);
                    startActivity(workoutItem);
                }
            });
            //Makes the meetings in the list view follow the font size and colour set
            ArrayAdapter<Workout> adapter =
                    new ArrayAdapter<Workout>(this, android.R.layout.simple_list_item_1, userWorkouts) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView tv = view.findViewById(android.R.id.text1);
                            return view;
                        }
                    };
            lv.setAdapter(adapter);
        }
    }
}
