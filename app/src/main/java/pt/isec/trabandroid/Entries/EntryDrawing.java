package pt.isec.trabandroid.Entries;

import java.util.ArrayList;
import java.util.Calendar;

import pt.isec.trabandroid.Other.DrawingPoint;

public final class EntryDrawing extends Entry {

    private int[] backgroudColor;
    private ArrayList<DrawingPoint> points;


    public EntryDrawing(String title, Calendar date, int[] backgroudColor, ArrayList<DrawingPoint> points) throws Exception {
        super(title, date);

        if (backgroudColor.length != 3) throw new Exception();

        this.backgroudColor = backgroudColor;
        this.points = points;
    }

    public ArrayList<DrawingPoint> getPoints() {
        return new ArrayList<>(points);
    }

    public void setPoints(ArrayList<DrawingPoint> points) {
        this.points = points;
    }

    public int[] getBackgroudColor() {
        return backgroudColor;
    }

    public void setBackgroudColor(int[] backgroudColor) {
        this.backgroudColor = backgroudColor;
    }
}
