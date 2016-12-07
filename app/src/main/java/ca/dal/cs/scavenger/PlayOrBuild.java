// created by odavison
package ca.dal.cs.scavenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

// Simple view to let user choose whether to act as a challenge builder or a challenge player
public class PlayOrBuild extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_or_build);

        setupToolbar();
    }

    // Setup toolbar and buttons
    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageButton userButton = (ImageButton) findViewById(R.id.toolbar_user_button);
        userButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(PlayOrBuild.this, Preferences.class);
                PlayOrBuild.this.startActivity(loginIntent);
            }
        });
        LoadVisual
                .withContext(this)
                .fromSource(User.getInstance())
                .into(userButton);

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Scavenger");
    }

    // If 'PLAY' clicked, open PlayChallenges
    public void openPlayView(View view) {
        Intent intent = new Intent(view.getContext(), PlayChallenges.class);
        startActivity(intent);
    }

    // If 'BUILD' clicked, open MyChallenges
    public void openBuildView(View view) {
        Intent intent = new Intent(view.getContext(), MyChallenges.class);
        startActivity(intent);
    }
}
