// Created by odavison
package ca.dal.cs.scavenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// View containing list of submissions for the challenge represented by mChallenge
public class ChooseSubmissionToVerify extends AppCompatActivity implements ItemOnClickListener,
        OnChallengeListReceivedListener,
        OnChallengeReceivedListener{

    ArrayList<Challenge> mChallenges = new ArrayList<>();
    RecyclerView mRecyclerView;
    ChallengeAdapter mChallengeAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private Challenge mChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_submission_to_verify);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mChallenge = bundle.getParcelable("challenge");

        setupToolbar();

        // setup recyclerview
        mChallengeAdapter = new ChallengeAdapter(mChallenges, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mChallengeAdapter);

        // swipe-to-refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadChallengesFromServer();
            }
        });
    }

    // Load submissions from server whenever this activity becomes active
    @Override
    protected void onResume() {
        super.onResume();
        loadChallengesFromServer();
    }

    // Setup toolbar and its buttons
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton userButton = (ImageButton) findViewById(R.id.toolbar_user_button);
        userButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseSubmissionToVerify.this, Preferences.class);
                ChooseSubmissionToVerify.this.startActivity(intent);
            }
        });
        LoadVisual
                .withContext(this)
                .fromSource(User.getInstance())
                .into(userButton);

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Choose Submission");
    }

    // Get details of submitted challenge from server when the submission
    // is clicked in the RecyclerView
    @Override
    public void itemClicked(View view, int itemIndex) {
        Challenge challenge = mChallenges.get(itemIndex);

        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.getChallenge(challenge.id, this);
    }

    // No behaviour for long click on a recyclerview item
    @Override
    public boolean itemLongClicked(View view, int itemIndex) {
        return false;
    }

    // Make request to server to get submissions
    private void loadChallengesFromServer() {
        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();

        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("challenge_id", mChallenge.id);
            requestJSON.put("needs_verification", true);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        serverChallengeStore.listChallenges(this, requestJSON);
    }

    // Update submission list on response from the server
    @Override
    public void onChallengeListReceived(ArrayList<Challenge> challenges) {
        mChallenges.clear();
        mChallenges.addAll(challenges);
        mChallengeAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    // Open the VerifyChallenge view when we receive a single challenge from the server
    @Override
    public void onChallengeReceived(Challenge challenge) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("challenge", challenge);

        Intent intent = new Intent(this, VerifyChallenge.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // Handle request errors
    @Override
    public void onError(String error) {
        Log.e("PlayChallenges", error);
    }
}
