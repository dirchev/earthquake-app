package me.dirchev.mobile.earthquakeapp;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import me.dirchev.mobile.earthquakeapp.models.Earthquake;
import me.dirchev.mobile.earthquakeapp.models.EarthquakesChannel;
import me.dirchev.mobile.earthquakeapp.models.EarthquakesChannelImage;

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

    public List<EarthquakesChannel> parse() {
        LinkedList<EarthquakesChannel> channelLinkedList = null;
        EarthquakesChannel channel = null;
        EarthquakesChannelImage channelImage = null;
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
                    // We are starting a widget collection
                    if (xpp.getName().equalsIgnoreCase("channel")) {
                        channel = new EarthquakesChannel();
                    } else if (xpp.getName().equalsIgnoreCase("item")) {
                        earthquake = new Earthquake();
                    } else if (xpp.getName().equalsIgnoreCase("title")) {
                        String value = xpp.nextText();
                        if (channelImage != null) {
                            // we are currently in an image item
                            channelImage.setTitle(value);
                        } else if (earthquake != null) {
                            // we are currently in an earthquake item
                            earthquake.setTitle(value);
                        } else {
                            // we are currently in a channel
                            channel.setTitle(value);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("description")) {
                        String value = xpp.nextText();
                        if (earthquake != null) {
                            // we are currently in an earthquake item
                            earthquake.setDescription(value);
                        } else {
                            // we are currently in a channel
                            channel.setDescription(value);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("link")) {
                        String value = xpp.nextText();
                        if (channelImage != null) {
                            // we are currently in an image item
                            channelImage.setLink(value);
                        } else if (earthquake != null) {
                            // we are currently in an earthquake item
                            earthquake.setLink(value);
                        } else {
                            // we are currently in a channel
                            channel.setLink(value);
                        }
                    } else if (xpp.getName().equalsIgnoreCase("url")) {
                        String value = xpp.nextText();
                        channelImage.setUrl(value);
                    } else if (xpp.getName().equalsIgnoreCase("language")) {
                        String value = xpp.nextText();
                        channel.setLanguage(value);
                    } else if (xpp.getName().equalsIgnoreCase("lastBuildDate")) {
                        String value = xpp.nextText();
                        channel.setLastBuildDate(value);
                    } else if (xpp.getName().equalsIgnoreCase("image")) {
                        channelImage = new EarthquakesChannelImage();
                        String value = xpp.nextText();
                        channel.setLastBuildDate(value);
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (xpp.getName().equalsIgnoreCase("item")) {
                        channel.getEarthquakes().add(earthquake);
                        earthquake = null;
                    } else if (xpp.getName().equalsIgnoreCase("channel")) {
                        channelLinkedList.add(channel);
                        channel = null;
                    } else if (xpp.getName().equalsIgnoreCase("image")) {
                        channel.setImage(channelImage);
                        channelImage = null;
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

        return channelLinkedList;
    }
}
