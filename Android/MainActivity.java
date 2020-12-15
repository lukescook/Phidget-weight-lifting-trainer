package com.example.hardwareanddevicesdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private String TAG = "MainActivity";
    private SensorManager sm;
    private FirebaseFirestore db;
    private FirebaseAuth userAuthenticate;
    private DocumentReference reference;
    private FirebaseUser currentUser;
    private double weight;
    private GraphView graph;
    private LineGraphSeries<DataPoint> dataSeries;
    private static double currentX;
    private ThreadPoolExecutor graphExecutor;
    private LinkedBlockingQueue<Double> acceleration = new LinkedBlockingQueue<>();
    private Map<String, Object> workoutData = new HashMap<>();
    private ArrayList<Double> workout = new ArrayList<>();
    private double xValue;
    private double yValue;
    private double zValue;
    private double accSquareRoot;
    private double accValue;
    private double tempValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent bundle = getIntent();
        if(bundle != null){
            weight = bundle.getDoubleExtra("weight", 40);
        }
        db = FirebaseFirestore.getInstance();
        userAuthenticate = FirebaseAuth.getInstance();
        currentUser = userAuthenticate.getCurrentUser();
        reference = db.collection("users").document(currentUser.getUid());
        sm= (SensorManager) getSystemService(SENSOR_SERVICE);

        graph = (GraphView) findViewById(R.id.graphView);

        dataSeries = new LineGraphSeries<>();
        dataSeries.setColor(Color.GREEN);
        graph.addSeries(dataSeries);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0.5);
        graph.getViewport().setMaxX(6.5);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(6);
        currentX = 0;
        graphExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        if (graphExecutor != null) {
            graphExecutor.execute(new AccelerationChart(new AccelerationChartHandler()));

        }
        getTemperature();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(sensorEvent);
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE){
            tempValue = sensorEvent.values[0];
            Log.d(TAG, "temperature>> " + tempValue);
            Toast.makeText(this, "Temperature: " + tempValue, Toast.LENGTH_SHORT).show();
        }
    }

    public void getTemperature(){

        Sensor temp = sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        if(temp != null){
            sm.registerListener(this, temp, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            tempValue = 16;
            Toast.makeText(this, "Temperature sensor not available", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        sm.unregisterListener(this);
    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        xValue = values[0];
        yValue = values[1];
        zValue = values[2];

        accSquareRoot = (xValue * xValue + yValue * yValue + zValue * zValue)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

        accValue = Math.sqrt(accSquareRoot);
        workout.add(accValue);
        acceleration.offer(accValue);

    }

    public void changeToHome(){
        Intent home = new Intent(this, WorkoutSelectActivity.class);
        startActivity(home);
    }

    public void stop(View view){
        addData(workout);
    }

    public void addData(ArrayList<Double> workout){
        Date date = new Date();
        Map<String, Object> test = new HashMap<>();
        test.put("Values", workout);
        test.put("Weight", weight);
        test.put("Date", date.toString());
        test.put("Temperature", tempValue);


        reference.collection("workouts").document(date.toString()).set(test)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Workout added to database",
                        Toast.LENGTH_SHORT).show();
            }
        });
        changeToHome();
    }

    public void changeToLogin(){
        userAuthenticate.signOut();
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
    }


    private class AccelerationChartHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Double accelerationY = 0.0D;
            if (!msg.getData().getString("ACCELERATION_VALUE").equals(null) && !msg.getData().getString("ACCELERATION_VALUE").equals("null")) {
                accelerationY = (Double.parseDouble(msg.getData().getString("ACCELERATION_VALUE")));
            }

            dataSeries.appendData(new DataPoint(currentX, accelerationY), true, 10);
            currentX = currentX + 1;
        }
    }

    private class AccelerationChart implements Runnable {
        private boolean drawChart = true;
        private Handler handler;

        public AccelerationChart(Handler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            while (drawChart) {
                Double accelerationY;
                try {
                    Thread.sleep(200); // Speed up the X axis
                    accelerationY = acceleration.poll();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
                if (accelerationY == null)
                    continue;

                // currentX value will be excced the limit of double type range
                // To overcome this problem comment of this line
                // currentX = (System.currentTimeMillis() / 1000) * 8 + 0.6;

                Message msgObj = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("ACCELERATION_VALUE", String.valueOf(accelerationY));
                msgObj.setData(b);
                handler.sendMessage(msgObj);
            }
        }
    }
}