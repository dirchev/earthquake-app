package me.dirchev.mobile.earthquakeapp.data;

import java.util.LinkedList;

import me.dirchev.mobile.earthquakeapp.models.Earthquake;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 April 07
 */
public class EarthquakeLoaderResult {
    private LinkedList<Earthquake> earthquakes;
    private Exception error;

    public LinkedList<Earthquake> getEarthquakes() {
        return earthquakes;
    }

    public Exception getError() {
        return error;
    }

    public EarthquakeLoaderResult(LinkedList<Earthquake> earthquakes) {
        this.earthquakes = earthquakes;
    }

    public EarthquakeLoaderResult(Exception error) {
        this.error = error;
    }
}
