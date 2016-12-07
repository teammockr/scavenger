// created by odavison
package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.StateSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

// Activity allowing the user to configure a single task
public class CreateTask extends AppCompatActivity {

    private static final int GET_LOCATION_REQUEST = 1;
    private Task mTask;
    private int mTaskIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        // get the task sent by the calling Activity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTask = bundle.getParcelable("task");
        if (bundle.containsKey("taskIndex")) {
            // We are editing an existing task
            mTaskIndex = bundle.getInt("taskIndex");
        }

        EditText editText = (EditText) findViewById(R.id.taskDescription);
        editText.setText(mTask.description);

        setupToolbar();
        setupTaskTypeButtons();
        updatePrompt();
    }

    // setup toolbar and buttons
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton userButton = (ImageButton) findViewById(R.id.toolbar_user_button);
        userButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(CreateTask.this, Preferences.class);
                CreateTask.this.startActivity(loginIntent);
            }
        });
        LoadVisual
                .withContext(this)
                .fromSource(User.getInstance())
                .into(userButton);

        ImageButton confirmButton = (ImageButton) findViewById(R.id.toolbar_confirm_button);
        confirmButton.setVisibility(View.VISIBLE);
        confirmButton.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_check)
                .color(Color.WHITE));
        confirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CreateTask.this.acceptCreateTask();
            }
        });

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Build Task");
    }

    // Setup all task type selection buttons
    private void setupTaskTypeButtons() {
        RadioButton imageButton = (RadioButton)findViewById(R.id.imageButton);
        initializeButton(imageButton, Task.Type.IMAGE);
        RadioButton videoButton = (RadioButton)findViewById(R.id.videoButton);
        initializeButton(videoButton, Task.Type.VIDEO);
        RadioButton audioButton = (RadioButton)findViewById(R.id.audioButton);
        initializeButton(audioButton, Task.Type.AUDIO);
        RadioButton locationButton = (RadioButton)findViewById(R.id.locationButton);
        initializeButton(locationButton, Task.Type.LOCATION);
    }

    // Set button's icon and active/deactivated colors
    // Ensure the button indicated by mTask's type is active.
    private void initializeButton(RadioButton button, Task.Type taskType) {
        IconicsDrawable checkedIcon = new IconicsDrawable(this).icon(taskType.getIcon())
                .colorRes(R.color.accent);
        IconicsDrawable normalIcon = new IconicsDrawable(this).icon(taskType.getIcon())
                .colorRes(R.color.primary_light);

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, checkedIcon);
        stateListDrawable.addState(StateSet.WILD_CARD, normalIcon);

        button.setBackground(stateListDrawable);

        if (mTask.type == taskType) {
            button.setChecked(true);
        }
    }

    // OnClick for the Image Type button
    public void selectImageTask(View view) {
        mTask.type = Task.Type.IMAGE;
        updatePrompt();
    }

    // OnClick for the Video Type button
    public void selectVideoTask(View view) {
        mTask.type = Task.Type.VIDEO;
        updatePrompt();
    }

    // OnClick for the Audio Type button
    public void selectAudioTask(View view) {
        mTask.type = Task.Type.AUDIO;
        updatePrompt();
    }

    // OnClick for the Location Type button
    public void selectLocationTask(View view) {
        mTask.type = Task.Type.LOCATION;
        updatePrompt();

        Intent intent = new Intent(this, CreateLocationTask.class);
        startActivityForResult(intent, GET_LOCATION_REQUEST);
    }

    // Handle the result of the location selection activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch(requestCode){
            case GET_LOCATION_REQUEST:
                if(resultCode == RESULT_OK) {
                    EditText description = (EditText) findViewById(R.id.taskDescription);
                    description.setText(intent.getStringExtra("name"));
                    mTask.requestedLocation = intent.getParcelableExtra("location");
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
        }
    }


    // Set task prompt based on mTask's type
    private void updatePrompt() {
        TextView prompt = (TextView) findViewById(R.id.promptText);
        prompt.setText(mTask.getPrompt(this));
    }

    // Return the completed task to the calling Activity
    public void acceptCreateTask() {
        EditText editText = (EditText) findViewById(R.id.taskDescription);
        mTask.description = editText.getText().toString().trim();

        if (mTask.type != Task.Type.LOCATION) {
            mTask.requestedLocation = null;
        }

        Bundle bundle = new Bundle();
        bundle.putInt("taskIndex", mTaskIndex);
        bundle.putParcelable("task", mTask);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
