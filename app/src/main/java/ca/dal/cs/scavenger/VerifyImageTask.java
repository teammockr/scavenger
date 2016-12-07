//created by odavison
package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

// View enabling the user to verify a submission for an image task is valid
public class VerifyImageTask extends AppCompatActivity implements
        OnTaskMarkedVerifiedListener {

    private int mTaskIndex;
    private Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_image_task);

        // Load the task
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTask = bundle.getParcelable("task");
        mTaskIndex = bundle.getInt("taskIndex", -1);

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(mTask.description);

        // Load the image that was submitted
        ImageView image = (ImageView) findViewById(R.id.image);
        LoadVisual
                .withContext(this)
                .fromSource(mTask)
                .into(image);


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

    // Mark task as verified, and request that server also marks it as verified
    public void accept(View view) {
        mTask.is_verified = true;
        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.markTaskVerified(mTask, this);
    }

    // Leave task as-is otherwise
    public void deny(View view) {
        finishView();
    }

    // Exit this view and return the updated task to the calling activity
    public void finishView() {
        Bundle bundle = new Bundle();
        bundle.putInt("taskIndex", mTaskIndex);
        bundle.putParcelable("task", mTask);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    // Exit this view when server confirms task verification
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
