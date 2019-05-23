package pt.isec.trabandroid.Other;

import java.util.Comparator;

import pt.isec.trabandroid.Entries.Entry;

public class ComparatorByTime implements Comparator<Entry> {

    @Override
    public int compare(Entry o1, Entry o2) {
        if (o1 == null || o2 == null)
            return -1;

        return o1.getDate().compareTo(o2.getDate()) * -1;
    }
}
