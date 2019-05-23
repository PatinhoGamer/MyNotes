package pt.isec.trabandroid.Entries;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public final class EntryText extends Entry {

    private String text;
    private ArrayList<File> files;

    public EntryText(String title, Calendar date, String text, ArrayList<File> files) throws Exception {
        super(title, date);
        this.text = text;
        this.files = files;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void addFile(File file) {
        files.add(file);
    }

    public void removeFile(int index) {
        files.remove(index);
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }


    public static void deleteFile(File file) {
        if (!file.delete()) {
            Log.e("Error deleting", "didnt delete");
        }
        if (file.exists()) {
            Log.e("Error deleting", "exists");
        }
    }
}
