package cat.casalpopular.casalpopular;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tictacbum on 03/02/16.
 */
public class CustomArrayAdapter extends ArrayAdapter<Event> {
  private final Context context;
  private final ArrayList<Event> values;

    static class ViewHolder {
        public TextView title;
        public TextView date;
        //public ImageView image;
    }

    public CustomArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, -1, events);
        this.context = context;
        this.values = events;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowlayout, null);
            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title = (TextView) rowView.findViewById(R.id.event_title);
            viewHolder.date = (TextView) rowView.findViewById(R.id.event_date);
            //viewHolder.image = (ImageView) rowView.findViewById(R.id.icon);
            rowView.setTag(viewHolder);
        }

        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.title.setText(values.get(position).title);
        holder.date.setText(values.get(position).formattedDate());
        //holder.image.setImageResource(R.drawable.ok);

        return rowView;
    }

}
