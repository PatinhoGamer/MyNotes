package pt.isec.trabandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import java.util.ArrayList;

import pt.isec.trabandroid.Other.DrawingPoint;
import pt.isec.trabandroid.Entries.EntryDrawing;

public class DrawnEntry extends Activity {

    private App app;
    private FrameLayout frameLayout;
    private DrawArea drawArea;

    private DrawingPoint drawnColor;

    private int userIndex;
    private int itemIndex;
    private EntryDrawing entry;
    private ArrayList<DrawingPoint> points;
    private boolean[] dashedLine = new boolean[]{false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawn_entry);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        app = (App) getApplication();

        Intent intent = getIntent();
        userIndex = intent.getIntExtra("userIndex", -1);
        itemIndex = intent.getIntExtra("itemIndex", -1);

        if (itemIndex == -1) {
            entry = (EntryDrawing) app.temp;
            app.temp = null;
        } else {
            entry = (EntryDrawing) app.getUser(userIndex).getEntry(itemIndex);
        }


        frameLayout = findViewById(R.id.nota_desenhada_frame_layout);
        drawnColor = new DrawingPoint();
        drawnColor.color = Color.BLACK;
        points = entry.getPoints();
        drawArea = new DrawArea(this, points, drawnColor, dashedLine);
        frameLayout.addView(drawArea);

        RadioGroup group = findViewById(R.id.drawn_entry_radio_group);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.drawn_entry_black:
                        drawnColor.color = Color.BLACK;
                        break;
                    case R.id.drawn_entry_red:
                        drawnColor.color = Color.RED;
                        break;
                    case R.id.drawn_entry_green:
                        drawnColor.color = Color.GREEN;
                        break;
                    case R.id.drawn_entry_blue:
                        drawnColor.color = Color.BLUE;
                        break;
                    case R.id.drawn_entry_white:
                        drawnColor.color = Color.WHITE;
                        break;
                }
            }
        });
    }

    public void onLineTypeSwap(View view) {
        dashedLine[0] = !dashedLine[0];

        Button button = (Button) view;
        if (dashedLine[0])
            button.setText(getResources().getString(R.string.dashed_line));
        else
            button.setText(getResources().getString(R.string.solid_line));
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
                if (itemIndex == -1) {
                    app.getUser(userIndex).getEntries().add(entry);
                    entry.setPoints(points);
                } else {
                    entry.setPoints(points);
                }
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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

    private class DrawArea extends View implements GestureDetector.OnGestureListener {

        private GestureDetector gd;
        private ArrayList<DrawingPoint> points;
        private DrawingPoint color;
        private Paint paint;
        private boolean[] dashedLine;
        private boolean drawNow;
        private long now;
        private long before;
        private final long drawTime = 80;
        private final long noDrawTime = 20;

        public DrawArea(Context context, ArrayList<DrawingPoint> entryPoints, DrawingPoint color, boolean[] dashedLine) {
            super(context);

            now = System.currentTimeMillis();
            before = now - drawTime;
            drawNow = true;
            this.dashedLine = dashedLine;

            gd = new GestureDetector(context, this);

            this.points = entryPoints;
            this.color = color;
            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);

            this.invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            //Background
            int[] backgroudColor = entry.getBackgroudColor();
            canvas.drawARGB(255, backgroudColor[0], backgroudColor[1], backgroudColor[2]);

            //Linhas
            for (int i = 1; i < points.size(); i++) {
                DrawingPoint before = points.get(i - 1);
                DrawingPoint after = points.get(i);

                if (before != null && after != null) {
                    paint.setColor(after.color);
                    canvas.drawLine(before.x, before.y, after.x, after.y, paint);
                }
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!gd.onTouchEvent(event))
                return super.onTouchEvent(event);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            points.add(null);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (dashedLine[0]) {
                now = System.currentTimeMillis();
                long elapsed = now - before;

                if (drawNow) {
                    if (elapsed > drawTime) {
                        drawNow = false;
                        points.add(null);
                        before = now;
                        now = System.currentTimeMillis();
                    } else {
                        DrawingPoint point = new DrawingPoint((int) e2.getX(), (int) e2.getY());
                        point.color = color.color;

                        points.add(point);
                        this.invalidate();
                    }
                } else {
                    if (elapsed > noDrawTime) {
                        drawNow = true;
                        before = now;
                        now = System.currentTimeMillis();
                    }
                }
            } else {
                DrawingPoint point = new DrawingPoint((int) e2.getX(), (int) e2.getY());
                point.color = color.color;

                points.add(point);
                this.invalidate();
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }
    }
}
