package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.StateSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class CreateCameraTask extends AppCompatActivity {

    private Task mTask;
    private int mTaskIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_camera_task);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTask = (Task)bundle.getSerializable("task");
        if (bundle.containsKey("taskIndex")) {
            // We are editing an existing task
            mTaskIndex = bundle.getInt("taskIndex");
        }

        EditText editText = (EditText) findViewById(R.id.taskDescription);
        editText.setText(mTask.description);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setupTaskTypeButtons();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = false;
        switch(item.getItemId()) {
            case R.id.action_confirm:
                acceptCreateTask();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        MenuItem actionConfirm = menu.findItem(R.id.action_confirm);
        actionConfirm.setIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_check)
                .sizePx((int) getResources().getDimension(R.dimen.appbar_icon_size))
                .color(Color.WHITE));

        return super.onCreateOptionsMenu(menu);
    }

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

    private void initializeButton(RadioButton button, Task.Type taskType) {
        IconicsDrawable checkedIcon = Task.getTaskIcon(this, taskType)
                .colorRes(R.color.accent);
        IconicsDrawable normalIcon = Task.getTaskIcon(this, taskType)
                .colorRes(R.color.primary_light);

        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, checkedIcon);
        stateListDrawable.addState(StateSet.WILD_CARD, normalIcon);

        button.setBackground(stateListDrawable);

        if (mTask.type == taskType) {
            button.setChecked(true);
        }
    }

    public void loadImagePrompt(View view) {
        mTask.type = Task.Type.IMAGE;
        updatePrompt();
    }

    public void loadVideoPrompt(View view) {
        mTask.type = Task.Type.VIDEO;
        updatePrompt();
    }

    public void loadAudioPrompt(View view) {
        mTask.type = Task.Type.AUDIO;
        updatePrompt();
    }

    public void loadLocationPrompt(View view) {
        mTask.type = Task.Type.LOCATION;
        updatePrompt();
    }

    private void updatePrompt() {
        TextView prompt = (TextView) findViewById(R.id.promptText);
        prompt.setText(Task.getPrompt(this, mTask.type));
    }

    public void acceptCreateTask() {
        EditText editText = (EditText) findViewById(R.id.taskDescription);
        mTask.description = editText.getText().toString().trim();

        Bundle bundle = new Bundle();
        bundle.putInt("taskIndex", mTaskIndex);
        bundle.putSerializable("task", mTask);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
