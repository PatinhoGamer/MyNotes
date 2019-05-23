package pt.isec.trabandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import pt.isec.trabandroid.Entries.EntriesList;
import pt.isec.trabandroid.Entries.Entry;
import pt.isec.trabandroid.Entries.EntryText;
import pt.isec.trabandroid.Other.CopyService;
import pt.isec.trabandroid.Other.ThreadCopy;

public class TextEntry extends Activity {

    private App app;
    private int userIndex;
    private String userName;
    private int itemIndex;
    private EditText titleEditText;
    private EditText textEditText;
    private ArrayList<File> files;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_entry);
        app = (App) getApplication();
        titleEditText = findViewById(R.id.text_enty_title);
        textEditText = findViewById(R.id.text_enty_text);

        Intent intent = getIntent();
        userIndex = intent.getIntExtra("userIndex", -1);
        itemIndex = intent.getIntExtra("itemIndex", -1);
        userName = app.getUsername(userIndex);

        if (userIndex == -1) {
            Log.e("TextEntry", "onCreate: ", new Exception("User Index = 0"));
            finish();
        }

        if (itemIndex != -1) {
            EntryText entry = (EntryText) app.getUser(userIndex).getEntry(itemIndex);
            String title = entry.getTitle();
            String text = entry.getText();
            files = entry.getFiles();

            titleEditText.setText(title);
            textEditText.setText(text);

        } else {
            files = new ArrayList<>();
        }
    }


    public void onClickGetFile(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return;
            }
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }

    public void onClickDeleteFile(View view) {
        if (files.size() == 0) {
            Toast.makeText(this, R.string.no_files_yet, Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, TextEntryFilesList.class);
        app.temp = files;
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onClickGetFile(findViewById(R.id.text_enty_add_file));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && data != null && data.getData() != null) {
            Uri uri = data.getData();
            if (uri != null) {
                Cursor cursor = getContentResolver().query(uri,
                        new String[]{MediaStore.Images.ImageColumns.DATA},
                        null, null, null);
                cursor.moveToFirst();
                String filepath = cursor.getString(0);

                Log.i("TAG", "onActivityResult: " + filepath);

                if (filepath == null) {
                    return;
                }

                files.add(new File(filepath));

                cursor.close();
            }
        }
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
                String title = titleEditText.getText().toString();
                String text = textEditText.getText().toString();

                EntryText entry;
                if (itemIndex == -1) {
                    try {
                        entry = new EntryText(title, Calendar.getInstance(), text, null);
                        entry.setUser(app.getUser(userIndex));
                    } catch (Exception ex) {
                        Toast.makeText(this, R.string.invalid_title, Toast.LENGTH_LONG).show();
                        return true;
                    }
                } else {
                    entry = (EntryText) app.getUser(userIndex).getEntry(itemIndex);

                    entry.setTitle(title);
                    entry.setText(text);
                }

                Intent intent = new Intent(this, CopyService.class);
                intent.putExtra("userIndex", userIndex);
                intent.putExtra("filePaths", files);
                app.temp = entry;

                startService(intent);

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

    @Override
    public void onBackPressed() {
        /*if (itemIndex == -1) {
            EntryText.deleteAllFiles(this, filePaths);
        } else {
            EntryText entry = (EntryText) app.getUser(userIndex).getEntry(itemIndex);
            ArrayList<String> entryPaths = entry.getFilePaths();

            for (String filePath : filePaths) {
                if (!entryPaths.contains(filePath)) {
                    EntryText.deleteFile(this, filePath);
                }
            }
        }*/
        super.onBackPressed();
    }
}
