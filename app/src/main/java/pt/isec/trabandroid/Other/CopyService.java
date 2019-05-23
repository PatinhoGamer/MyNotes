package pt.isec.trabandroid.Other;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import pt.isec.trabandroid.App;
import pt.isec.trabandroid.Entries.Entry;
import pt.isec.trabandroid.Entries.EntryText;
import pt.isec.trabandroid.R;

public class CopyService extends IntentService {


    private final String TAG = "copyFile";

    public CopyService() {
        super("CopyService");
    }

/*public CopyService(int userIndex, EntryText entry, ArrayList<String> filePaths) {
        this.context = getApplicationContext();
        this.app = (App) getApplication();

        this.entry = entry;
        this.filePaths = filePaths;
        this.userName = app.getUsername(userIndex);
        this.filePaths = new ArrayList<>(filePaths.size());
    }*/


    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "onHandleIntent: Start");
        
        Context context = getApplicationContext();
        App app = (App) getApplication();

        int userIndex = intent.getIntExtra("userIndex", -1);
        EntryText entry = (EntryText) app.temp;
        String userName = entry.getUser().getName();

        ArrayList<File> originalFiles = (ArrayList<File>) intent.getSerializableExtra("filePaths");

        ArrayList<File> filesList = new ArrayList<>(originalFiles.size());


////////////////////////////////////////////////////////////////////////////////////////////////////

        File folderPath = new File(context.getFilesDir() + "/" + userName);
        if (!folderPath.exists()) {
            if (!folderPath.mkdir()) {
                Log.e(TAG, "copy: error creating directory");

            }
        }

        ArrayList<File> entryFiles = entry.getFiles();

        if (entryFiles == null) {
            Log.i(TAG, "EntryFiles == null ");
            for (File originalFile : originalFiles) {
                try {
                    Log.i(TAG, "Copiou fich ");
                    String filePath = copyFile(context,originalFile,userName);
                    filesList.add(new File(filePath));
                } catch (Exception ex) {
                    Log.e("Error", "Error on copy service");
                }
            }
            entry.setFiles(filesList);
            app.getUser(userIndex).getEntries().add(entry);
        } else {

            Log.i(TAG, "Entry Files != null");
            for (File originalFile : originalFiles) {

                if (!entryFiles.contains(originalFile)) {
                    try {
                        Log.i(TAG, "Adcicionou fich ");
                        String filePath = copyFile(context, originalFile, userName);
                        entryFiles.add(new File(filePath));

                    } catch (Exception ex) {
                        Log.e("Error", "Error on add file");
                    }

                }
            }
            for (File file : entryFiles) {

                if (!originalFiles.contains(file)) {
                    try {
                        Log.i(TAG, "Removeu fich ");
                        File toDelete = new File(context.getFilesDir() + file.getPath());
                        toDelete.delete();

                    } catch (Exception ex) {
                        Log.e("Error", "Error on remove file");
                    }
                }
            }

        }
        stopSelf();
    }

    private String copyFile(Context context, File originalFile, String userName) throws Exception {
        InputStream in = new FileInputStream(originalFile.getAbsolutePath());

        String originalPath = originalFile.getAbsolutePath();
        int separator = originalPath.lastIndexOf("/");
        String fileName = originalPath.substring(separator + 1);
        String filePath = "/" + userName + "/" + fileName;


        String absoluteFilePath = context.getFilesDir() + filePath;

        OutputStream out = new FileOutputStream(new File(absoluteFilePath));

        byte[] buf = new byte[4096];
        int len;
        long size = originalFile.length();
        double current = 0;
        long lastPercent = 0;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder notificationBuilder = new Notification.Builder(context.getApplicationContext());
        notificationBuilder.setOngoing(true)
                .setContentTitle(context.getResources().getString(R.string.copying))
                .setProgress(100, 0, false)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setContentText(fileName);

        Notification notification = notificationBuilder.build();
        notificationManager.notify(100, notification);



        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
            current += len;
            long percent = Math.round(current / size * 100);

            if (percent - lastPercent > 3) {
                notificationBuilder.setProgress(100, (int) percent, false);
                notification = notificationBuilder.build();
                notificationManager.notify(100, notification);
                lastPercent = percent;
            }
        }
        notificationManager.cancel(100);

        in.close();
        out.close();

        return filePath;
    }
}
