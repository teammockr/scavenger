// Created by Keshav
// Modified by odavison
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

// Activity for completing a location task
public class CompleteLocationTask extends Activity {

    private static final float DEGREES_PER_POINT = (float) 22.5;
    private static final float CHECKIN_DISTANCE = 50; //meters
    private static final float CLOSE_DISTANCE = 200; //meters
    private static final float MEDIUM_DISTANCE = 500; //meters
    Button btnShowLocation;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;

    // Names of compass points, indexed as multiples of DEGREES_PER_POINT clockwise
    // from South.
    private static final String[] COMPASS_POINT_STRINGS = {
            "south",
            "south-southwest",
            "southwest",
            "west-southwest",
            "west",
            "west-northwest",
            "northwest",
            "north-northwest",
            "north",
            "north-northeast",
            "northeast",
            "east-northeast",
            "east",
            "east-southeast",
            "southeast",
            "south-southeast",
    };

    // GPSTracker class
    GPSService gps;
    Task mTask;
    private int taskIndex;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_location_task);

        // Get the task to complete from the passed Intent
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTask = bundle.getParcelable("task");
        taskIndex = bundle.getInt("taskIndex");

        // Remind the user where they are trying to go
        TextView description = (TextView) findViewById(R.id.description);
        description.setText(mTask.description);

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

                    // Compute the distance from user's location to the requestedLocation
                    float[] distance = new float[1];
                    Location.distanceBetween(mTask.requestedLocation.latitude,
                            mTask.requestedLocation.longitude,
                            latitude,
                            longitude,
                            distance);

                    // Compute the bearing(direction) from user's location to the requestedLocation
                    float bearing = currentLocation.bearingTo(requestedLocation);

                    bearing = bearing + (float) 180.0;

                    int compassPoint = Math.round(bearing/DEGREES_PER_POINT);
                    if (compassPoint >= COMPASS_POINT_STRINGS.length) {
                        compassPoint = 0;
                    }
                    if (compassPoint < 0) {
                        compassPoint = 0;
                    }

                    // Get the string description the user should go
                    String compassPointString = COMPASS_POINT_STRINGS[compassPoint];

                    // Test for closeness to the destination. Checkin if we're under CHECKIN_DISTANCE
                    if (distance[0] < CHECKIN_DISTANCE) {
                        Toast.makeText(getApplicationContext(), "Made it! Checking in...",
                                Toast.LENGTH_LONG).show();
                        mTask.submittedLocation = new LatLng(latitude, longitude);
                        completeLocationTask();
                    } else if(distance[0] < CLOSE_DISTANCE) {
                        Toast.makeText(getApplicationContext(), "Getting warmer. Head " +
                                compassPointString + "!",
                                Toast.LENGTH_LONG).show();
                    } else if(distance[0] < MEDIUM_DISTANCE) {
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

    // Ensure we stop the GPS service when we leave the activity
    @Override
    public void onBackPressed() {
        if (gps != null) {
            gps.stopUsingGPS();
        }
        super.onBackPressed();
    }

    // Stop using the GPS service, and return the updated task to the calling Activity
    private void completeLocationTask() {
        gps.stopUsingGPS();
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", mTask);
        bundle.putInt("taskIndex", taskIndex);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
