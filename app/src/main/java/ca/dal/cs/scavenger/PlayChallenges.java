// created by odavison
package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// View listing the current user's accepted challenges
public class PlayChallenges extends AppCompatActivity implements ItemOnClickListener,
        OnChallengeListReceivedListener,
        OnChallengeReceivedListener{

    ArrayList<Challenge> mChallenges = new ArrayList<>();
    RecyclerView mRecyclerView;
    ChallengeAdapter mChallengeAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_challenges);

        setupToolbar();

        // Setup RecyclerView to list challenges
        mChallengeAdapter = new ChallengeAdapter(mChallenges, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mChallengeAdapter);

        // When the FAB is clicked, go to the all-challenges list to choose
        // a new challenge to play
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_add)
                .color(Color.WHITE));
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AllChallenges.class);
                startActivity(intent);
            }
        });

        // swipe-to-refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadChallengesFromServer();
            }
        });
    }

    // Reload list of challenges from the server whenever this activity becomes active
    @Override
    protected void onResume() {
        super.onResume();
        loadChallengesFromServer();
    }

    // Setup toolbar and buttons
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton userButton = (ImageButton) findViewById(R.id.toolbar_user_button);
        userButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlayChallenges.this, Preferences.class);
                PlayChallenges.this.startActivity(intent);
            }
        });
        LoadVisual
                .withContext(this)
                .fromSource(User.getInstance())
                .into(userButton);

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Play");
    }

    // When an challenge is clicked, load its details from the server
    @Override
    public void itemClicked(View view, int itemIndex) {
        Challenge challenge = mChallenges.get(itemIndex);

        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.getChallenge(challenge.id, this);
    }

    // No behavior for long-clicking a challenge in this view
    @Override
    public boolean itemLongClicked(View view, int itemIndex) {
        return false;
    }

    // Send request to server for an updated challenges list
    private void loadChallengesFromServer() {
        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();

        JSONObject requestJSON = new JSONObject();
        try {
            requestJSON.put("player_id", User.getID());
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }

        serverChallengeStore.listChallenges(this, requestJSON);
    }

    // When an updated challenge list is received from the server, update the RecyclerView
    // If we haven't selected any challenges to play yet, jump to the AllChallenges view so
    // the user can select one.
    @Override
    public void onChallengeListReceived(ArrayList<Challenge> challenges) {
        if (challenges.size() > 0) {
            mChallenges.clear();
            mChallenges.addAll(challenges);
            mChallengeAdapter.notifyDataSetChanged();
            mRecyclerView.scrollToPosition(0);
            mSwipeRefreshLayout.setRefreshing(false);
        } else {
            Intent intent = new Intent(this, AllChallenges.class);
            startActivity(intent);
        }
    }

    // When a single challenge is received, open it in DoChallenge to play it
    @Override
    public void onChallengeReceived(Challenge challenge) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("challenge", challenge);

        Intent intent = new Intent(this, DoChallenge.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // Handle server response errors
    @Override
    public void onError(String error) {
        Log.e("PlayChallenges", error);
    }
}
