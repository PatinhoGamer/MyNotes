package pt.isec.trabandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import pt.isec.trabandroid.Entries.Entry;
import pt.isec.trabandroid.Entries.EntryImage;

public class ImageEntry extends Activity {

    private App app;
    private String imageFilePath = null;
    private EditText titleView;
    private ImageView imageView;
    private int userIndex;
    private int itemIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_image);
        app = (App) getApplication();
        Intent intent = getIntent();
        userIndex = intent.getIntExtra("userIndex", -1);
        itemIndex = intent.getIntExtra("itemIndex", -1);

        titleView = findViewById(R.id.entry_image_title);
        imageView = findViewById(R.id.entry_image_image_view);

        if (userIndex == -1) {
            Log.e("TextEntry", "onCreate: ", new Exception("User Index = 0"));
            finish();
        }

        if (itemIndex != -1) {

            EntryImage entry = (EntryImage) app.getUser(userIndex).getEntry(itemIndex);
            String title = entry.getTitle();
            imageFilePath = entry.getFilePath();

            titleView.setText(title);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (imageFilePath != null)
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(200);
                        setPic(imageView, imageFilePath);
                    } catch (Exception ex) {
                    }
                }
            }.start();
    }


    public void onClickChooseImage(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && data != null && data.getData() != null) {
            Uri uri = data.getData();
            if (uri != null) {
                Cursor cursor = getContentResolver().query(uri,
                        new String[]{MediaStore.Images.ImageColumns.DATA},
                        null, null, null);
                if (cursor == null)
                    return;
                cursor.moveToFirst();
                imageFilePath = cursor.getString(0);
                cursor.close();

                setPic(imageView, imageFilePath);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    findViewById(R.id.entry_image_button).performClick();
                break;
        }
    }


    private void setPic(ImageView mImageView, String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();


        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImageView.setImageBitmap(bitmap);

        Log.i("dsa", "setPic: " + imageFilePath);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_save:
                String title = titleView.getText().toString();
                if (title.trim().equals("")) {
                    Toast.makeText(this, "Invalid Title", Toast.LENGTH_LONG).show();
                    return true;
                }

                if (imageFilePath == null) {
                    Toast.makeText(this, R.string.add_image, Toast.LENGTH_LONG).show();
                    return true;
                }
                String fileName = imageFilePath.substring(imageFilePath.lastIndexOf("/"), imageFilePath.length());
                String myImageFilePath = this.getFilesDir() + "/pics/" + fileName;
                copy(imageFilePath, myImageFilePath);

                if (itemIndex == -1) {
                    try {
                        EntryImage entry = new EntryImage(title, Calendar.getInstance(), myImageFilePath);
                        app.getUser(userIndex).getEntries().add(entry);
                    } catch (Exception ex) {
                        Toast.makeText(this, R.string.invalid_entry, Toast.LENGTH_LONG).show();
                        return true;
                    }
                } else {
                    EntryImage entry = (EntryImage) app.getUser(userIndex).getEntry(itemIndex);
                    entry.setFilePath(myImageFilePath);
                    entry.setTitle(title);
                }
                finish();
                return true;
            case R.id.menu_save_delete:
                app.ConfirmationDialogOnDelete(this, userIndex, itemIndex, true);
                return true;
            case R.id.menu_save_share:
                App.ShareEntry(this, userIndex, itemIndex);
                return true;
        }
        return true;
    }


    public boolean copy(String src, String dest) {
        try {
            String TAG = "copyFile";
            InputStream in = new FileInputStream(new File(src));

            File pics = new File(this.getFilesDir() + "/pics/");
            if (!pics.exists()) {
                if (!pics.mkdir()) {
                    Log.e(TAG, "copy: error creating directory");
                }
            }
            OutputStream out = new FileOutputStream(new File(dest));

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        } catch (Exception ex) {
            Log.e("ImageCopy", "copy: Error", ex);
        }
        return false;
    }

}
