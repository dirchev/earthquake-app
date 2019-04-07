package me.dirchev.mobile.earthquakeapp.models;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 April 07
 */
public class EarthquakeTest {
    @Test
    public void setTitle() {
        Earthquake earthquake = new Earthquake();
        earthquake.setTitle("Test title");
        Assert.assertEquals(earthquake.getTitle(), "Test title");
    }

    @Test
    public void setLink() {
        Earthquake earthquake = new Earthquake();
        earthquake.setLink("Some link");
        Assert.assertEquals(earthquake.getLink(), "Some link");
    }

    @Test
    public void parsePubDate() throws ParseException {
        Earthquake earthquake = new Earthquake();
        earthquake.parsePubDate("Sun, 07 Apr 2019 12:40:00");
        Date pubDate = earthquake.getPubDate();
        Assert.assertEquals(pubDate.getYear(), 2019 - 1900); // because of https://www.javatpoint.com/java-date-getyear-method
        Assert.assertEquals(pubDate.getMonth(), 3); // because months start from 0
        Assert.assertEquals(pubDate.getDate(), 7);
        Assert.assertEquals(pubDate.getHours(), 12);
        Assert.assertEquals(pubDate.getMinutes(), 40);
        Assert.assertEquals(pubDate.getSeconds(), 0);
    }

    @Test
    public void setCategory() {
        Earthquake earthquake = new Earthquake();
        earthquake.setCategory("Some link");
        Assert.assertEquals(earthquake.getCategory(), "Some link");
    }

    @Test
    public void parseLocation() {
        Earthquake earthquake = new Earthquake();
        earthquake.parseLocation("11.111,-22.222");
        LatLng earthquakeLocation = earthquake.getLocation();
        Assert.assertEquals(earthquakeLocation.latitude, 11.111, 0.0001);
        Assert.assertEquals(earthquakeLocation.longitude, -22.222, 0.0001);
    }

    @Test
    public void setLocationName() {
        Earthquake earthquake = new Earthquake();
        earthquake.setLocationName("GLASGOW,UK");
        Assert.assertEquals(earthquake.getLocationName(), "GLASGOW,UK");
    }

    @Test
    public void parseMagnitude() {
        Earthquake earthquake = new Earthquake();
        earthquake.parseMagnitude("3.7");
        double magnitude = earthquake.getMagnitude();
        Assert.assertEquals(magnitude, 3.7, 0.01);
    }

    @Test
    public void parseDepth() {
        Earthquake earthquake = new Earthquake();
        earthquake.parseDepth("12 km");
        Earthquake.Depth depth = earthquake.getDepth();
        Assert.assertEquals(depth.getValue(), 12, 0.01);
        Assert.assertEquals(depth.getMeasure(), "km");
    }

    @Test
    public void parseDescription() {
        Earthquake earthquake = new Earthquake();
        earthquake.parseDescription("Origin date/time: Sat, 30 Mar 2019 05:03:46 ; Location: SOUTHERN NORTH SEA ; Lat/long: 53.684,1.139 ; Depth: 9 km ; Magnitude:  2.4");

        Date pubDate = earthquake.getPubDate();
        LatLng earthquakeLocation = earthquake.getLocation();
        double magnitude = earthquake.getMagnitude();
        Earthquake.Depth depth = earthquake.getDepth();

        Assert.assertEquals(pubDate.getYear(), 2019 - 1900); // because of https://www.javatpoint.com/java-date-getyear-method
        Assert.assertEquals(pubDate.getMonth(), 2); // because months start from 0
        Assert.assertEquals(pubDate.getDate(), 30);
        Assert.assertEquals(pubDate.getHours(), 5);
        Assert.assertEquals(pubDate.getMinutes(), 3);
        Assert.assertEquals(pubDate.getSeconds(), 46);
        Assert.assertEquals(earthquakeLocation.latitude, 53.684, 0.0001);
        Assert.assertEquals(earthquakeLocation.longitude, 1.139, 0.0001);
        Assert.assertEquals(earthquake.getLocationName(), "SOUTHERN NORTH SEA");
        Assert.assertEquals(magnitude, 2.4, 0.01);
        Assert.assertEquals(depth.getValue(), 9, 0.01);
        Assert.assertEquals(depth.getMeasure(), "km");
    }
}