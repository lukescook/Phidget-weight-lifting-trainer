package com.example.hardwareanddevicesdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SingleWorkoutView extends AppCompatActivity {

    private ListView lv;
    FirebaseAuth userAuth;
    FirebaseFirestore db;
    FirebaseUser currentUser;
    String date;
    ArrayList<Double> valueArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_workout_view);
        db = FirebaseFirestore.getInstance();
        userAuth = FirebaseAuth.getInstance();
        currentUser = userAuth.getCurrentUser();
        lv = findViewById(R.id.valueList);
        Intent bundle = getIntent();
        if(bundle != null){
            date = bundle.getStringExtra("date");
            Log.d("DATE-> ", date);
        }

        valueArray = fetchValues();



    }

    public ArrayList fetchValues(){
        valueArray = new ArrayList<>();
        db.collection("users").document(currentUser.getUid()).collection("workouts")
                .document(date).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    valueArray = (ArrayList<Double>) task.getResult().get("Values");
                    displayValues(valueArray);
                }
            }
        });

        return valueArray;
    }

    public void displayValues(final ArrayList<Double> values){
        final ArrayList<String> valueStrings = new ArrayList<>();

        for(double v : values){
            valueStrings.add(String.valueOf(v));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, valueStrings){
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                if(values.get(position) < 2){
                    tv.setText(valueStrings.get(position) + " m/s");
                    tv.setTextColor(Color.GREEN);
                } else if(values.get(position) > 2 && values.get(position) < 4){
                    tv.setText(valueStrings.get(position) + " m/s");
                    int orange = Color.rgb(255,165,0);
                    tv.setTextColor(orange);
                } else{
                    tv.setText(valueStrings.get(position) + " m/s");
                    tv.setTextColor(Color.RED);
                }
                return view;
            }

        };
        lv.setAdapter(adapter);
    }
}
