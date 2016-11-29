package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

public class Lobby extends AppCompatActivity implements ItemOnClickListener {

    private static final int CREATE_NEW_CHALLENGE_RESULT = 1;
    private static final int EDIT_CHALLENGE_RESULT = 2;

    ArrayList<Challenge> mChallenges = new ArrayList<>();
    RecyclerView mRecyclerView;
    ChallengeAdapter mChallengeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
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
        actionBar.setTitle("Lobby");

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_add)
                .color(Color.WHITE));
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                int newChallengeIndex = mChallenges.size();
                Challenge newChallenge = new Challenge();
                mChallenges.add(newChallenge);
                mChallengeAdapter.notifyItemInserted(newChallengeIndex);
                mRecyclerView.scrollToPosition(newChallengeIndex);

                Bundle bundle = new Bundle();
                bundle.putSerializable("challenge", newChallenge);

                Intent intent = new Intent(view.getContext(), BuildChallenge.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, CREATE_NEW_CHALLENGE_RESULT);
            }
        });
        ImageButton userPref = (ImageButton) findViewById(R.id.btnUserPreferences);
        //ImageButton userPref = (ImageButton) findViewById(R.id.btnUserPreferences);
        userPref.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(Lobby.this, Preferences.class);
                Lobby.this.startActivity(loginIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case CREATE_NEW_CHALLENGE_RESULT:
                handleCreateNewChallengeResult(resultCode, data);
                break;
            case EDIT_CHALLENGE_RESULT:
                handleEditChallengeResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void handleCreateNewChallengeResult(int resultCode, Intent intent) {
        int challengeIndex = mChallenges.size() - 1;

        if (resultCode == RESULT_OK) {
            // User updated the challenge
            Bundle bundle = intent.getExtras();
            Challenge updatedChallenge = (Challenge)bundle.getSerializable("challenge");

            mChallenges.set(challengeIndex, updatedChallenge);

            mChallengeAdapter.notifyItemChanged(challengeIndex);
        } else if (resultCode == RESULT_CANCELED){
            // User cancelled item creation -> remove the placeholder
            if (!mChallenges.isEmpty()) {
                mChallenges.remove(challengeIndex);
                mChallengeAdapter.notifyItemRemoved(challengeIndex);
            }
        }
    }

    private void handleEditChallengeResult(int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            // User updated the challenge
            Bundle bundle = intent.getExtras();
            int challengeIndex = bundle.getInt("challengeIndex");
            Challenge updatedChallenge = (Challenge) bundle.getSerializable("challenge");
            mChallenges.set(challengeIndex, updatedChallenge);

            mChallengeAdapter.notifyItemChanged(challengeIndex);
            final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            recyclerView.scrollToPosition(mChallenges.size() - 1);
        } else if (resultCode == RESULT_CANCELED){
            // User cancelled the update -> do nothing
        }
    }


    @Override
    public void itemClicked(View view, int itemIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt("challengeIndex", itemIndex);
        bundle.putSerializable("challenge", mChallenges.get(itemIndex));

        Intent intent = new Intent(this, DoChallenge.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
