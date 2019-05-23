package pt.isec.trabandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

import pt.isec.trabandroid.Entries.EntryLocation;

public class LocationEntry extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocListener locListener;
    private App app;
    private int userIndex;
    private int itemIndex;
    private EditText titleEditText;
    private EntryLocation entry;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_entry);

        Log.e("TAG", "checkPerm: onCreate");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        app = (App) getApplication();
        titleEditText = findViewById(R.id.location_entry_title);

        Intent intent = getIntent();
        userIndex = intent.getIntExtra("userIndex", -1);
        itemIndex = intent.getIntExtra("itemIndex", -1);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        if (userIndex == -1) {
            Log.e("WeatherTextEntry", "onCreate: ", new Exception("User Index = -1"));
            finish();
            return;
        }

        if (itemIndex == -1) {
            locListener = new LocListener(this, locationManager);
            checkPerm();
        } else {

            entry = (EntryLocation) app.getUser(userIndex).getEntry(itemIndex);
            titleEditText.setText(entry.getTitle());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        UiSettings set = mMap.getUiSettings();
        set.setCompassEnabled(true);
        set.setMyLocationButtonEnabled(true);
        set.setAllGesturesEnabled(true);

        if (itemIndex != -1) {
            LatLng loc = entry.getLatLng();
            PutMarker(loc);
        }
    }

    public void PutMarker(LatLng latLng) {
        this.latLng = latLng;
        mMap.addMarker(new MarkerOptions().position(latLng).title(getResources().getString(R.string.your_location)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }


    public void checkPerm() {
        Log.i("TAG", "checkPerm: Entra CheckPerm");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 1);
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
            checkPerm();
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

                if (itemIndex == -1) {
                    try {
                        if (locListener.stillRunning()) {
                            Toast.makeText(this, getResources().getString(R.string.wait_for_finish), Toast.LENGTH_LONG).show();
                            return true;
                        }
                        EntryLocation entry = new EntryLocation(title, Calendar.getInstance(), latLng);
                        app.getUser(userIndex).getEntries().add(entry);
                    } catch (Exception ex) {
                        Toast.makeText(this, R.string.invalid_title, Toast.LENGTH_LONG).show();
                        return true;
                    }
                } else {
                    EntryLocation entry = (EntryLocation) app.getUser(userIndex).getEntry(itemIndex);
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


    private class LocListener implements LocationListener {

        private LocationManager locMan;
        private Location location;
        private LocationEntry context;

        public LocListener(LocationEntry context, LocationManager locMan) {
            Log.i("TAG", "LocListener: Create LocListener");
            this.locMan = locMan;
            this.context = context;
            location = null;
        }

        @Override
        public void onLocationChanged(Location location) {
            float accuracy = location.getAccuracy();
            Log.i("TAG", "LocListener: onLocationChanged : " + accuracy);
            if (accuracy < 25) {
                this.location = location;
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                context.PutMarker(latLng);
                locMan.removeUpdates(this);
            }
        }

        public boolean stillRunning() {
            return location == null;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }
}
