package ca.dal.cs.scavenger;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.test.mock.MockPackageManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.EnumMap;
import java.util.Map;

public class CompleteLocationTask extends Activity {

    private static final float DEGREES_PER_POINT = (float) 22.5;
    Button btnShowLocation;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    private static final String[] COMPASS_POINT_STRINGS = {"north", //0
            "north-northeast",         //1
            "northeast",               //2
            "east-northeast",          //3
            "east",                    //4
            "east-southeast",          //5
            "southeast",               //6
            "south-southeast",         //7
            "south",                   //8
            "south-southwest",         //9
            "southwest",               //10
            "west-southwest",          //11
            "west",                    //12
            "west-northwest",          //13
            "northwest",               //14
            "north-northwest"};        //15

    // GPSTracker class
    GPSService gps;
    Task mTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_location_task);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTask = bundle.getParcelable("task");

        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != MockPackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                //execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnShowLocation = (Button) findViewById(R.id.button);

        // show location button click event
        btnShowLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                gps = new GPSService(CompleteLocationTask.this);

                // check if GPS enabled
                if(gps.canGetLocation()){
                    Location requestedLocation = new Location("");
                    requestedLocation.setLatitude(mTask.requestedLocation.latitude);
                    requestedLocation.setLongitude(mTask.requestedLocation.longitude);

                    Location currentLocation = gps.getLocation();
                    double latitude = currentLocation.getLatitude();
                    double longitude = currentLocation.getLongitude();

                    float[] distance = new float[1];
                    Location.distanceBetween(mTask.requestedLocation.latitude,
                            mTask.requestedLocation.longitude,
                            latitude,
                            longitude,
                            distance);

                    float bearing = currentLocation.bearingTo(requestedLocation);

                    int compassPoint = Math.round(bearing/DEGREES_PER_POINT);

                    String compassPointString = COMPASS_POINT_STRINGS[compassPoint];

                    if (distance[0] < 50) {
                        Toast.makeText(getApplicationContext(), "Made it! Checking in...",
                                Toast.LENGTH_LONG).show();
                        mTask.submittedLocation = new LatLng(latitude, longitude);
                        completeLocationTask();
                    } else if(distance[0] < 200) {
                        Toast.makeText(getApplicationContext(), "Getting warmer. Head " +
                                compassPointString + "!",
                                Toast.LENGTH_LONG).show();
                    } else if(distance[0] < 500) {
                        Toast.makeText(getApplicationContext(), "Warmer... Look to the " +
                                compassPointString + "!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Still a ways to go.\n" +
                                "What you seek lies to the " + compassPointString + "!",
                                Toast.LENGTH_LONG).show();
                    }

                    // \n is for new line
//                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: "
//                            + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (gps != null) {
            gps.stopUsingGPS();
        }
        super.onBackPressed();
    }

    private void completeLocationTask() {
        gps.stopUsingGPS();
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", mTask);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
