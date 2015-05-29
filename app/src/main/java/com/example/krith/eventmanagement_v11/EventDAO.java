package com.example.krith.eventmanagement_v11;

import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/**
 * Created by Chirag on 28-03-2015.
 */
public class EventDAO {

    String city;
    Date date;

    public EventDAO()
    {
        date= new Date();
    }

    public void setCity(String city)
    {
        this.city=city;
    }

    public Map<String, List<JSONObject>> getEventData(Map<String, List<JSONObject>> eventMap)
    {
        Log.v("TAG ","SERVICE CALLS URLS");
       // SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        List<JSONObject> eventList ;
        int count=0;
        String get_events = "https://secure-journey-4788.herokuapp.com/getDetails?city="+city ;
        try {

           // while(count<7)
            //{
              //  count++;
                //get_events+="&date="+sdf.format(date);
                URL url = new URL(get_events);

                HttpURLConnection urlConnection =
                        (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // gets the server json data
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(
                                urlConnection.getInputStream()));
                String next;
                while ((next = bufferedReader.readLine()) != null) {

                    JSONArray ja = new JSONArray(next);

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = (JSONObject) ja.get(i);
                        String type = jo.getString("type");
                        //Log.d("EventDAO","type = "+type);
                        if (eventMap.get(type) != null) {
                            eventList=eventMap.get(type);
                            eventList.add(jo);
                            eventMap.put(type, eventList);
                        } else {
                            eventList = new ArrayList<JSONObject>();
                            eventList.add(jo);
                            eventMap.put(type, eventList);
                        }
                    }
                }
                Calendar cal = new GregorianCalendar();
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_YEAR, 1);
                date = cal.getTime();
           // }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d("Events" , eventMap.toString());
        return eventMap;
    }

    public void fetchCity(Location gpsLocation) {

        String get_events = "https://secure-journey-4788.herokuapp.com/getUserLocation?lat="+gpsLocation.getLatitude()+"&lon="+gpsLocation.getLongitude();
        try {
            URL url = new URL(get_events);

            HttpURLConnection urlConnection =
                    (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // gets the server json data
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(
                            urlConnection.getInputStream()));
            this.city=bufferedReader.readLine();
            //Log.d("EventDAO","City : "+city);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String getCity() {
        return city;
    }
}
