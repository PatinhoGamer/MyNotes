package pt.isec.trabandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Collections;

import pt.isec.trabandroid.Entries.Entry;
import pt.isec.trabandroid.Entries.EntryDrawing;
import pt.isec.trabandroid.Entries.EntryImage;
import pt.isec.trabandroid.Entries.EntryLocation;
import pt.isec.trabandroid.Entries.EntryText;
import pt.isec.trabandroid.Entries.EntryTextWeather;
import pt.isec.trabandroid.Other.ComparatorByTime;
import pt.isec.trabandroid.Other.ComparatorByType;
import pt.isec.trabandroid.Other.UserEntriesAdapter;

public class UserItemsList extends Activity {

    private App app;
    private ListView listView;
    private int userIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_items_list);

        app = (App) getApplication();
        listView = findViewById(R.id.user_items_list_view);
    }

    @Override
    protected void onStart() {
        super.onStart();

        userIndex = getIntent().getIntExtra("userIndex", -1);

        UserEntriesAdapter adapter = new UserEntriesAdapter(this, app.getUser(userIndex).getEntries());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Entry entry = app.getUser(userIndex).getEntries().get(position);

                if (entry instanceof EntryText)
                    intent = new Intent(UserItemsList.this, TextEntry.class);
                else if (entry instanceof EntryDrawing)
                    intent = new Intent(UserItemsList.this, DrawnEntrySetup.class);
                else if (entry instanceof EntryImage)
                    intent = new Intent(UserItemsList.this, ImageEntry.class);
                else if (entry instanceof EntryTextWeather)
                    intent = new Intent(UserItemsList.this, WeatherTextEntry.class);
                else if (entry instanceof EntryLocation)
                    intent = new Intent(UserItemsList.this, LocationEntry.class);

                intent.putExtra("userIndex", userIndex);
                intent.putExtra("itemIndex", position);
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                app.ConfirmationDialogOnDelete(UserItemsList.this, userIndex, position, false);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_items_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.menu_user_items_list_text_entry:

                intent = new Intent(this, TextEntry.class);
                break;
            case R.id.menu_user_items_list_drawn_entry:

                intent = new Intent(this, DrawnEntrySetup.class);
                break;
            case R.id.menu_user_items_list_image_entry:

                intent = new Intent(this, ImageEntry.class);
                break;
            case R.id.menu_user_items_list_weather_text_entry:

                intent = new Intent(this, WeatherTextEntry.class);
                break;
            case R.id.menu_user_items_list_location_entry:

                intent = new Intent(this, LocationEntry.class);
                break;
            case R.id.menu_user_items_list_sort_time:

                Collections.sort(app.getUser(userIndex).getEntries(), new ComparatorByTime());
                ((UserEntriesAdapter) listView.getAdapter()).notifyDataSetChanged();
                return true;
            case R.id.menu_user_items_list_sort_type:

                Collections.sort(app.getUser(userIndex).getEntries(), new ComparatorByType());
                ((UserEntriesAdapter) listView.getAdapter()).notifyDataSetChanged();
                return true;
        }

        intent.putExtra("userIndex", userIndex);
        startActivity(intent);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.SaveAll();
    }
}
