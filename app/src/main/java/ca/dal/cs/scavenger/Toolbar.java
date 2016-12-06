package ca.dal.cs.scavenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by rishabh on 28-11-2016.
 */

public class Toolbar extends AppCompatActivity implements View.OnClickListener {

    private ImageButton btnUserPreferences;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.toolbar);
        btnUserPreferences = (ImageButton) findViewById(R.id.toolbar_user_button);
        btnUserPreferences.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btnUserPreferences){
            Intent loginIntent = new Intent(Toolbar.this, Preferences.class);
            Toolbar.this.startActivity(loginIntent);
        }
    }
}
