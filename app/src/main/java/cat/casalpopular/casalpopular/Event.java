package cat.casalpopular.casalpopular;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tictacbum on 01/02/16.
 */
public class Event {

    public String id, title, description;

    public Event(JSONObject row) throws JSONException {
        id = row.getString("id");
        title = row.getString("title");
        description = row.getString("description");
    }

    public String toString() {
        return title;
    }

    public void setTitle(String string) {
        title = string;
    }

}
