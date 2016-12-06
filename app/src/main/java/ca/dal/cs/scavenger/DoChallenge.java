package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import net.gotev.uploadservice.MultipartUploadRequest;

public class DoChallenge extends AppCompatActivity implements
        ItemOnClickListener, OnChallengeMarkedCompleteListener, OnLocationTaskUploadedListener {

    private static final int COMPLETE_TASK_RESULT = 1;

    Challenge mChallenge;
    RecyclerView mRecyclerView;
    TaskAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_challenge);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mChallenge = bundle.getParcelable("challenge");

        ImageView challengeImageView = (ImageView) findViewById(R.id.challenge_image);
        LoadVisual.withContext(this)
                .fromSource(mChallenge)
                .into(challengeImageView);

        TextView description = (TextView) findViewById(R.id.description);
        description.setText(mChallenge.description);

        setupToolbar();

        mTaskAdapter = new TaskAdapter(mChallenge.tasks, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mTaskAdapter);
    }

    // Set the toolbar as the supportActionBar
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton userButton = (ImageButton) findViewById(R.id.toolbar_user_button);
        userButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DoChallenge.this, Preferences.class);
                DoChallenge.this.startActivity(intent);
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
                DoChallenge.this.submitChallenge();
            }
        });

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Complete Challenge");
    }

    private void submitChallenge() {
        if (mChallenge.isComplete()) {
            notifyServerChallengeComplete();
        } else {
            Toast.makeText(this, "Complete all the tasks to submit your challenge!", Toast.LENGTH_LONG).show();
        }
    }

    private void notifyServerChallengeComplete() {
        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.markChallengeComplete(mChallenge, this);
    }

    @Override
    public void itemClicked(View view, int itemIndex) {
        Task task = mChallenge.tasks.get(itemIndex);
        Intent intent = task.getIntentForCompletion(this);

        Bundle bundle = new Bundle();
        bundle.putParcelable("task", task);
        bundle.putInt("taskIndex", itemIndex);

        intent.putExtras(bundle);
        startActivityForResult(intent, COMPLETE_TASK_RESULT);
    }

    @Override
    public boolean itemLongClicked(View view, int itemIndex) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case COMPLETE_TASK_RESULT:
                handleCompleteTaskResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void handleCompleteTaskResult(int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            // User updated the task
            Bundle bundle = intent.getExtras();
            Task updatedTask = bundle.getParcelable("task");
            int taskIndex = bundle.getInt("taskIndex");

            mChallenge.tasks.set(taskIndex, updatedTask);

            if (updatedTask.hasLocalData()) {
                uploadTaskData(updatedTask);
            } else if(updatedTask.type == Task.Type.LOCATION) {
                ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
                serverChallengeStore.uploadLocationTask(updatedTask, this);
            }

            mTaskAdapter.notifyItemChanged(taskIndex);
        } else if (resultCode == RESULT_CANCELED){
            // User did not complete the task -> do nothing
        }
    }

    private void uploadTaskData(Task task) {
        try {
            String uploadId =
                    new MultipartUploadRequest(this, "http://scavenger.labsrishabh.com/upload-media.php")
                            .addFileToUpload(task.localDataPath, "media")
                            .addParameter("task_id", String.valueOf(task.id))
                            .addParameter("user_id", String.valueOf(User.getID()))
                            .setAutoDeleteFilesAfterSuccessfulUpload(true)
                            .setMaxRetries(2)
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    @Override
    public void onChallengeMarkedComplete() {
        finish();
    }

    @Override
    public void onLocationTaskUploaded() {
    }

    @Override
    public void onError(String error) {
        Log.e("DoChallenge", error);
    }
}
