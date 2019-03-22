package me.dirchev.mobile.earthquakeapp.models;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 March 01
 */
public class EarthquakeDataStore {
    private EarthquakeRepository earthquakeRepository;
    private static EarthquakeDataStore instance;

    private EarthquakeDataStore () {
        this.earthquakeRepository = new EarthquakeRepository();
    }

    public static EarthquakeDataStore getInstance () {
        if (instance == null) {
            instance = new EarthquakeDataStore();
        }
        return instance;
    }

    public EarthquakeRepository getEarthquakeRepository() {
        return earthquakeRepository;
    }
}
