package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlayChallenges extends AppCompatActivity implements ItemOnClickListener,
        OnChallengeListReceivedListener,
        OnChallengeReceivedListener{

    private static final int CREATE_NEW_CHALLENGE_RESULT = 1;
    private static final int EDIT_CHALLENGE_RESULT = 2;

    ArrayList<Challenge> mChallenges = new ArrayList<>();
    RecyclerView mRecyclerView;
    ChallengeAdapter mChallengeAdapter;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_challenges);

        setupToolbar();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mChallengeAdapter = new ChallengeAdapter(mChallenges, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mChallengeAdapter);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("PlayChallenges");

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

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadChallengesFromServer();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChallengesFromServer();
    }

    // Set the toolbar as the supportActionBar
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

    @Override
    public void itemClicked(View view, int itemIndex) {
        Challenge challenge = mChallenges.get(itemIndex);

        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.getChallenge(challenge.id, this);
    }

    @Override
    public boolean itemLongClicked(View view, int itemIndex) {
        return false;
    }

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

    @Override
    public void onChallengeReceived(Challenge challenge) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("challenge", challenge);

        Intent intent = new Intent(this, DoChallenge.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onError(String error) {
        Log.e("PlayChallenges", error);
    }
}
