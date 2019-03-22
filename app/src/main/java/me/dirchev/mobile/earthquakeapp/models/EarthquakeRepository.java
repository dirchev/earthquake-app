package me.dirchev.mobile.earthquakeapp.models;

import android.util.Log;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
    Map<String, List<Earthquake>> earthquakesByDateList;

    List<EarthquakeRepositoryChangeListener> changeListeners;
    int selectedEarthquakeIndex = -1;
    Date selectedDate = new Date();

    public static String getStringFromEarthquakePubDate (Date date) {
        return date.getDate() + "/" + date.getMonth() + "/" + date.getYear();
    }

    public EarthquakeRepository () {
        this.earthquakeList = new LinkedList<>();
        this.changeListeners = new LinkedList<>();
        this.earthquakesByDateList = new HashMap<>();
        Log.e("MyTag/initial date", this.getStringFromEarthquakePubDate(this.selectedDate));
    }

    public void addEarthquake (Earthquake earthquake) {
        String dateString = this.getStringFromEarthquakePubDate(earthquake.getPubDate());
        this.earthquakeList.add(earthquake);
        if (earthquakesByDateList.containsKey(dateString)) {
            earthquakesByDateList.get(dateString).add(earthquake);
        } else {
            List<Earthquake> newListForDate = new LinkedList<>();
            newListForDate.add(earthquake);
            earthquakesByDateList.put(dateString, newListForDate);
            Log.e("MyTag/ new date", dateString);
        }
    }

    public Earthquake getEarthquakeByIndex (int index) {
        return earthquakeList.get(index);
    }

    public int size () {
        return earthquakeList.size();
    }

    public Earthquake getSelectedEarthquake () {
        if (this.selectedEarthquakeIndex == -1) return null;
        return this.getEarthquakesForDate().get(this.selectedEarthquakeIndex);
    }

    public List<Earthquake> getEarthquakesForDate () {
        List<Earthquake> list = earthquakesByDateList.get(this.getStringFromEarthquakePubDate(this.selectedDate));
        if (list == null) {
            return new LinkedList<>();
        }
        return list;
    }

    public void setSelectedEarthquakeIndex (int index) {
        this.selectedEarthquakeIndex = index;
        this.updateAllListeners();
    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
        this.selectedEarthquakeIndex = -1;
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
