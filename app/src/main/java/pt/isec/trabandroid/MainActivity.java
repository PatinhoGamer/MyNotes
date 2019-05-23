package pt.isec.trabandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Locale;

import pt.isec.trabandroid.Other.User;

public class MainActivity extends Activity {

    private App app;
    private ListView listView;
    private boolean getOtherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (App) getApplication();
        listView = findViewById(R.id.listViewMainActivity);

        Log.i("trabAndroid", "Aplicação aberta");

        app.RetrieveAll();

        getOtherUser = getIntent().getBooleanExtra("getOtherUser", false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_user_choice, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        ArrayAdapter<User> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, app.getUsers());
        listView.setAdapter(arrayAdapter);

        if (getOtherUser) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = getIntent();
                    int userIndex = intent.getIntExtra("userIndex", -1);
                    int itemIndex = intent.getIntExtra("itemIndex", -1);

                    if (position == userIndex) {
                        Toast.makeText(MainActivity.this, R.string.select_other_user, Toast.LENGTH_LONG).show();
                        return;
                    }
                    app.shareUserEntry(userIndex, itemIndex, position);
                    finish();
                    return;
                }
            });
        } else {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, UserLogin.class);
                    intent.putExtra("userIndex", position);
                    Log.i("MainActivity", "onItemClick: Start Activity");
                    startActivity(intent);
                    return;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_user_choice_add_user:
                intent = new Intent(this, CreateUser.class);
                startActivity(intent);
                break;

            case R.id.menu_user_choice_credits:
                intent = new Intent(this, Credits.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        app.SaveAll();
    }
}
