package com.example.hardwareanddevicesdemo;

import java.util.ArrayList;

public class Workout {

    private double weight;
    private ArrayList<Double> values;
    private String date;
    private double temperature;


    public Workout(double weight, ArrayList<Double> values, String date, double temperature){
        setDate(date);
        setValues(values);
        setWeight(weight);
        setTemperature(temperature);
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public ArrayList<Double> getValues() {
        return values;
    }

    public void setValues(ArrayList<Double> values) {
        this.values = values;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return
                "Temperature: " + this.getTemperature() + "\n" +
                "Weight: " + this.getWeight() + "\n" +
                "Date: " + this.getDate();
    }
}
