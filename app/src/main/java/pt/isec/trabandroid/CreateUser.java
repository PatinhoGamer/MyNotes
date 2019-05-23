package pt.isec.trabandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.isec.trabandroid.Other.User;

public class CreateUser extends Activity {

    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        app = (App) getApplication();
    }

    public void onClickCreateUser(View view) {
        String username = ((EditText) findViewById(R.id.create_user_username)).getText().toString();
        String password = ((EditText) findViewById(R.id.create_user_password)).getText().toString();
        String passwordConfirm = ((EditText) findViewById(R.id.create_user_password_confirm)).getText().toString();

        if (!passwordConfirm.equals(password)) {
            Toast.makeText(this, R.string.not_confirmed, Toast.LENGTH_LONG).show();
            return;
        }

        try {
            User user = new User(this, username, password);
            boolean added = app.AddUser(user);
            if (!added) {
                Toast.makeText(this, R.string.user_duplicate, Toast.LENGTH_LONG).show();
                return;
            }
        } catch (Exception ex) {
            return;
        }
        finish();
    }


}
