package com.example.krith.eventmanagement_v11;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


public class FirstScreen extends Activity{

    AutoCompleteTextView autoCompleteTextView;
    List<String> citiesList = new ArrayList<String>();
    static String userID="";
    static String fbUserName="";
    static SessionState state = null;
    static Context storeContext;

    @Override
    protected void onStart() {
        super.onStart();
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setText("");
        autoCompleteTextView.clearFocus();
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v("TAG CITY : ", parent.getItemAtPosition(position).toString());
                String city = parent.getItemAtPosition(position).toString();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                if(Session.getActiveSession()!=null && state.isOpened())
                {
                    i.putExtra("userid",userID);
                    i.putExtra("username",fbUserName);
                }
                i.putExtra("city",city);
                startActivity(i);
            }
        });
        if(Session.getActiveSession()!=null && (state.isOpened() || state.equals("OPENED")))
        {
            Log.v("Inside onStart",state.toString());
            final ImageView img = (ImageView) findViewById(R.id.imageView4);
            img.setVisibility(View.INVISIBLE);
        }
        else
        {
            final ImageView img = (ImageView) findViewById(R.id.imageView4);
            img.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen2);
        storeContext = FirstScreen.this;
        final ImageView img = (ImageView) findViewById(R.id.imageView4);
        String citiesURL = "https://secure-journey-4788.herokuapp.com/getCities";
        new getCities(this).execute(citiesURL);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Session.getActiveSession() == null  || !state.isOpened())
                {
                    Log.v("TAG", "INSIDE FB LOGIN");
                    final List<String> PERMISSIONS = Arrays.asList("email", "public_profile");
                    Session.openActiveSession(FirstScreen.this, true, new Session.StatusCallback() {
                        @Override
                        public void call(final Session session, SessionState state, Exception exception) {
                            Log.v("TAG ", "INSIDE Call");
                            Log.v("TAG SESSION ID ", session.toString());
                            boolean pendingPublishReauthorization = false;
                            Log.v("Session State", state.toString());
                            List<String> permissions = session.getPermissions();

                            if(state.equals("CLOSED_LOGIN_FAILED"))
                            {
                                session.close();
                                session.closeAndClearTokenInformation();
                            }
                            if (state.isOpened()) {
                                Log.v("TAG ", "STATE IS OPENED");
                                if (!isSubsetOf(PERMISSIONS, permissions)) {
                                    pendingPublishReauthorization = true;
                                    Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(FirstScreen.this, PERMISSIONS);
                                            /*newPermissionsRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);*/
                                    session.requestNewReadPermissions(newPermissionsRequest);
                                    return;
                                }

                                Request.newMeRequest(session, new Request.GraphUserCallback() {

                                    @Override
                                    public void onCompleted(GraphUser user, Response response) {

                                        Log.v("TAG ", "INSIDE ONCOMPLETE");
                                        if (response != null) {
                                            Log.v("TAG USERNAME", user.getName());
                                            userID = user.getId();
                                            fbUserName = user.getName();
                                            final ImageView img = (ImageView) findViewById(R.id.imageView4);
                                            img.setVisibility(View.INVISIBLE);
                                        }
                                    }
                                }).executeAsync();
                            }
                        FirstScreen.state = state;
                        }
                    });
                }
            }
        });


        ImageView img3 = (ImageView) findViewById(R.id.imageView3);
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                Location gpsLocation = new LocationService(FirstScreen.this).getLocation(LocationManager.GPS_PROVIDER);
                if (gpsLocation != null) {
                    new getCurrentLocation(FirstScreen.this).execute(gpsLocation);
                } else {
                    Toast.makeText(getApplicationContext(), "Cannot find your location", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private boolean isSubsetOf(Collection<String> subset,
                               Collection<String> superset) {
        for (String string : subset) {
            if (!superset.contains(string)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_first_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SetList(List<String> citiesList){
        this.citiesList = citiesList;
        String citiesArray[]= new String[this.citiesList.size()];
        for(int i=0;i<this.citiesList.size();i++)
        {
            citiesArray[i] = citiesList.get(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item,citiesArray);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode,
                resultCode, data);
    }

    public void setCurrentLocation(String city){

        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        if(Session.getActiveSession()!=null && (state.isOpened() || state.equals("OPENED")))
        {
            i.putExtra("userid",userID);
            i.putExtra("username",fbUserName);
        }
        i.putExtra("city",city);
        startActivity(i);
    }

    public Context getContext(){
        return storeContext;
    }

    public String getUserId(){
        return userID;
    }

    public String getUserName(){
        return fbUserName;
    }

    public SessionState getState(){
        return FirstScreen.state;
    }
}

class getCities extends AsyncTask<String,Void,List<String>> {

    List<String> cities = new ArrayList<String>();
    private FirstScreen activity;

    public getCities (FirstScreen activity)
    {
        this.activity=activity;
    }

    ProgressDialog progress = new ProgressDialog(new FirstScreen().getContext());

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress.setMessage("Loading..");
        progress.show();
    }

    @Override
    protected List<String> doInBackground(String... params) {
        try {
            HttpGet httppost = new HttpGet(params[0]);
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(httppost);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);
                JSONArray arr = new JSONArray(data);
                for (int i = 0; i < arr.length(); i++) {
                    cities.add(arr.getJSONObject(i).getString("city"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cities;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        super.onPostExecute(strings);
        Log.v("TAG CITIES ", strings.toString());
        progress.dismiss();
        activity.SetList(strings);
    }
}

class getCurrentLocation extends AsyncTask<Location,Void,String>{

    private FirstScreen activity;
    FirstScreen obj = new FirstScreen();
    String city="";
    ProgressDialog progress = new ProgressDialog(new FirstScreen().getContext());

    public getCurrentLocation (FirstScreen activity)
    {
        this.activity=activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress.setMessage("Getting your present Location");
        progress.show();
    }

    @Override
    protected String doInBackground(Location... params) {
        try {
            String get_events = "https://secure-journey-4788.herokuapp.com/getUserLocation?lat=" + params[0].getLatitude() + "&lon=" + params[0].getLongitude();
            URL url = new URL(get_events);
            HttpURLConnection urlConnection = null;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // gets the server json data
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            city = bufferedReader.readLine();
            Log.d("EventDAO", "City : " + city);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return city;
    }

    @Override
    protected void onPostExecute(String city) {
        super.onPostExecute(city);
        Toast.makeText(obj.getContext(),"Your Current Location : "+city,Toast.LENGTH_LONG).show();
        activity.setCurrentLocation(city);
        progress.dismiss();
    }
}