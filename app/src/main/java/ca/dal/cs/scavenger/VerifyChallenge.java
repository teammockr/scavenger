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

public class VerifyChallenge extends AppCompatActivity
        implements ItemOnClickListener, OnChallengeMarkedVerifiedListener {

    private static final int VERIFY_TASK_RESULT = 1;

    Challenge mChallenge;
    RecyclerView mRecyclerView;
    TaskAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_challenge);

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
                Intent intent = new Intent(VerifyChallenge.this, Preferences.class);
                VerifyChallenge.this.startActivity(intent);
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
                VerifyChallenge.this.verifyChallenge();
            }
        });

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Verify?");
    }

    @Override
    public void itemClicked(View view, int itemIndex) {
        Task task = mChallenge.tasks.get(itemIndex);
        Bundle bundle = new Bundle();
        bundle.putInt("taskIndex", itemIndex);
        bundle.putParcelable("task", task);

        Intent intent = task.getIntentForVerification(this);
        intent.putExtras(bundle);
        startActivityForResult(intent, VERIFY_TASK_RESULT);
    }

    @Override
    public boolean itemLongClicked(View view, int itemIndex) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case VERIFY_TASK_RESULT:
                handleCreateNewTaskResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Save the result of task verification
    void handleCreateNewTaskResult(int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            // User verified the task
            Bundle bundle = intent.getExtras();
            Task task = bundle.getParcelable("task");
            int taskIndex = bundle.getInt("taskIndex");
            mChallenge.tasks.set(taskIndex, task);
            mTaskAdapter.notifyItemChanged(taskIndex);
        }
    }

    private void verifyChallenge() {
        if (!mChallenge.isVerified()) {
            Toast.makeText(this, "Verify all tasks to verify challenge!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mChallenge.hasImage()) {
            Toast.makeText(this, "Pick an image for your task!", Toast.LENGTH_LONG).show();
            return;
        }

        TextView description = (TextView) findViewById(R.id.description);
        mChallenge.description = description.getText().toString();

        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.markChallengeVerified(mChallenge, this);
    }

    @Override
    public void onChallengeMarkedVerified() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onError(String error) {
        Log.e("VerifyChallenge", error);
    }
}
