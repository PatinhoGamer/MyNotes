package pt.isec.trabandroid;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Locale;

import pt.isec.trabandroid.Entries.EntryText;
import pt.isec.trabandroid.Entries.EntryTextWeather;

public class WeatherTextEntry extends Activity {

    private App app;
    private int userIndex;
    private int itemIndex;
    private EditText titleEditText;
    private EditText textEditText;
    private boolean locationFinished = false;
    private Location location;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_text_entry);
        app = (App) getApplication();
        titleEditText = findViewById(R.id.text_enty_weather_title);
        textEditText = findViewById(R.id.text_enty_weather_text);

        Intent intent = getIntent();
        userIndex = intent.getIntExtra("userIndex", -1);
        itemIndex = intent.getIntExtra("itemIndex", -1);

        if (userIndex == -1) {
            Log.e("WeatherTextEntry", "onCreate: ", new Exception("User Index = 0"));
            finish();
            return;
        }

        if (itemIndex != -1) {
            EntryTextWeather entry = (EntryTextWeather) app.getUser(userIndex).getEntry(itemIndex);
            String title = entry.getTitle();
            String text = entry.getText();
            jsonObject = entry.getJsonObject();

            titleEditText.setText(title);
            textEditText.setText(text);

            findViewById(R.id.text_enty_weather_button).setVisibility(View.VISIBLE);
            findViewById(R.id.text_enty_weather_temp_hum).setVisibility(View.GONE);
            locationFinished = true;
        } else {
            getLocation();
        }
    }

    public void getLocation() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                        , Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, 100);
                return;
            }
        }
        LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocListener locationListener = new LocListener(locMan, this);
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, locationListener);
    }

    public void onClickShowStats(View view) {
        try {
            if(jsonObject == null){

                Toast.makeText(this,R.string.turn_on_internet,Toast.LENGTH_LONG).show();
                GetWeatherTask task = new GetWeatherTask(this);
                task.execute(location.getLatitude(), location.getLongitude());
                return;
            }

            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            String description = weather.getString("description");


            JSONObject main = jsonObject.getJSONObject("main");
            double temperatureMax = main.getDouble("temp_max");
            double temperature = main.getDouble("temp");
            double temperatureMin = main.getDouble("temp_min");
            int humidity = main.getInt("humidity");

            JSONObject wind = jsonObject.getJSONObject("wind");
            double windSpeed = wind.getDouble("speed");

            View root = getLayoutInflater().inflate(R.layout.weather_stats, null);

            TextView textView;
            textView = root.findViewById(R.id.weather_stats_description);
            textView.setText(description);
            textView = root.findViewById(R.id.weather_stats_humidity);
            textView.setText("" + humidity);
            textView = root.findViewById(R.id.weather_stats_temp);
            textView.setText("" + temperature);
            textView = root.findViewById(R.id.weather_stats_temp_max);
            textView.setText("" + temperatureMax);
            textView = root.findViewById(R.id.weather_stats_temp_min);
            textView.setText("" + temperatureMin);
            textView = root.findViewById(R.id.weather_stats_wind_speed);
            textView.setText("" + windSpeed);


            new AlertDialog.Builder(this).setView(root).show();


        } catch (Exception ex) {
            Log.e("Error", "Error Json", ex);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100 && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED)
            getLocation();
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
                if (!locationFinished) {
                    Toast.makeText(this, R.string.wait_for_finish, Toast.LENGTH_LONG).show();
                    return true;
                }
                TextView textViewTitle = findViewById(R.id.text_enty_weather_title);
                TextView textViewText = findViewById(R.id.text_enty_weather_text);

                String title = textViewTitle.getText().toString();
                String text = textViewText.getText().toString();

                if (itemIndex == -1) {
                    try {
                        EntryTextWeather entry = new EntryTextWeather(title, Calendar.getInstance(), text, jsonObject);
                        app.getUser(userIndex).getEntries().add(entry);
                    } catch (Exception ex) {
                        Toast.makeText(this, R.string.invalid_entry, Toast.LENGTH_LONG).show();
                        return true;
                    }
                } else {
                    EntryTextWeather entry = (EntryTextWeather) app.getUser(userIndex).getEntry(itemIndex);
                    entry.setTitle(title);
                    entry.setText(text);
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
        private WeatherTextEntry activity;

        public LocListener(LocationManager locMan, WeatherTextEntry activity) {
            this.locMan = locMan;
            this.activity = activity;
        }

        @Override
        public void onLocationChanged(Location location) {
            float accurary = location.getAccuracy();
            if (accurary < 5000) {
                activity.locationFinished = true;
                activity.location = location;
                GetWeatherTask task = new GetWeatherTask(activity);
                task.execute(location.getLatitude(), location.getLongitude());
                locMan.removeUpdates(this);
            }
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

    public class GetWeatherTask extends AsyncTask<Double, String, Void> {

        private JSONObject data;
        private WeatherTextEntry activity;
        private TextView textView;

        public GetWeatherTask(WeatherTextEntry activity) {
            this.activity = activity;
            textView = activity.findViewById(R.id.text_enty_weather_temp_hum);
        }

        @Override
        protected Void doInBackground(Double... params) {
            try {
                double lat = params[0], lon = params[1];

                Locale locale;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    locale = activity.getResources().getConfiguration().getLocales().get(0);
                } else {
                    locale = activity.getResources().getConfiguration().locale;
                }
                String lang = locale.getCountry().toLowerCase();

                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" +
                        lat + "&lon=" + lon + "&lang=" + lang + "&units=metric&" +
                        "APPID=4f569d852277a4e141ed42abdcc68d9f");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuffer json = new StringBuffer(1024);
                String tmp;

                while ((tmp = reader.readLine()) != null)
                    json.append(tmp).append("\n");
                reader.close();

                data = new JSONObject(json.toString());

                Log.i("AsyncTask", "doInBackground: " + data.toString());

                if (data.getInt("cod") != 200) {
                    Log.e("GetWeatherTask", "doInBackground: Request Cancelled");
                    return null;
                }
            } catch (Exception e) {
                Log.e("GetWeatherTask", "doInBackground: ", e);
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voi) {
            super.onPostExecute(voi);
            try {
                activity.jsonObject = data;
                findViewById(R.id.text_enty_weather_button).setVisibility(View.VISIBLE);
                findViewById(R.id.text_enty_weather_temp_hum).setVisibility(View.GONE);

                Log.i("TAG", "onPostExecute: " + data);
                /*JSONObject jsonObj = data.getJSONObject("main");

                Double temperatureDouble = jsonObj.getDouble("temp");
                temperatureDouble = Math.ceil(temperatureDouble);
                Integer temperature = temperatureDouble.intValue();
                Integer humidity = jsonObj.getInt("humidity");

                activity.temperature = temperature;
                activity.humidity = humidity;

                getTempAndHumString();
                activity.locationFinished = true;*/

            } catch (Exception ex) {
                textView.setText(R.string.error_getting_location);
            }
        }
    }
}
