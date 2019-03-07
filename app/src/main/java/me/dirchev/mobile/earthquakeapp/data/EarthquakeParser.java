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

    EarthquakeParser (String xmlString) {
        this.xmlString = xmlString;
    }

    public EarthquakeRepository parse() {
        EarthquakeRepository earthquakeRepository = new EarthquakeRepository();
        Earthquake earthquake = null;

        try {
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
                        earthquakeRepository.addEarthquake(earthquake);
                        earthquake = null;
                    }
                }
                eventType = xpp.next();

            }
        }
        catch (XmlPullParserException ae1)
        {
            Log.e("MyTag","Parsing error" + ae1.toString());
        }
        catch (IOException ae1)
        {
            Log.e("MyTag","IO error during parsing");
        }

        return earthquakeRepository;
    }
}
