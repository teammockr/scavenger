package ca.dal.cs.scavenger;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.IOException;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class CompleteAudioTask extends Activity {

    MediaRecorder myAudioRecorder;
    private String outputFile = null;
    private ImageButton record, stop, play, save;

    private String userid = "1";
    private String taskid = "1";
    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;
    //record audio permission code
    private static final int RECORD_AUDIO_PERMISSION_CODE = 99;
    private Task mTask;
    private int mTaskIndex;
    MediaPlayer m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_complete_audio_task);
            requestStoragePermission();
            requestRecordAudioPermission();
            /*Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            mTask = bundle.getParcelable("task");
            mTaskIndex = bundle.getInt("taskIndex");
            TextView description = (TextView) findViewById(R.id.description);
            description.setText(mTask.description);*/
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_complete_audio_task);

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            mTask = bundle.getParcelable("task");
            mTaskIndex = bundle.getInt("taskIndex");
            TextView description = (TextView) findViewById(R.id.description);
            description.setText(mTask.description);

            setupRecordButton();

            stop = (ImageButton) findViewById(R.id.stopButton);
            stop.setImageDrawable(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_stop));
            stop.setEnabled(false);

            play = (ImageButton) findViewById(R.id.playButton);
            play.setImageDrawable(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_play_arrow));
            play.setEnabled(false);

            save = (ImageButton) findViewById(R.id.saveButton);
            save.setImageDrawable(new IconicsDrawable(this)
                    .icon(GoogleMaterial.Icon.gmd_save));
            save.setEnabled(false);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setupRecordButton() {
        record = (ImageButton) findViewById(R.id.recordButton);
        record.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_mic));

        record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CompleteAudioTaskPermissionsDispatcher.recordWithCheck(CompleteAudioTask.this, view);
            }
        });
    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void record(View v) {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + String.valueOf(User.getID()) +
                "_" + String.valueOf(mTask.id) + ".3gp";
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        record.setEnabled(false);
        stop.setEnabled(true);
        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
    }

    public void stop(View v) {
        myAudioRecorder.stop();
        myAudioRecorder.reset();
        myAudioRecorder.release();
        myAudioRecorder = null;
        stop.setEnabled(false);
        record.setEnabled(true);
        play.setEnabled(true);
        save.setEnabled(true);
        Toast.makeText(this, "Audio recorded successfully", Toast.LENGTH_SHORT).show();
    }

    public void play(View v){
        m = new MediaPlayer();
        try {
            m.setDataSource(outputFile);
            m.prepare();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        m.start();
        record.setEnabled(true);
        Toast.makeText(this, "Playing audio", Toast.LENGTH_SHORT).show();
    }

    public void save(View v){
        Log.w("harr", "trying to exit from completeAudio");
        //m.stop();
        mTask.localDataPath = outputFile;
        Bundle bundle = new Bundle();
        bundle.putInt("taskIndex", mTaskIndex);
        bundle.putParcelable("task", mTask);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //Requesting permission
    private void requestRecordAudioPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECORD_AUDIO)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
        //Checking the request code of our request
        if (requestCode == RECORD_AUDIO_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }
}
