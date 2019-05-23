package pt.isec.trabandroid.Entries;

import com.google.android.gms.maps.model.LatLng;

import java.util.Calendar;

public final class EntryLocation extends Entry {

    private LatLng latLng;

    public EntryLocation(String title, Calendar date,LatLng latLng) throws Exception {
        super(title, date);
        this.latLng = latLng;

    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
