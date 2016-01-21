package cat.casalpopular.casalpopular;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    public final static String apiURL = "http://192.168.10.3:3000";
    JSONArray events = new JSONArray();
    CalendarView cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cal = (CalendarView) findViewById(R.id.calendari);
//        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                //TODO
//                Toast.makeText(
//                        getBaseContext(), "Selected Date is\n\n" + dayOfMonth + " : " +
//                                month + 1 + " : " + year, Toast.LENGTH_SHORT
//                ).show();
//            }
//        });
        new CallAPI().execute(apiURL + "/events.json");
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

    public void populateEvents() {
        try {
            for (int i = 0; i < events.length(); i++) {
                JSONObject row = events.getJSONObject(i);
                int id = row.getInt("id");
                String title = row.getString("title");
                Toast.makeText(getBaseContext(), "id: " + id + " title: " + title, Toast.LENGTH_LONG).show();
                //TODO: marcar els dies a cal com a ocupats i posar-hi listeners
            }
        } catch (JSONException e) {
            showError(e.getMessage());
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
                events = (JSONArray) new JSONTokener(response).nextValue();
                populateEvents();
            } catch (JSONException e) {
                showError(e.getMessage());
            }
        }
    }
}
