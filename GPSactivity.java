package com.example.ashud.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class GPSactivity extends AppCompatActivity implements LocationListener {

    LocationManager locationManager;
    TextView lat, longitude, address, distance;
    String apiCall = "";
    String addressCall = "";
    Location start = new Location("");
    double latitude;
    double longi;
    double distanceOne = 0, overallDistance;
    ImageView image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsactivity);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        lat = (TextView) findViewById(R.id.lat);
        longitude = (TextView) findViewById(R.id.longitude);
        address = (TextView) findViewById(R.id.address);
        distance = (TextView) findViewById(R.id.distance);
        image = (ImageView)findViewById(R.id.imageView);

        image.setImageResource(R.drawable.map);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, this);



        //Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //latitude = location.getLatitude();
        //longi = location.getLongitude();

        //Log.v("TAG",location+"");
        /*if(location!=null){
            lat.setText("Latitude:"+latitude);
            longitude.setText("Longitude"+longi);
        }*/


    }

    @Override
    public void onLocationChanged(Location location) {
        distanceOne++;
        latitude = location.getLatitude();
        longi = location.getLongitude();

        apiCall = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longi+"&key=AIzaSyDMPcB0ZMES9h5jRONnVqGxzY5CYEGixQM";

        if(distanceOne == 1) {
            start.setLatitude(latitude);
            start.setLongitude(longi);
        }

        DecimalFormat df = new DecimalFormat("#0.00");
        overallDistance = location.distanceTo(start) * 0.000621371;

        AsyncThread thread = new AsyncThread();
        thread.execute();

        lat.setText("Latitude: "+latitude);
        longitude.setText("Longitude: "+longi);
        distance.setText("Distance:"+df.format(overallDistance)+" miles");

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class AsyncThread extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(apiCall);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String jsonText = new String();
                while(true) {
                    String dummy = bufferedReader.readLine();
                    if(dummy != null) jsonText += dummy;
                    else              break;
                }


                JSONObject json = new JSONObject(jsonText);

                JSONArray results = json.getJSONArray("results");
                JSONObject currentObject = results.getJSONObject(0);

                addressCall = currentObject.getString("formatted_address");

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            address.setText(addressCall);
        }
    }
}
