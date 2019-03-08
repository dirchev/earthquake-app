package me.dirchev.mobile.earthquakeapp.models;

import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 March 01
 */
public class EarthquakeRepository {
    List<Earthquake> earthquakeList;
    List<EarthquakeRepositoryChangeListener> changeListeners;
    int selectedEarthquakeIndex = 0;

    public EarthquakeRepository () {
        this.earthquakeList = new LinkedList<>();
        this.changeListeners = new LinkedList<>();
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

    public Earthquake getSelectedEarthquake () {
        return earthquakeList.get(this.selectedEarthquakeIndex);
    }

    public void setSelectedEarthquakeIndex (int index) {
        this.selectedEarthquakeIndex = index;
        this.updateAllListeners();
    }

    private void updateAllListeners () {
        for (EarthquakeRepositoryChangeListener listener : changeListeners) {
            listener.onChange(this);
        }
    }

    public void subscribeToChange (EarthquakeRepositoryChangeListener listener) {
        this.changeListeners.add(listener);
    }

    public void unsubscribeToChange (EarthquakeRepositoryChangeListener listener) {
        this.changeListeners.remove(listener);
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
