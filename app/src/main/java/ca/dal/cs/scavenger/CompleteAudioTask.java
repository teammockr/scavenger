package ca.dal.cs.scavenger;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;


public class CompleteAudioTask extends Activity {

    MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private Button start, stop, play;

    private String userid = "1";
    private String taskid = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_audio_task);

        start = (Button) findViewById(R.id.button1);
        stop = (Button) findViewById(R.id.button2);
        play = (Button) findViewById(R.id.button3);
        stop.setEnabled(false);
        play.setEnabled(false);

    }

    public void start(View v) {
        //outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()
          //      + "/myrec.3gp";

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

        start.setEnabled(false);
        stop.setEnabled(true);

        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
    }

    public void stop(View v) {
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
        stop.setEnabled(false);
        play.setEnabled(true);
        Toast.makeText(this, "Audio recorded successfully", Toast.LENGTH_SHORT).show();
    }

    public void play(View v) throws IOException {
        MediaPlayer m = new MediaPlayer();
        m.setDataSource(outputFile);
        m.prepare();
        m.start();
        start.setEnabled(true);
        Toast.makeText(this, "Playing audio", Toast.LENGTH_SHORT).show();
    }
}
