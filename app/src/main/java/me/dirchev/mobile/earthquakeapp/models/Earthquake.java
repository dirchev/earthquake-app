package me.dirchev.mobile.earthquakeapp.models;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 February 21
 */
public class Earthquake {
    private String title;
    private String link;
    private Date pubDate;
    private String category;
    private String locationName;
    private LatLng location;
    private double magnitude;
    private Depth depth;

    public Earthquake() { }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public void parsePubDate (String pubDateString) throws ParseException {
        DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss", Locale.ENGLISH);
        this.setPubDate(df.parse(pubDateString));
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public void parseLocation (String locationString) {
        String[] parts = locationString.split(",");
        double lat = Double.parseDouble(parts[0]);
        double lon = Double.parseDouble(parts[1]);
        this.setLocation(new LatLng(lat, lon));
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public void parseMagnitude (String magnitudeString) {
        this.setMagnitude(Double.parseDouble(magnitudeString));
    }

    public Depth getDepth() {
        return depth;
    }

    public void setDepth(Depth depth) {
        this.depth = depth;
    }

    public void parseDepth (String depthString) {
        String[] parts = depthString.split(" ");
        this.setDepth(new Depth(Double.parseDouble(parts[0]), parts[1]));
    }

    @Override
    public String toString () {
        String result = "";
        result += "Title: " + this.title + "\n";
        result += "Link: " + this.link + "\n";
        result += "PubDate: " + this.pubDate.toLocaleString() + "\n";
        result += "Category: " + this.category+ "\n";
        result += "Location Name: " + this.locationName + "\n";
        result += "Location: " + this.location.toString() + "\n";
        result += "Magnitude: " + this.magnitude + "\n";
        result += "Depth: " + this.depth.toString() + "\n";

        return result;
    }

    public void parseDescription(String descriptionString) {
        String[] fields = descriptionString.split(" ; ");
        for (int i = 0; i < fields.length; i++) {
            String[] keyValue = fields[i].split(": ");
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            try {
                switch (key) {
                    case "Origin date/time":
                        this.parsePubDate(value);
                        break;
                    case "Location":
                        this.setLocationName(value);
                        break;
                    case "Lat/long":
                        this.parseLocation(value);
                        break;
                    case "Depth":
                        this.parseDepth(value);
                        break;
                    case "Magnitude":
                        this.parseMagnitude(value);
                        break;
                }
            } catch (ParseException e) {
                Log.d("Earthquake Parse", "Could not parse the value" + keyValue);
            } catch (NumberFormatException e) {
                Log.d("Earthquake Parse", "Could not parse the value" + keyValue);
            }
        }
    }

    public class Depth {
        double value;
        String measure;
        public Depth(double value, String measure) {
            this.value = value;
            this.measure = measure;
        }

        @Override
        public String toString() {
            return Double.toString(this.value) + this.measure;
        }

        public double getValue() {
            return value;
        }

        public String getMeasure() {
            return measure;
        }
    }
}
