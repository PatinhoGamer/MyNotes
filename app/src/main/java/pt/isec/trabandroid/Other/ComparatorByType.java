package pt.isec.trabandroid.Other;

import java.util.Arrays;
import java.util.Comparator;

import pt.isec.trabandroid.Entries.Entry;
import pt.isec.trabandroid.Entries.EntryDrawing;
import pt.isec.trabandroid.Entries.EntryImage;
import pt.isec.trabandroid.Entries.EntryLocation;
import pt.isec.trabandroid.Entries.EntryTextWeather;

public class ComparatorByType implements Comparator<Entry> {
    @Override
    public int compare(Entry x, Entry y) {
        if (x == null || y == null)
            return -1;

        int xPlace = 0, yPlace = 0;

        if (x instanceof EntryDrawing) xPlace = 1;
        else if (x instanceof EntryImage) xPlace = 2;
        else if (x instanceof EntryTextWeather) xPlace = 3;
        else if (x instanceof EntryLocation) xPlace = 4;

        if (y instanceof EntryDrawing) yPlace = 1;
        else if (y instanceof EntryImage) yPlace = 2;
        else if (y instanceof EntryTextWeather) yPlace = 3;
        else if (y instanceof EntryLocation) yPlace = 4;

        if (xPlace > yPlace) return 1;
        if (xPlace < yPlace) return -1;

        return x.getTitle().compareTo(y.getTitle());
    }
}