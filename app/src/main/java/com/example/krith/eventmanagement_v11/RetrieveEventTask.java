package com.example.krith.eventmanagement_v11;

import android.app.usage.UsageEvents;
import android.location.Location;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Chirag on 28-03-2015.
 */
public class RetrieveEventTask extends AsyncTask {

    @Override
    protected Object doInBackground(Object[] params) {
        EventDAO eventDAO = (EventDAO)params[0];
        Map<String,List<JSONObject>> eventMap=new HashMap<String,List<JSONObject>>();
        eventDAO.fetchCity((Location)params[1]);
        eventDAO.getEventData(eventMap);
        return eventMap;
    }
}
