package cat.casalpopular.casalpopular;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public final static String BASE_URL = "https://casalpopular.cat";
    public final static String API_URL = BASE_URL+"/api";
    //public final static String API_URL = "http://localhost:3000";

    Hashtable<Date,Event> eventsHash = new Hashtable<Date, Event>();
    private CustomCaldroidFragment caldroidFragment;
    private HashMap<String, Object> extraData;
    private CustomArrayAdapter adapter;
    private CaldroidListener caldroidListener;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        caldroidFragment = new CustomCaldroidFragment();
        final ListView events_list = (ListView) findViewById(R.id.events_list);
        final ArrayList<Event> eventsArray = new ArrayList<Event>();
        adapter = new CustomArrayAdapter(this, eventsArray);
        events_list.setAdapter(adapter);

        events_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * Callback method to be invoked when an item in this AdapterView has
             * been clicked.
             * <p/>
             * Implementers can call getItemAtPosition(position) if they need
             * to access the data associated with the selected item.
             *
             * @param parent   The AdapterView where the click happened.
             * @param view     The view within the AdapterView that was clicked (this
             *                 will be a view provided by the adapter)
             * @param position The position of the view in the adapter.
             * @param id       The row id of the item that was clicked.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Event event = (Event) parent.getItemAtPosition(position);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BASE_URL+"/node/"+event.id));
                startActivity(browserIntent);
                //Toast.makeText(getBaseContext(), event.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // If Activity is created after rotation
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        // If activity is created from fresh
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

            // Uncomment this to customize startDayOfWeek
             args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);

            // Uncomment this line to use Caldroid in compact mode
            // args.putBoolean(CaldroidFragment.SQUARE_TEXT_VIEW_CELL, false);

            // Uncomment this line to use dark theme
            // args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);

            caldroidFragment.setArguments(args);
        }

        caldroidListener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                ArrayList<Event> eventsThisDay = new ArrayList<Event>();
                for (Date d : eventsHash.keySet()) {
                    if (dateFormat.format(date).equals(dateFormat.format(d))) {
                        eventsThisDay.add(eventsHash.get(d));
                    }
                }
                if (eventsThisDay.size() > 0) {
                    adapter.clear();
                    Collections.sort(eventsThisDay);
                    for (Event event : eventsThisDay) {
                        adapter.add(event);
                        //Toast.makeText(
                        //        getApplicationContext(),
                        //        event.title + ": " + event.description,
                        //        Toast.LENGTH_LONG
                        //).show();
                    }
                } else {
                    onChangeMonth(caldroidFragment.getMonth(), caldroidFragment.getYear());
                }
            }

            @Override
            public void onChangeMonth(int month, int year) {
                // populate events on list
                ArrayList<Event> eventsThisMonth = new ArrayList<Event>();
                for (Date date : eventsHash.keySet()) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH)+1 == month) {
                        eventsThisMonth.add(eventsHash.get(date));
                    }
                }
                adapter.clear();
                Collections.sort(eventsThisMonth);
                for (Event e : eventsThisMonth) {
                    adapter.add(e);
                }
            }
        };

        caldroidFragment.setCaldroidListener(caldroidListener);
        extraData = caldroidFragment.getExtraData();

        // Attach to the activity
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendari, caldroidFragment);
        t.commit();

        new CallAPI().execute(API_URL + "/events.json");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    // populates events on calendar
    public void populateEvents() {
        if (caldroidFragment != null) {
            for (Date from : eventsHash.keySet()) {
                // mark days with events
                if (caldroidFragment != null) {
                    caldroidFragment.setBackgroundResourceForDate(R.color.colorPrimary, from);
                    caldroidFragment.setTextColorForDate(R.color.caldroid_black, from);
                    extraData.put(dateFormat.format(from), eventsHash.get(from).title);
                }
            }
            caldroidFragment.refreshView();
        }
    }

    public void showError(String error) {
        System.out.println("show ERROR: "+error);
        Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();
    }

    private class CallAPI extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call

            // HTTP Get
            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();

                } finally {
                    urlConnection.disconnect();
                }

            } catch (Exception e) {
                System.out.println(e.getMessage());
                return e.getMessage();
            }
        }

        protected void onPostExecute(String response) {
            try {
                // populate events
                JSONArray events = (JSONArray) new JSONTokener(response).nextValue();
                for (int i = 0; i < events.length(); i++) {
                    JSONObject row = events.getJSONObject(i);
                    Event event = new Event(row);
                    eventsHash.put(event.date, event);
                }
                populateEvents();
                caldroidListener.onChangeMonth(caldroidFragment.getMonth(), caldroidFragment.getYear());
            } catch (JSONException|ParseException e) {
                showError(e.getMessage());
            }
        }
    }
}
