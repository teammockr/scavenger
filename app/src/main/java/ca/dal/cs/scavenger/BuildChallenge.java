package ca.dal.cs.scavenger;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

public class BuildChallenge extends AppCompatActivity implements ItemOnClickListener {

    private static final int CREATE_NEW_TASK_RESULT = 1;
    private static final int EDIT_TASK_RESULT = 2;
    private static final int PICK_CHALLENGE_IMAGE_RESULT = 3;//I need this one

    Challenge mChallenge;
    RecyclerView mRecyclerView;
    TaskAdapter mTaskAdapter;
    private int mChallengeIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_challenge);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mChallenge = (Challenge)bundle.getSerializable("challenge");
        if (bundle.containsKey("challengeIndex")) {
            // We are editing an existing challenge
            mChallengeIndex = bundle.getInt("challengeIndex");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTaskAdapter = new TaskAdapter(mChallenge.tasks, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mTaskAdapter);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Challenge Builder");

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_add)
                .color(Color.WHITE));
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int newTaskIndex = mChallenge.tasks.size();
                Task newTask = new Task(Task.Type.IMAGE, "");
                mChallenge.tasks.add(newTask);
                mTaskAdapter.notifyItemInserted(newTaskIndex);
                mRecyclerView.scrollToPosition(newTaskIndex);

                Bundle bundle = new Bundle();
                bundle.putSerializable("task", newTask);

                Intent intent = new Intent(view.getContext(), CreateCameraTask.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, CREATE_NEW_TASK_RESULT);
            }
        });

        ImageButton challengeImageButton = (ImageButton)findViewById(R.id.challenge_image_button);
        if(mChallenge.imageURIString == "") {
            challengeImageButton.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_add_a_photo));
        } else {
            updateChallengeImage();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = false;
        switch(item.getItemId()) {
            case R.id.action_confirm:
                acceptCreateChallenge();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }
        return result;
    }

    private void acceptCreateChallenge() {
        EditText editText = (EditText) findViewById(R.id.description);
        mChallenge.description = editText.getText().toString().trim();

        Bundle bundle = new Bundle();
        bundle.putInt("challengeIndex", mChallengeIndex);
        bundle.putSerializable("challenge", mChallenge);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm, menu);
        MenuItem actionConfirm = menu.findItem(R.id.action_confirm);
        actionConfirm.setIcon(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_check)
                .sizePx((int) getResources().getDimension(R.dimen.appbar_icon_size))
                .color(Color.WHITE));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case CREATE_NEW_TASK_RESULT:
                handleCreateNewTaskResult(resultCode, data);
                break;
            case EDIT_TASK_RESULT:
                handleEditTaskResult(resultCode, data);
                break;
            case PICK_CHALLENGE_IMAGE_RESULT:
                handlePickChallengeImageResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void handleCreateNewTaskResult(int resultCode, Intent intent) {
        int taskIndex = mChallenge.tasks.size() - 1;

        if (resultCode == RESULT_OK) {
            // User updated the task
            Bundle bundle = intent.getExtras();
            Task updatedTask = (Task)bundle.getSerializable("task");

            mChallenge.tasks.set(taskIndex, updatedTask);

            mTaskAdapter.notifyItemChanged(taskIndex);
        } else if (resultCode == RESULT_CANCELED){
            // User cancelled item creation -> remove the placeholder
            if (!mChallenge.tasks.isEmpty()) {
                mChallenge.tasks.remove(taskIndex);
                mTaskAdapter.notifyItemRemoved(taskIndex);
            }
        }
    }

    private void handleEditTaskResult(int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            // User updated the task
            Bundle bundle = intent.getExtras();
            int taskIndex = bundle.getInt("taskIndex");
            Task updatedTask = (Task) bundle.getSerializable("task");
            mChallenge.tasks.set(taskIndex, updatedTask);

            mTaskAdapter.notifyItemChanged(taskIndex);
            final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            recyclerView.scrollToPosition(mChallenge.tasks.size() - 1);
        }
    }

    private void handlePickChallengeImageResult(int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            // User updated the image
            Uri imageURI = intent.getData();
            mChallenge.imageURIString = imageURI.toString();
            updateChallengeImage();
        }
    }

    private void updateChallengeImage() {
        ImageButton challengeImageButton = (ImageButton)findViewById(R.id.challenge_image_button);
        Uri imageURI = Uri.parse(mChallenge.imageURIString);
        challengeImageButton.setImageURI(imageURI);
    }

    @Override
    public void itemClicked(View view, int itemIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt("taskIndex", itemIndex);
        bundle.putSerializable("task", mChallenge.tasks.get(itemIndex));

        Intent intent = new Intent(this, CreateCameraTask.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_TASK_RESULT);
    }
// I need this function
    public void pickChallengeImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_CHALLENGE_IMAGE_RESULT);
    }
}
