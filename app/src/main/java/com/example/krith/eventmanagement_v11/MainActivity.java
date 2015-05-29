package com.example.krith.eventmanagement_v11;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.internal.ImageRequest;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MainActivity extends FragmentActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    Bitmap bitmap = null;
    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    double lat = 0.0;
    double lon = 0.0;
    List<String> drawerList;
    String url = "https://secure-journey-4788.herokuapp.com/getUserLocation?lat=";
    String city = "";
    String latitude = "";
    String longitude = "";
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    RelativeLayout mDrawerPane;
    //String user_id = "877555828977050";
    static String userId = null;
    static String fbUserName = null;
    static String fbEmail = null;
    Facebook fb = new Facebook("352608801604477");
    PageAdapter pageAdapter;
    private ViewPager mViewPager;
    private TabHost mTabHost;
    private static Map<String, List<JSONObject>> eventMap;
    SectionsPagerAdapter mSectionsPagerAdapter;
    Session session;
    public static final String MyPREFERENCES = "MyFacebookDetails";
    ProgressDialog progress;
    private static Context context;

    public static Context getAppContext(){
        return MainActivity.context;
    }

    @Override
    public void onSaveInstanceState(Bundle data) {
        super.onSaveInstanceState(data);
        data.putString("FbUserName", fbUserName);
        data.putString("FbUserId", userId);
        data.putParcelable("bitmap", bitmap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private void initialiseTabHost() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        for (int i = 0; i < eventMap.keySet().size(); i++) {
            String tab = mSectionsPagerAdapter.getPageTitle(i).toString();
            MainActivity.AddTab(this, this.mTabHost, this.mTabHost.newTabSpec(tab).setIndicator(tab));
        }

        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(Color.parseColor("#707070"));
            tv.setTextSize(10);
            tv.setTypeface(null,Typeface.NORMAL);
        }

        TextView tv = (TextView) mTabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTextSize(10);
        tv.setTypeface(null,Typeface.NORMAL);
        mTabHost.setOnTabChangedListener(this);
    }

    @Override
    public void onPageSelected(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        int pos = this.mViewPager.getCurrentItem();
        this.mTabHost.setCurrentTab(pos);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    public void onTabChanged(String tag) {
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);

        for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(Color.parseColor("#707070"));
            tv.setTextSize(10);
            tv.setTypeface(null,Typeface.NORMAL);
        }

        TextView tv = (TextView) mTabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
        tv.setTextColor(Color.parseColor("#FFFFFF"));
        tv.setTextSize(10);
        tv.setTypeface(null, Typeface.NORMAL);
    }

    private static void AddTab(MainActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec) {
        tabSpec.setContent(new TabFactory(activity));
        tabHost.addTab(tabSpec);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        Log.v("TAG ","Inside onStart");
        if(Session.getActiveSession()!=null) {
            Log.v("TAG Session", Session.getActiveSession().toString());
        }
        else
        {
            Log.v("TAG Session"," NULL");
        }

        if(sharedPreferences.getString("userID",null)!=null) {
            setActivityContents();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("TAG ","Inside onStop");
    }

    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        int targetWidth = 50;
        int targetHeight = 50;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth, targetHeight), null);
        return targetBitmap;
    }

    public void setActivityContents()
    {
        Log.v("TAG ","Inside setActivityContents");
        SharedPreferences sharedPreferences = getSharedPreferences(MyPREFERENCES,MODE_PRIVATE);
        final ImageButton fblogin = (ImageButton) findViewById(R.id.img_login);
        final TextView username = (TextView) findViewById(R.id.userName);
        final ImageView avatar = (ImageView) findViewById(R.id.avatar);
        fblogin.setImageResource(R.drawable.logout);
        this.userId = sharedPreferences.getString("userID",null);
        this.fbUserName = sharedPreferences.getString("userName",null);
        username.setText(this.fbUserName);

        //this.bitmap = getRoundedShape(decodeBase64(sharedPreferences.getString("fbpic", null)));
        this.bitmap = decodeBase64(sharedPreferences.getString("fbpic", null));
        avatar.setImageBitmap(this.bitmap);
    }

    public void setActivityOnIntentCallFromFirstScreen()
    {
        Log.v("TAG ","INSIDE SetActivityonIntent");
        Bundle extras = getIntent().getExtras();
        this.userId = extras.getString("userid");
        this.fbUserName = extras.getString("username");
        final TextView username = (TextView) findViewById(R.id.userName);
        username.setText(this.fbUserName);
        new getFbImage().execute();
        extras.clear();
    }

    public void savePreferences()
    {
        Log.v("TAG ","Inside savePreferences");
        SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES,MODE_PRIVATE).edit();
        editor.putString("userID",userId);
        editor.putString("userName",fbUserName);
        editor.putString("fbpic",encodeTobase64(bitmap));
        editor.commit();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Log.v("TAG ", "INSIDE ONCEATE");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------ PROGRESS ------------------------//
        progress = new ProgressDialog(MainActivity.this);
        progress.setMessage("Searching for events in your city");
        progress.show();
        //---------------------------------------------//


        Bundle extras = getIntent().getExtras();
        city = extras.getString("city");
        Log.v("TAG ExtrasKeySet",extras.keySet().toString());

        if(Session.getActiveSession()!=null && extras.containsKey("userid"))
        {
            Log.v("TAG USERID FS",extras.getString("userid"));
            Log.v("TAG ","ERROR GETTING INSIDE");
            setActivityOnIntentCallFromFirstScreen();
        }

        // TABS IMPLEMNTATION
        eventMap = new HashMap<String, List<JSONObject>>();
        EventDAO eventDao = new EventDAO();
        eventDao.setCity(city);
        eventMap = eventDao.getEventData(eventMap);
        //Log.v("Inside Main eventMap", eventMap.toString());

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Tab Initialization
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initialiseTabHost();
                mViewPager.setAdapter(mSectionsPagerAdapter);
                mViewPager.setOnPageChangeListener(MainActivity.this);
                progress.dismiss();
            }
        }, 2000);


        // CLOSES

        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        final ImageButton fblogin = (ImageButton) findViewById(R.id.img_login);
        final TextView username = (TextView) findViewById(R.id.userName);
        final ImageButton button = (ImageButton) findViewById(R.id.imageView1);
        final ImageView avatar = (ImageView) findViewById(R.id.avatar);
        session = Session.getActiveSession();

        if (savedInstanceState != null) {
            Log.v("Inside Saved ", "Instance");
            userId = savedInstanceState.getString("FbUserId");
            username.setText(savedInstanceState.getString("FbUserName"));
            fbUserName = savedInstanceState.getString("FbUserName");
            //bitmap = getRoundedShape((Bitmap) savedInstanceState.getParcelable("bitmap"));
            bitmap = (Bitmap) savedInstanceState.getParcelable("bitmap");
            avatar.setImageBitmap(bitmap);
        }

        if (session != null && userId != null) {
            FBUserDetails details;
            mDrawerList.setPadding(0, 0, 0, 150);
            //Log.v("TAG CLASS G/S username",details.getFbUserName());
            fblogin.setImageResource(R.drawable.logout);
        } else {
            mDrawerList.setPadding(0, 0, 0, 50);
            fblogin.setImageResource(R.drawable.login);
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });

        mNavItems.add(new NavItem("Browse", "Change your preferences", R.drawable.ic_action_search));
        mNavItems.add(new NavItem("About Us", "Know about us", R.drawable.ic_action_person));
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);
        DrawerListAdapter adapter = new DrawerListAdapter(getApplicationContext(), mNavItems);
        mDrawerList.setAdapter(adapter);

        fblogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Session.getActiveSession() != null && userId != null) {
                    Log.v("TAG", "INSIDE FB LOGOUT");
                    SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES,MODE_PRIVATE).edit();
                    new logoutOfFb().execute(getApplicationContext());
                    Session.getActiveSession().close();
                    Session.getActiveSession().closeAndClearTokenInformation();
                    Session.setActiveSession(null);
                    editor.clear();
                    editor.commit();
                    userId = null;
                    fbUserName = "";
                    mDrawerList.setPadding(0, 0, 0, 50);
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            startActivity(getIntent());
                        }
                    }, 1000);
                } else {
                        Log.v("TAG", "INSIDE FB LOGIN");
                        final List<String> PERMISSIONS = Arrays.asList("email", "public_profile");
                        Session.openActiveSession(MainActivity.this, true, new Session.StatusCallback() {
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
                                            Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(MainActivity.this, PERMISSIONS);
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
                                                    MainActivity.this.session = session;
                                                    fblogin.setImageResource(R.drawable.logout);
                                                    username.setText(user.getName());
                                                    userId = user.getId();
                                                    fbUserName = user.getName();
                                                    Log.v("TAG USER ID", user.getId());
                                                    new getFbImage().execute();
                                                    onSaveInstanceState(new Bundle());
                                                    mDrawerList.setPadding(0, 0, 0, 150);
                                                }
                                            }
                                        }).executeAsync();
                                    }
                               }
                        });
                }
            }
        });

        MainActivity.context = getApplicationContext();
    }


  /*  @Override
    public void onDetachedFromWindow()
    {
        Log.v("TAG ","Inside onDetachedFromWindow");
    }*/


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            ListFragment fragment = PlaceholderFragment.newInstance(position + 1,getPageTitle(position).toString());
            return fragment;
        }

        @Override
        public int getCount() {
            //Log.v("EventMap in getCount",eventMap.toString());
            return eventMap.keySet().size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            int count=0;
            Set<String> titles=eventMap.keySet();
            Iterator<String> iterator=titles.iterator();
            while(iterator.hasNext())
            {
                if(count==position)
                {
                    return iterator.next();
                }
                iterator.next();
                count++;
            }

            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */


        private static final String ARG_SECTION_NUMBER = "section_number";

        private static int position;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String title) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            position=sectionNumber-1;
            args.putString("TITLE",title);
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            //Log.d("Title : ",title);
            //Log.d("Position : ",position+"");
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_search, container, false);
            return rootView;
        }

        @Override
        public void onViewCreated (View view, Bundle savedInstanceState){
            RecyclerView rv = (RecyclerView)view.findViewById(R.id.list);
            rv.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this.getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(llm);
            Bundle args=getArguments();
            String title = args.getString("TITLE");
            //Log.d("Title : ", title);
            List<JSONObject> list = eventMap.get(title);
            //Log.d("List",list.toString());
            final List<EventInfo> eventList= new ArrayList<EventInfo>();
            try {
                for (JSONObject jo : list) {
                    EventInfo event = new EventInfo();
                    event.setEventName(jo.getString("eventname"));
                    event.setEventCity(jo.getString("eventcity"));
                    event.setEventdetails(jo.getString("eventdetails"));
                    event.setEventDate(new SimpleDateFormat("yyyy-MM-dd").parse(jo.getString("eventdate").substring(0, jo.getString("eventdate").indexOf("T"))));
                    event.setImage(new URL(jo.getString("imageurl")));
                    eventList.add(event);
                }
                EventAdapter eventAdapter = new EventAdapter(eventList);
                rv.setAdapter(eventAdapter);
               /* eventAdapter.SetOnItemClickListener(new EventAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        EventInfo event = new EventInfo();
                        event = eventList.get(position);
                        Intent i_to_details;
                        i_to_details = new Intent(getAppContext(),EventDetails.class);
                        i_to_details.putExtra("eventname",event.getEventName());
                        i_to_details.putExtra("eventcity",event.getEventCity());
                        i_to_details.putExtra("eventdetails",event.getEventdetails());
                        i_to_details.putExtra("imageurl",event.getImage());
                        i_to_details.putExtra("eventdate",new SimpleDateFormat("dd/MM/yyyy").format(event.getEventDate()));
                        startActivity(i_to_details);
                    }
                }); */
            }catch(JSONException e)
            {
                Log.e("Error : ",e.toString());
            }
            catch(ParseException e)
            {
                Log.e("Error : ",e.toString());
            }
            catch(MalformedURLException e)
            {
                Log.e("Error : ",e.toString());
            }
        }

    }


    class logoutOfFb extends AsyncTask<Context,Void,Void> {

        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Logging Out");
            progress.show();
        }

        @Override
        protected Void doInBackground(Context... params) {
            try {
                fb.logout(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            startActivity(getIntent());
        }
    }

    class getFbImage extends AsyncTask<Void,Void,Bitmap> {

        ProgressDialog progress = new ProgressDialog(MainActivity.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Please Wait..");
            progress.show();
        }
        @Override
        protected Bitmap doInBackground(Void... params) {
            Log.v("TAG : ", "INSIDE IMAGE RETRIEVAL");

            HttpClient client = new DefaultHttpClient();
            String url = "http://graph.facebook.com/"+userId+"/picture?type=small";
            Log.v("TAG URL ",url);
            HttpGet request = new HttpGet(url);
            HttpResponse response;
            try {
                response = (HttpResponse)client.execute(request);
                HttpEntity entity = response.getEntity();
                BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(entity);
                InputStream inputStream = bufferedEntity.getContent();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            catch (ClientProtocolException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.v("TAG ","IMAGE onPost");
            final ImageView avatar = (ImageView) findViewById(R.id.avatar);
            avatar.setImageBitmap(bitmap);
            savePreferences();
            progress.dismiss();
        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode,
                resultCode, data);
    }
}

