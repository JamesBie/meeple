package com.james.android.meepleaid;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 100599223 on 8/8/2017. This package will download the string from the website through ASYNC TASK
 * ...Implementation through a load manager will be upcoming updates
 */

public class DownloadXmlTask  {
    public AsyncResponse delegate = null;
    private DownloadXmlTask(){}
    public static boolean BGGdelayed = false;

    public static List<BoardGame> fetchXML (String requesturl){
        Log.i("FetchXML","entering fetch xml");
        URL url = createURL(requesturl);
        String xmlOutput = "";
        List<BoardGame> boardgames = new ArrayList<>();
        try {
            xmlOutput = makeHttpRequest(url);
        }catch (IOException e){
            Log.e("Do in background", "problem making http request", e);
        }
        try {
            boardgames = ParseXML.parse(xmlOutput);
        } catch (Exception e){
            Log.e("parsing xml", "exception error", e);
            boardgames = null;
        }
        return boardgames;
        }




    //Uploads XML , parses it and combines it with HTML markup. Returns HTML string

    private static String makeHttpRequest (URL url) throws IOException {
        String xmlOutput = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;


        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000/*milliseconds*/);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();
          do {
              Log.i("NetworkActivity", "current URL code is  " + urlConnection.getResponseCode());

              if (urlConnection.getResponseCode() == 200) {
                 Log.i("NetworkActivity", "entered with " + urlConnection.getResponseCode());
                 inputStream = urlConnection.getInputStream();
                 xmlOutput = readFromStream(inputStream);
             } else if (urlConnection.getResponseCode() == 202) {
                 try {
                     Log.w("thread sleep", "thread sleeping now");
                     Thread.sleep(1000);
                     BGGdelayed=true;
                 } catch (Exception e){
                     Log.e("thread sleep", "thread sleeping error", e);}

             }
         }while(!BGGdelayed && urlConnection.getResponseCode()==202);

        } catch (IOException e){
            Log.e("NetworkActivity", "problem retrieving data", e);
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();

            }if (inputStream !=null){
                inputStream.close();
            }

        }

        return xmlOutput;
    }

    //use async task to download the xml feed from url
    private static URL createURL (String stringURL){
        URL url = null;
        try {
            url = new URL(stringURL);

        } catch ( MalformedURLException e){
            Log.e("DownloadXmlTask", "Error with creating the URL", e);
            return null;
        }
        return url;
    }


    private static String readFromStream (InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line != null) {
                output.append(line);
                line=reader.readLine();
            }
        }
        return output.toString();
    }
}
