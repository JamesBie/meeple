package com.james.android.meepleaid;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.in;

/**
 * Created by 100599223 on 8/8/2017.
 */

public class ParseXML {
    private static String mXMLoutput = "";
    private ParseXML(){}
    private final static String ns = null;

    public static List<BoardGame> parse(String xmlOutput) throws Exception {
         mXMLoutput = xmlOutput;
        if(TextUtils.isEmpty(mXMLoutput)){
            Log.i ("ParseXML", "xmL output is empty");
            return null;

        }

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(mXMLoutput));
            parser.nextTag();
            Log.i("parse", " readFeed about to be initialized");
            return readFeed(parser);

        }finally
         {
            in.close();
        }

    }
    // does the actual work of processing xml
    private static List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name = null; // boardgame title
        String min , max = null; // the stats but mostly for player numbers
        List<BoardGame> boardgames = new ArrayList<BoardGame>();

        //items is at the start of the xml file for bgg api
        parser.require(XmlPullParser.START_TAG, ns, "items");
        Log.i("readfeed", "about to parse through while loop!");
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String tag = parser.getName();
            Log.v("readfeed", "tag is " + parser.getName());
            // starts by looking for the item tag which
            //represents each game from bgg
            if (tag.equals("item")) {
                Log.i("readfeed", " about to add to boardgames!");
                boardgames.add(readItem(parser));
            } else {
                Log.i("readfeed", " about to skip a tag!");
                skip(parser);
            }
        }
        Log.i("ParseXML", "returning boardgames");
        return boardgames;
    }
//parses the contents of an item. if it encounters name or stats it'll read more into it
    private static BoardGame readItem (XmlPullParser parser) throws XmlPullParserException,IOException {

        parser.require(XmlPullParser.START_TAG, ns, "item");
        String name = null;
        String min = null;
        String max = null;
        ArrayList<String> stats = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }
            String tag = parser.getName();
            Log.i("readItem", "tag in readItem is " + parser.getName());

            if(tag.equals("name")){
                Log.i("readItem", " about to readName!");
                name= readName(parser);

            }else if (tag.equals("stats")) {
                Log.i("readItem", " about to readStats");
                stats = readStats(parser);
                min = stats.get(0);
                max = stats.get(1);

            }else {
                Log.i("readItem", " Skipped");
                skip(parser);
            }

        }
        Log.i("XML Tags",name);
        Log.v("Stat Tag", min+" "+ max);



        return new BoardGame(name, Integer.parseInt(min), Integer.parseInt(max));

    }
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT){
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static ArrayList<String> readTextOfStats(XmlPullParser parser) throws IOException, XmlPullParserException {
        ArrayList<String> result= new ArrayList<>();
        Log.i("readTextofStats", "before if statement");
        //if (parser.next() == XmlPullParser.TEXT){

        try {
            result.add(parser.getAttributeValue(0));
            result.add(parser.getAttributeValue(1));
        }catch (Exception e){
            result.add("0");
            result.add("0");
        }

            Log.i("readTextofStats", "atribute name is " + result );

            parser.nextTag();

        //}
        Log.i("readTextofStats", "before return statment");
        return result;
    }
// Parse the content of an entry. if it encounters a title, playercount or link tag, hands them off
    //to their respective "read" methods for processing. otherwise skip the tag.

    private  static String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require (XmlPullParser.START_TAG, ns, "name");
        String title = readText(parser);
        Log.i("readName", " Title is " + title);
        parser.require(XmlPullParser.END_TAG,ns, "name");
        return title;

    }

    private static ArrayList<String> readStats (XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "stats");
        Log.i("readStats", " about to read stats!");
        ArrayList stats =readTextOfStats(parser);
        Log.i("readStats", " Stats are " + stats);
        parser.require(XmlPullParser.START_TAG, ns, "rating");
        Log.i("readStats", " before return statment");
        return stats;
    }

    private static void skip(XmlPullParser parser) throws IOException,XmlPullParserException {

        if (parser.getEventType() != XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth !=0){
            switch (parser.next()){
                case XmlPullParser.END_TAG:
                    depth --;
                    break;
                case XmlPullParser.START_TAG:
                    depth ++;
                    break;
            }
        }
    }
}

/*
    // does the actual work of processing xml
    private BoardGame readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        String name = null; // boardgame title
        String player = null; // the stats but mostly for player numbers
        int counter = 0;

        parser.require(XmlPullParser.START_TAG, ns, "item");
        while (parser.next() != XmlPullParser.END_TAG){
            if (parser.getEventType() != XmlPullParser.START_TAG){
                continue;
            }

            String tag = parser.getName();
            // starts by looking for the entry tag

            if(tag.equals("name")){
                name= readName(parser);

            }else if (tag.equals("stats")) {
                player = readPlayer(parser);

            }else {
                skip(parser);
            }

        }
        Log.i("XML Tags",name);
        Log.v("Stat Tag", player);
        return new BoardGame(name,1,4);

    }
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException{
        String result = "";
        if (parser.next() == XmlPullParser.TEXT){
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
// Parse the content of an entry. if it encounters a title, playercount or link tag, hands them off
    //to their respective "read" methods for processing. otherwise skip the tag.

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require (XmlPullParser.START_TAG, ns, "name");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG,ns, "name");
        return title;

    }

    private String readPlayer (XmlPullParser parser) throws IOException, XmlPullParserException{
        parser.require(XmlPullParser.START_TAG, ns, "stats");
        String player =readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "stats");
        return player;
    }

    private void skip(XmlPullParser parser) throws IOException,XmlPullParserException{

        if (parser.getEventType() != XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth !=0){
            switch (parser.next()){
                case XmlPullParser.END_TAG:
                    depth --;
                    break;
                case XmlPullParser.START_TAG:
                    depth ++;
                    break;
            }
        }
    }

    *//**
     * convert inputstream into a string which has the whole response from server
     *//*


}
*/