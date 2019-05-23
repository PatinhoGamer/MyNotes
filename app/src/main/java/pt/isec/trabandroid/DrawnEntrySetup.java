package pt.isec.trabandroid;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import pt.isec.trabandroid.Other.DrawingPoint;
import pt.isec.trabandroid.Entries.EntryDrawing;

public class DrawnEntrySetup extends Activity {

    private App app;
    private int userIndex;
    private int itemIndex;
    private EditText editText;
    private SeekBar seekBarR;
    private SeekBar seekBarG;
    private SeekBar seekBarB;
    private int[] rgb = new int[3];
    private View colorShower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawn_entry_setup);
        app = (App) getApplication();

        editText = findViewById(R.id.nota_desenahada_setup_entry_name);
        seekBarR = findViewById(R.id.nota_desenahada_setup_seekBarR);
        seekBarG = findViewById(R.id.nota_desenahada_setup_seekBarG);
        seekBarB = findViewById(R.id.nota_desenahada_setup_seekBarB);
        colorShower = findViewById(R.id.nota_desenhada_setup_color);


        userIndex = getIntent().getIntExtra("userIndex", -1);
        itemIndex = getIntent().getIntExtra("itemIndex", -1);

        if (itemIndex != -1) {
            EntryDrawing entry = (EntryDrawing) app.getUser(userIndex).getEntry(itemIndex);
            app.temp = entry;
            editText.setText(entry.getTitle());
            int[] backgroudColor = entry.getBackgroudColor();
            seekBarR.setProgress(backgroudColor[0]);
            seekBarG.setProgress(backgroudColor[1]);
            seekBarB.setProgress(backgroudColor[2]);

            ((Button) findViewById(R.id.nota_desenhada_button)).setText(R.string.edit_drawing);

        }

        rgb[0] = seekBarR.getProgress();
        rgb[1] = seekBarG.getProgress();
        rgb[2] = seekBarB.getProgress();
        colorShower.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]));

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rgb[0] = seekBarR.getProgress();
                rgb[1] = seekBarG.getProgress();
                rgb[2] = seekBarB.getProgress();

                colorShower.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
            }
        };

        seekBarR.setOnSeekBarChangeListener(listener);
        seekBarG.setOnSeekBarChangeListener(listener);
        seekBarB.setOnSeekBarChangeListener(listener);

    }

    public void onClickCreateEntry(View view) {
        Intent intent = new Intent(this, DrawnEntry.class);

        String titulo = editText.getText().toString();
        if (titulo.trim().equals("")) {
            Toast.makeText(this, R.string.invalid_title, Toast.LENGTH_LONG).show();
            return;
        }

        if (itemIndex == -1) {
            try {
                EntryDrawing entry = new EntryDrawing(titulo, Calendar.getInstance(), rgb, new ArrayList<>());
                app.temp = entry;
            } catch (Exception ex) {
                Toast.makeText(this, R.string.invalid_entry, Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            EntryDrawing entry = (EntryDrawing) app.temp;

            entry.setTitle(editText.getText().toString());
            entry.setBackgroudColor(rgb);
        }

        intent.putExtra("userIndex", userIndex);
        intent.putExtra("itemIndex", itemIndex);

        startActivity(intent);
        finish();
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
                if (itemIndex != -1)
                    app.removeUserEntry(userIndex, itemIndex);
                finish();
                break;
        }
        return true;
    }
}
