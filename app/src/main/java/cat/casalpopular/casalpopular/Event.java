package cat.casalpopular.casalpopular;

import com.roomorama.caldroid.CalendarHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by tictacbum on 01/02/16.
 */
public class Event implements Comparable<Event> {

    public String id, title, description;
    public Date date;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public Event(JSONObject row) throws JSONException, ParseException {
        id = row.getString("id");
        title = row.getString("title");
        description = row.getString("description");
        date = CalendarHelper.getDateFromString(
                row.getString("date"), "EEE, dd MMM yyyy HH:mm:ss Z"
        );
    }

    public String toString() {
        return dateFormat.format(date) + ": " + title;
    }

    public String formattedDate() {
        return dateFormat.format(date);
    }

    @Override
    public int compareTo(Event another) {
        Event other_event = (Event) another;
        return date.compareTo(other_event.date);
    }
}
