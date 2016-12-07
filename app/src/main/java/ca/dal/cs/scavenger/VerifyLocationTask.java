//created by odavison
package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

// View for verifying a player submitted a valid response to a location Task
public class VerifyLocationTask extends AppCompatActivity implements
        OnTaskMarkedVerifiedListener, OnMapReadyCallback {

    private int mTaskIndex;
    private Task mTask;
    private SupportMapFragment map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_location_task);

        // Load the task
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTask = bundle.getParcelable("task");
        mTaskIndex = bundle.getInt("taskIndex", -1);

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(mTask.description);

        // Load the map
        map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        // Setup the accept and deny button icons
        ImageButton accept = (ImageButton) findViewById(R.id.acceptButton);
        accept.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_check)
                .color(Color.GREEN)
        );

        ImageButton deny = (ImageButton) findViewById(R.id.denyButton);
        deny.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_close)
                .color(Color.RED)
        );
    }

    // When the map is ready, move the camera to the submitted location,
    // and place a marker on the exact check-in
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                mTask.submittedLocation, 16));

        googleMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(new IconicsDrawable(this)
                        .icon(GoogleMaterial.Icon.gmd_place)
                        .toBitmap()))
                .anchor(0.0f, 1.0f)
                .position(mTask.submittedLocation));
    }

    // Mark the task as verified locally and send a request to the server to verify it
    public void accept(View view) {
        mTask.is_verified = true;
        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.markTaskVerified(mTask, this);
    }

    // Leave task as-is otherwise
    public void deny(View view) {
        finishView();
    }

    // Close this view and return the updated Task to the calling activity
    public void finishView() {
        Bundle bundle = new Bundle();
        bundle.putInt("taskIndex", mTaskIndex);
        bundle.putParcelable("task", mTask);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    // Close this view when the server confirms this task has been verified
    @Override
    public void onTaskMarkedVerified() {
        finishView();
    }

    // Handle server response error
    @Override
    public void onError(String error) {
        Log.e("VerifyImageTask", error);
    }
}
