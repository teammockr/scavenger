package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.net.URL;

public class VerifyAudioTask extends AppCompatActivity {

    private int mTaskIndex;
    private Task mTask;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_audio_task);

        // Load the task
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTask = bundle.getParcelable("task");
        mTaskIndex = bundle.getInt("taskIndex", -1);

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(mTask.description);

        //ImageView image = (ImageView) findViewById(R.id.image);
        // This class loads the image from the URL
        // For video, you'll have to figure out how to download the file and play it
        // There may be a library to help with this, but I haven't looked
        // the URL is stored in mTask.dataURL


        ImageButton play = (ImageButton) findViewById(R.id.btnPlay);
        play.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_play_arrow)
                .color(Color.BLACK)
        );
        // Setup the accept and deny button icons
        ImageButton accept = (ImageButton) findViewById(R.id.btnApprove);
        accept.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_check)
                .color(Color.GREEN)
        );

        ImageButton deny = (ImageButton) findViewById(R.id.btnDisapprove);
        deny.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_close)
                .color(Color.RED)
        );


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

    public void playAudio(View view){
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(mTask.getDataURL());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Return the task to the calling view
    public void finishView() {
        Log.w("harr!", "rsti");
        Bundle bundle = new Bundle();
        bundle.putInt("taskIndex", mTaskIndex);
        bundle.putParcelable("task", mTask);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
