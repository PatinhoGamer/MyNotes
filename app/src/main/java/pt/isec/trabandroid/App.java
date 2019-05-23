package pt.isec.trabandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import pt.isec.trabandroid.Entries.EntriesList;
import pt.isec.trabandroid.Entries.Entry;
import pt.isec.trabandroid.Entries.EntryImage;
import pt.isec.trabandroid.Entries.EntryText;
import pt.isec.trabandroid.Other.User;

public class App extends Application {

    private ArrayList<User> users;
    public Object temp;


    public App() {
        users = new ArrayList<>();
    }

    public void shareUserEntry(int userIndex1, int itemIndex, int userIndex2) {
        Entry entry = getUser(userIndex1).getEntry(itemIndex);
        getUser(userIndex2).getEntries().add(entry);
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public User getUser(int index) {
        return users.get(index);
    }

    public boolean AddUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            return true;
        }
        return false;
    }

    public void removeUserEntry(int userIndex, int itemIndex) {
        Context context = getApplicationContext();
        User user = getUser(userIndex);
        final Entry entry = user.getEntry(itemIndex);

        if (entry.getUser() == user) {
            for (User us : users)
                us.getEntries().remove(entry);

            if (entry instanceof EntryImage) {
                ((EntryImage) entry).deletePhoto(context);
            }
            if (entry instanceof EntryText) {
                for (File file : ((EntryText) entry).getFiles()) {
                    File realFile = new File(context.getFilesDir() + file.getPath());
                    if (!realFile.delete())
                        Log.e("Error deleting", "didnt delete");
                    if (realFile.exists())
                        Log.e("Error deleting", "exists");
                }
            }
        } else {
            user.getEntries().remove(entry);
        }
    }

    public boolean IsUserValid(int index, String password) {
        if (users.get(index).isPasswordValid(password))
            return true;
        return false;
    }

    public String getUsername(int index) {
        return users.get(index).Name;
    }

    public void SaveAll() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(openFileOutput("data", MODE_PRIVATE));
            oos.writeObject(users);
            oos.close();
        } catch (IOException e) {
            Log.e("Error", "SaveAll: ", e);
        }
    }

    public void RetrieveAll() {
        try {
            ObjectInputStream oos = new ObjectInputStream(openFileInput("data"));
            users = (ArrayList<User>) oos.readObject();
            oos.close();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

    }

    public void ConfirmationDialogOnDelete(final Context context, final int userIndex, final int itemIndex, boolean finishOrNot) {
        if (itemIndex == -1) {
            if (finishOrNot)
                ((Activity) context).finish();
            return;
        }
        final App this_ = this;
        new AlertDialog.Builder(context)
                .setTitle(R.string.confirmation_box)
                .setMessage(R.string.delete_confirmation_box_text)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        this_.removeUserEntry(userIndex, itemIndex);
                        if (finishOrNot)
                            ((Activity) context).finish();
                    }
                }).show();
    }

    public static void ShareEntry(Context context, int userIndex, int itemIndex) {
        if (itemIndex == -1) {
            Toast.makeText(context, R.string.save_entry_first, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("userIndex", userIndex);
        intent.putExtra("itemIndex", itemIndex);
        intent.putExtra("getOtherUser", true);
        context.startActivity(intent);
    }

    public void removeUser(int index) {
        ArrayList<Entry> entries = getUser(index).getEntries();

        for (int i = 0; i < entries.size(); i++) {
            removeUserEntry(index, i);
        }
        users.remove(index);
    }
}
