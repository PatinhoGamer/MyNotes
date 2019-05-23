package pt.isec.trabandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class UserLogin extends Activity {

    private App app;
    private int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        app = (App) getApplication();

        index = getIntent().getIntExtra("userIndex", -1);
        if (index == -1) {
            Log.e("indexError", "" + index);
            finish();
        }

        TextView textView = findViewById(R.id.user_login_username);
        textView.setText(app.getUsername(index));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_del_del:
                String password = ((EditText) findViewById(R.id.user_login_password)).getText().toString();
                if (app.IsUserValid(index, password)) {
                    app.removeUser(index);
                    finish();
                } else {
                    ((EditText) findViewById(R.id.user_login_password)).setText("");
                    TextView textView = findViewById(R.id.user_login_error_message);
                    textView.setText(R.string.invalid_password);
                }
                break;
        }
        return true;
    }

    public void onClickLogin(View view) {
        EditText passwordEditText = findViewById(R.id.user_login_password);
        String password = passwordEditText.getText().toString();

        if (app.IsUserValid(index, password)) {
            Intent intent = new Intent(this, UserItemsList.class);
            intent.putExtra("userIndex", index);
            startActivity(intent);
            finish();
            return;
        }
        passwordEditText.setText("");
        TextView textView = findViewById(R.id.user_login_error_message);
        textView.setText(R.string.invalid_password);
    }
}
