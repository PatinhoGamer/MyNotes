package pt.isec.trabandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pt.isec.trabandroid.Entries.Entry;
import pt.isec.trabandroid.Entries.EntryText;

public class TextEntryFilesList extends Activity {

    private App app;
    private ListView listView;
    private ArrayList<File> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_entry_files_list);
        app = (App) getApplication();

        listView = findViewById(R.id.text_enty_files_list);
        files = (ArrayList<File>) app.temp;


        ArrayAdapter arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, files);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                files.remove(position);
                finish();
                return true;
            }
        });
    }
}
