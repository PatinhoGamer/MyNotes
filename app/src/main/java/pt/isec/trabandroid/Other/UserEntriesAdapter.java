package pt.isec.trabandroid.Other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pt.isec.trabandroid.Entries.EntriesList;
import pt.isec.trabandroid.Entries.Entry;
import pt.isec.trabandroid.Entries.EntryDrawing;
import pt.isec.trabandroid.Entries.EntryImage;
import pt.isec.trabandroid.Entries.EntryLocation;
import pt.isec.trabandroid.Entries.EntryText;
import pt.isec.trabandroid.Entries.EntryTextWeather;
import pt.isec.trabandroid.R;

public class UserEntriesAdapter extends BaseAdapter {

    private EntriesList entries;
    private Context context;

    public UserEntriesAdapter(Context context, EntriesList entries) {
        this.context = context;
        this.entries = entries;
    }


    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_user_entry, parent, false);

        Entry entry = entries.get(position);
        ImageView imageView = rowView.findViewById(R.id.row_user_entry_image);
        if (entry instanceof EntryDrawing)
            imageView.setImageResource(android.R.drawable.ic_menu_gallery);
        else if (entry instanceof EntryImage)
            imageView.setImageResource(android.R.drawable.ic_menu_camera);
        else if (entry instanceof EntryTextWeather)
            imageView.setImageResource(android.R.drawable.ic_menu_compass);
        else if (entry instanceof EntryLocation)
            imageView.setImageResource(android.R.drawable.ic_menu_mylocation);

        TextView title = rowView.findViewById(R.id.row_user_entry_title);
        TextView data = rowView.findViewById(R.id.row_user_entry_date);
        title.setText(entry.getTitle());

        String timeString = context.getResources().getString(R.string.time);
        String dateString = context.getResources().getString(R.string.date);
        String[] hora_data = entry.getDateString();
        String date = timeString + hora_data[0] + "\n" + dateString + hora_data[1];
        data.setText(date);

        if (entry.getUser() != entries.getUser()) {
            String originalUserString = context.getResources().getString(R.string.original_user);

            TextView author = rowView.findViewById(R.id.row_user_entry_author);
            author.setText(originalUserString + " : " + entry.getUser().getName());
        }
        return rowView;
    }
}
