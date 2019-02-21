package me.dirchev.mobile.earthquakeapp;

import java.util.List;

import me.dirchev.mobile.earthquakeapp.models.EarthquakesChannel;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 February 21
 */
public interface EarthquakeParsedEventListener{
    void run (List<EarthquakesChannel> earthquakesChannelList);
}
