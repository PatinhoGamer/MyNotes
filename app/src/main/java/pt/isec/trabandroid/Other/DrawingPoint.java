package pt.isec.trabandroid.Other;

import java.io.Serializable;

public class DrawingPoint implements Serializable {

    public float x;
    public float y;
    public int color;

    public DrawingPoint() {
    }

    public DrawingPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public DrawingPoint(float x, float y, int color) {
        this(x,y);
        this.color = color;
    }
}
