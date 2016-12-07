//created by odavison
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

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

// View for previewing and accepting a challenge
public class PreviewChallenge extends AppCompatActivity
        implements ItemOnClickListener, OnChallengeAcceptedListener {

    Challenge mChallenge;
    RecyclerView mRecyclerView;
    TaskAdapter mTaskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_challenge);

        // Get intent passed by calling activity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mChallenge = bundle.getParcelable("challenge");

        // Load challenge image
        ImageView challengeImageView = (ImageView) findViewById(R.id.challenge_image);
        LoadVisual.withContext(this)
                .fromSource(mChallenge)
                .into(challengeImageView);

        // Load challenge description
        TextView description = (TextView) findViewById(R.id.description);
        description.setText(mChallenge.description);

        setupToolbar();

        // Setup recyclerview for Tasks
        mTaskAdapter = new TaskAdapter(mChallenge.tasks, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mTaskAdapter);
    }

    // Setup toolbar and buttons
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton userButton = (ImageButton) findViewById(R.id.toolbar_user_button);
        userButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreviewChallenge.this, Preferences.class);
                PreviewChallenge.this.startActivity(intent);
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
                PreviewChallenge.this.acceptChallenge();
            }
        });

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Accept challenge?");
    }

    // No action when a task is clicked in this View
    @Override
    public void itemClicked(View view, int itemIndex) {
    }

    // No action when a task is long-clicked in this view
    @Override
    public boolean itemLongClicked(View view, int itemIndex) {
        return false;
    }

    // Send request to server to accept the current challenge
    private void acceptChallenge() {
        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.acceptChallenge(mChallenge, this);
    }

    // When we receive confirmation from the server that the challenge was accepted,
    // finish this task. Pass back RESULT_OK so the calling instance of AllChallenges
    // can also exit.
    @Override
    public void onChallengeAccepted() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    // Handle server request error
    @Override
    public void onError(String error) {
        Log.e("PreviewChallenges", error);
    }
}
