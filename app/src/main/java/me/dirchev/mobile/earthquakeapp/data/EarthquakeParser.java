package me.dirchev.mobile.earthquakeapp.data;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
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
public class EarthquakeParser {
    String xmlString;
    LinkedList<Earthquake> earthquakes;

    EarthquakeParser (String xmlString) {
        this.xmlString = xmlString;
        earthquakes = new LinkedList<>();
    }

    public LinkedList<Earthquake> parse() throws XmlPullParserException, IOException {
        Earthquake earthquake = null;

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(this.xmlString));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            // Found a start tag
            if (eventType == XmlPullParser.START_TAG)
            {
                if (xpp.getName().equalsIgnoreCase("item")) {
                    earthquake = new Earthquake();
                } else if (earthquake != null && xpp.getName().equalsIgnoreCase("title")) {
                    String value = xpp.nextText();
                    earthquake.setTitle(value);
                } else if (earthquake != null && xpp.getName().equalsIgnoreCase("description")) {
                    String value = xpp.nextText();
                    earthquake.parseDescription(value);
                } else if (earthquake != null && xpp.getName().equalsIgnoreCase("link")) {
                    String value = xpp.nextText();
                    earthquake.setLink(value);
                } else if (earthquake != null && xpp.getName().equalsIgnoreCase("category")) {
                    String value = xpp.nextText();
                    earthquake.setCategory(value);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (xpp.getName().equalsIgnoreCase("item")) {
                    earthquakes.add(earthquake);
                    earthquake = null;
                }
            }
            eventType = xpp.next();
        }
        return earthquakes;
    }
}
