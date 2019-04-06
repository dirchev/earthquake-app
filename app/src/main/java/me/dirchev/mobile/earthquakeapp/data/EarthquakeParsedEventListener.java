package me.dirchev.mobile.earthquakeapp.data;

import java.util.LinkedList;
import java.util.List;

import me.dirchev.mobile.earthquakeapp.models.Earthquake;
import me.dirchev.mobile.earthquakeapp.models.EarthquakeRepository;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 February 21
 */
public interface EarthquakeParsedEventListener{
    void run(LinkedList<Earthquake> earthquakes);
}
