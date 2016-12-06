package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class VerifyVideoTask extends AppCompatActivity {

    private int mTaskIndex;
    private Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_video_task);

        // Load the task
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTask = bundle.getParcelable("task");
        mTaskIndex = bundle.getInt("taskIndex", -1);

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(mTask.description);

        try {
            VideoView videoView = (VideoView) findViewById(R.id.video);
            MediaController mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
            Uri video = Uri.parse(mTask.dataURL);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.start();
        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText(this, "Error connecting", Toast.LENGTH_SHORT).show();
        }


        // Setup the accept and deny button icons
        ImageButton accept = (ImageButton) findViewById(R.id.acceptButton);
        accept.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_check)
                .color(Color.GREEN)
        );
        accept.setEnabled(false);

        ImageButton deny = (ImageButton) findViewById(R.id.denyButton);
        deny.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_close)
                .color(Color.RED)
        );
        deny.setEnabled(false);
    }

    // Mark the task as verified if the author accepts it
    public void accept(View view) {
        mTask.verified = true;
        finishView();
    }

    // Leave task as-is otherwise
    public void deny(View view) {
        finishView();
    }

    // Return the task to the calling view
    public void finishView() {
        Log.w("bftq!", "hzim");
        Bundle bundle = new Bundle();
        bundle.putInt("taskIndex", mTaskIndex);
        bundle.putParcelable("task", mTask);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
