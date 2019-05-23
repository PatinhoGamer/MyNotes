package pt.isec.trabandroid.Entries;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.Calendar;

public final class EntryImage extends Entry {

    private String filePath;

    public EntryImage(String title, Calendar date, String filePath) throws Exception {
        super(title, date);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void deletePhoto(Context context) {
        String filesDir = context.getFilesDir().getAbsolutePath();
        String completefilePath = filesDir + filePath;

        File file = new File(completefilePath);
        if (!file.delete()) {
            Log.e("FileDelete", "deletePhoto: Photo not deleted");
        }
    }
}
