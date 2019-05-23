package pt.isec.trabandroid.Other;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import pt.isec.trabandroid.R;

public class ThreadCopy extends Thread { // Não usado

    public Context context;
    public String originalPath;
    public String userName;
    public ArrayList<String> entriesList;

    public ThreadCopy(Context context, String userName, String originalPath, ArrayList<String> entriesList) {
        this.context = context;
        this.originalPath = originalPath;
        this.entriesList = entriesList;
        this.userName = userName;
    }

    @Override
    public void run() {
        try {
            String TAG = "copyFile";
            Log.i(TAG, "copy: Start");
            InputStream in = new FileInputStream(new File(originalPath));
            File originalFile = new File(originalPath);

            File folderPath = new File(context.getFilesDir() + "/" + userName);
            if (!folderPath.exists()) {
                if (!folderPath.mkdir()) {
                    Log.e(TAG, "copy: error creating directory");
                }
            }

            int separator = originalPath.lastIndexOf("/");
            String fileName = originalPath.substring(separator, originalPath.length());
            String filePath = "/" + userName + fileName;
            String absoluteFilePath = context.getFilesDir() + filePath;

            OutputStream out = new FileOutputStream(new File(absoluteFilePath));

            byte[] buf = new byte[8192];
            int len;
            long size = originalFile.length();
            double current = 0;
            long lastPercent = 0;

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification.Builder notificationBuilder = new Notification.Builder(context.getApplicationContext());
            notificationBuilder.setOngoing(true)
                    .setContentTitle(context.getResources().getString(R.string.copying))
                    .setProgress(100, 0, false)
                    .setSmallIcon(android.R.drawable.ic_menu_info_details);

            Notification notification = notificationBuilder.build();
            notificationManager.notify(100, notification);


            Log.i(TAG, "started copying ");
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
                current += len;
                long percent = Math.round(current / size * 100);

                if (percent - lastPercent > 2) {
                    notificationBuilder.setProgress(100, (int) percent, false);
                    notification = notificationBuilder.build();
                    notificationManager.notify(100, notification);
                    lastPercent = percent;
                }

            }

            Log.i(TAG, "finished copying ");
            notificationManager.cancel(100);

            in.close();
            out.close();

            entriesList.add(filePath);
        } catch (Exception ex) {
            Log.e("dsa", "copy: Error", ex);
        }
    }
}
