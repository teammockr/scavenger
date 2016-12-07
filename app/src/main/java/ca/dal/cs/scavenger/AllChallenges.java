// Created by odavison
package ca.dal.cs.scavenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

// View that shows all the challenges available on the server
public class AllChallenges extends AppCompatActivity implements ItemOnClickListener,
        OnChallengeListReceivedListener,
        OnChallengeReceivedListener{

    private static final int ADD_CHALLENGE_RESULT = 1;

    ArrayList<Challenge> mChallenges = new ArrayList<>();
    RecyclerView mRecyclerView;
    ChallengeAdapter mChallengeAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_challenges);

        setupToolbar();

        // setup the recyclerview
        mChallengeAdapter = new ChallengeAdapter(mChallenges, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mChallengeAdapter);

        // pull-to-refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadChallengesFromServer();
            }
        });
    }

    // Want to reload the challenge list from the server every time this activity becomes
    // the active activity.
    @Override
    protected void onResume() {
        super.onResume();
        loadChallengesFromServer();
    }

    // Set the toolbar and buttons
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton userButton = (ImageButton) findViewById(R.id.toolbar_user_button);
        userButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AllChallenges.this, Preferences.class);
                AllChallenges.this.startActivity(intent);
            }
        });
        LoadVisual
                .withContext(this)
                .fromSource(User.getInstance())
                .into(userButton);

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("All Challenges");
    }

    // Handle returned values from activities we started
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case ADD_CHALLENGE_RESULT:
                handleAcceptChallengeResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Close challenge browser when user has accepted an activity
    private void handleAcceptChallengeResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // User selected a challenge
            finish();
        }
    }

    // Open preview activity when user clicks an item
    @Override
    public void itemClicked(View view, int itemIndex) {
        Challenge challenge = mChallenges.get(itemIndex);

        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.getChallenge(challenge.id, this);
    }

    // No behaviour on long click
    @Override
    public boolean itemLongClicked(View view, int itemIndex) {
        return false;
    }

    // Update the list of challenges from the server
    private void loadChallengesFromServer() {
        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.listChallenges(this, null);
    }

    // When we receive the list of challenges from the server, update the recyclerview
    @Override
    public void onChallengeListReceived(ArrayList<Challenge> challenges) {
        mChallenges.clear();
        mChallenges.addAll(challenges);
        mChallengeAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    // When we receive a single challenge (for preview) from the server,
    // open the PreviewChallenge activity
    @Override
    public void onChallengeReceived(Challenge challenge) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("challenge", challenge);

        Intent intent = new Intent(this, PreviewChallenge.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, ADD_CHALLENGE_RESULT);
    }

    // Handle error with request to server
    @Override
    public void onError(String error) {
        Log.e("PlayChallenges", error);
    }
}
