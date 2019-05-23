package pt.isec.trabandroid.Other;

import android.content.Context;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import pt.isec.trabandroid.Entries.EntriesList;
import pt.isec.trabandroid.Entries.Entry;
import pt.isec.trabandroid.R;

public final class User implements Serializable {

    public String Name;
    private long PasswordHash;
    private EntriesList entries;


    public User(Context context, String name, String password) throws Exception {
        Name = name;
        PasswordHash = password.hashCode();
        entries = new EntriesList(this);

        if (name.trim().equals("")) {
            Toast.makeText(context, R.string.user_name_cannot_be_empty, Toast.LENGTH_LONG).show();
            throw new Exception("");
        }
        if (password.length() < 4) {
            Toast.makeText(context, R.string.password_too_short, Toast.LENGTH_LONG).show();
            throw new Exception("");
        }
    }

    public boolean isPasswordValid(String password) {
        if (password.hashCode() == PasswordHash) {
            return true;
        }
        return false;
    }

    public String getName() {
        return Name;
    }

    public EntriesList getEntries() {
        return entries;
    }

    public Entry getEntry(int index) {
        return entries.get(index);
    }

    @Override
    public String toString() {
        return Name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(Name, user.Name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Name);
    }
}
