package me.dirchev.mobile.earthquakeapp.models;

import java.util.LinkedList;
import java.util.List;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 March 01
 */
public class EarthquakeRepository {
    List<Earthquake> earthquakeList;

    public EarthquakeRepository () {
        this.earthquakeList = new LinkedList<>();
    }

    public void addEarthquake (Earthquake earthquake) {
        this.earthquakeList.add(earthquake);
    }

    public Earthquake getEarthquakeByIndex (int index) {
        return earthquakeList.get(index);
    }

    public int size () {
        return earthquakeList.size();
    }

    @Override
    public String toString() {
        String result = "";
        for (Earthquake earthquake : earthquakeList) {
            result += earthquake.toString() + "\n============\n";
        }
        return result;
    }
}
