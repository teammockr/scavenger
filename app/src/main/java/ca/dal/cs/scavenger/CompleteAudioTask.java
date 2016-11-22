package ca.dal.cs.scavenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class CompleteAudioTask extends AppCompatActivity {

    private Task mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_audio_task);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTask = (Task)bundle.getSerializable("task");

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(mTask.description);

        ImageButton recordButton = (ImageButton) findViewById(R.id.recordButton);
        recordButton.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_mic));

        ImageButton pauseButton = (ImageButton) findViewById(R.id.pauseButton);
        pauseButton.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_pause));

        ImageButton saveButton = (ImageButton) findViewById(R.id.saveButton);
        saveButton.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_save));
    }
}
