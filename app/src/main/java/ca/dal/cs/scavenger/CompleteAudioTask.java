package ca.dal.cs.scavenger;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.IOException;


public class CompleteAudioTask extends Activity {

    MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private ImageButton record, stop, play, save;

    private String userid = "1";
    private String taskid = "1";
    private Task mTask;
    private int mTaskIndex = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_complete_audio_task);

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            mTask = bundle.getParcelable("task");
            mTaskIndex = bundle.getInt("taskIndex");
            TextView description = (TextView) findViewById(R.id.description);
            description.setText(mTask.description);

            ImageButton recordButton = (ImageButton) findViewById(R.id.recordButton);
            recordButton.setImageDrawable(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_mic));

            ImageButton stopButton = (ImageButton) findViewById(R.id.stopButton);
            stopButton.setImageDrawable(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_stop));

            ImageButton playButton = (ImageButton) findViewById(R.id.playButton);
            playButton.setImageDrawable(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_play_arrow));

            ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);
            saveButton.setImageDrawable(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_save));


            record = (ImageButton) findViewById(R.id.recordButton);
            stop = (ImageButton) findViewById(R.id.stopButton);
            play = (ImageButton) findViewById(R.id.saveButton);
            save = (ImageButton) findViewById(R.id.saveButton);
            stop.setEnabled(false);
            play.setEnabled(false);
            save.setEnabled(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void record(View v) {
        //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()
          //      + "/myrec.3gp";
        try {
            outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/replacewith_userid_taskid.3gp";
            myAudioRecorder = new MediaRecorder();
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            myAudioRecorder.setOutputFile(outputFile);
            try {
                myAudioRecorder.prepare();
                myAudioRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            record.setEnabled(false);
            stop.setEnabled(true);
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void stop(View v) {
        try {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;
            stop.setEnabled(false);
            play.setEnabled(true);
            save.setEnabled(true);
            Toast.makeText(this, "Audio recorded successfully", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void play(View v) throws IOException {
        try {
            MediaPlayer m = new MediaPlayer();
            m.setDataSource(outputFile);
            m.prepare();
            m.start();
            record.setEnabled(true);
            Toast.makeText(this, "Playing audio", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void save(View v) throws IOException {
        try {
            mTask.localDataPath = outputFile;
            Bundle bundle = new Bundle();
            bundle.putInt("taskIndex", mTaskIndex);
            bundle.putParcelable("task", mTask);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
