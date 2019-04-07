package me.dirchev.mobile.earthquakeapp.data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import me.dirchev.mobile.earthquakeapp.models.Earthquake;

/**
 * Mobile Platform Development Coursework 2019
 * Name:                    Dimitar Mirchev
 * Student ID:              S1515512
 * Programme of study:      Computing
 * 2019 April 07
 */
@RunWith(RobolectricTestRunner.class)
public class EarthquakeParserTest {

    @Test
    public void parseNoEarthquakes() {
        String rawXML = "" +
                "<?xml version=\"1.0\"?>\n" +
                "<rss version=\"2.0\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
                "<channel>\n" +
                "<title>Recent UK earthquakes</title>\n" +
                "<link>http://earthquakes.bgs.ac.uk/</link>\n" +
                "<description>Recent UK seismic events recorded by the BGS Seismology team</description>\n" +
                "<language>en-gb</language>\n" +
                "<lastBuildDate>Sun, 07 Apr 2019 12:40:00</lastBuildDate>\n" +
                "<image>\n" +
                "<title>BGS Logo</title>\n" +
                "<url>http://www.bgs.ac.uk/images/logos/bgs_c_w_227x50.gif</url>\n" +
                "<link>http://earthquakes.bgs.ac.uk/</link>\n" +
                "</image>\n" +
                "</channel>\n" +
                "</rss>";
        EarthquakeParser parser = new EarthquakeParser(rawXML);
        List<Earthquake> earthquakes = parser.parse();
        Assert.assertEquals(earthquakes.size(), 0);
    }

    @Test
    public void parseSingleEarthquake() {
        String rawXML = "" +
                "<?xml version=\"1.0\"?>\n" +
                "<rss version=\"2.0\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
                "<channel>\n" +
                "<title>Recent UK earthquakes</title>\n" +
                "<link>http://earthquakes.bgs.ac.uk/</link>\n" +
                "<description>Recent UK seismic events recorded by the BGS Seismology team</description>\n" +
                "<language>en-gb</language>\n" +
                "<lastBuildDate>Sun, 07 Apr 2019 12:40:00</lastBuildDate>\n" +
                "<image>\n" +
                "<title>BGS Logo</title>\n" +
                "<url>http://www.bgs.ac.uk/images/logos/bgs_c_w_227x50.gif</url>\n" +
                "<link>http://earthquakes.bgs.ac.uk/</link>\n" +
                "</image>\n" +
                "<item>\n" +
                "<title>UK Earthquake alert : M  2.4 :SOUTHERN NORTH SEA, Sat, 30 Mar 2019 05:03:46</title>\n" +
                "<description>Origin date/time: Sat, 30 Mar 2019 05:03:46 ; Location: SOUTHERN NORTH SEA ; Lat/long: 53.684,1.139 ; Depth: 9 km ; Magnitude:  2.4</description>\n" +
                "<link>http://earthquakes.bgs.ac.uk/earthquakes/recent_events/20190330050239.html</link>\n" +
                "<pubDate>Sat, 30 Mar 2019 05:03:46</pubDate>\n" +
                "<category>EQUK</category>\n" +
                "<geo:lat>53.684</geo:lat>\n" +
                "<geo:long>1.139</geo:long>\n" +
                "</item>\n" +
                "</channel>\n" +
                "</rss>";
        EarthquakeParser parser = new EarthquakeParser(rawXML);
        List<Earthquake> earthquakes = parser.parse();
        Assert.assertEquals(earthquakes.size(), 1);
    }

    @Test
    public void parseMultipleEarthquakes() {
        String rawXML = "" +
                "<?xml version=\"1.0\"?>\n" +
                "<rss version=\"2.0\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
                "<channel>\n" +
                "<title>Recent UK earthquakes</title>\n" +
                "<link>http://earthquakes.bgs.ac.uk/</link>\n" +
                "<description>Recent UK seismic events recorded by the BGS Seismology team</description>\n" +
                "<language>en-gb</language>\n" +
                "<lastBuildDate>Sun, 07 Apr 2019 12:40:00</lastBuildDate>\n" +
                "<image>\n" +
                "<title>BGS Logo</title>\n" +
                "<url>http://www.bgs.ac.uk/images/logos/bgs_c_w_227x50.gif</url>\n" +
                "<link>http://earthquakes.bgs.ac.uk/</link>\n" +
                "</image>\n" +
                "<item>\n" +
                "<title>UK Earthquake alert : M  2.4 :SOUTHERN NORTH SEA, Sat, 30 Mar 2019 05:03:46</title>\n" +
                "<description>Origin date/time: Sat, 30 Mar 2019 05:03:46 ; Location: SOUTHERN NORTH SEA ; Lat/long: 53.684,1.139 ; Depth: 9 km ; Magnitude:  2.4</description>\n" +
                "<link>http://earthquakes.bgs.ac.uk/earthquakes/recent_events/20190330050239.html</link>\n" +
                "<pubDate>Sat, 30 Mar 2019 05:03:46</pubDate>\n" +
                "<category>EQUK</category>\n" +
                "<geo:lat>53.684</geo:lat>\n" +
                "<geo:long>1.139</geo:long>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>UK Earthquake alert : M  2.4 :SOUTHERN NORTH SEA, Sat, 30 Mar 2019 05:03:46</title>\n" +
                "<description>Origin date/time: Sat, 30 Mar 2019 05:03:46 ; Location: SOUTHERN NORTH SEA ; Lat/long: 53.684,1.139 ; Depth: 9 km ; Magnitude:  2.4</description>\n" +
                "<link>http://earthquakes.bgs.ac.uk/earthquakes/recent_events/20190330050239.html</link>\n" +
                "<pubDate>Sat, 30 Mar 2019 05:03:46</pubDate>\n" +
                "<category>EQUK</category>\n" +
                "<geo:lat>53.684</geo:lat>\n" +
                "<geo:long>1.139</geo:long>\n" +
                "</item>\n" +
                "</channel>\n" +
                "</rss>";
        EarthquakeParser parser = new EarthquakeParser(rawXML);
        List<Earthquake> earthquakes = parser.parse();
        Assert.assertEquals(earthquakes.size(), 2);
    }
}