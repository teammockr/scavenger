package ca.dal.cs.scavenger;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.common.io.Files;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import net.gotev.uploadservice.MultipartUploadRequest;

import java.io.File;
import java.io.IOException;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class BuildChallenge extends AppCompatActivity implements ItemOnClickListener, OnChallengeAddedListener {

    private static final int CREATE_NEW_TASK_RESULT = 1;
    private static final int EDIT_TASK_RESULT = 2;
    private static final int PICK_CHALLENGE_IMAGE_RESULT = 3;

    Challenge mChallenge = new Challenge();
    RecyclerView mRecyclerView;
    TaskAdapter mTaskAdapter;
    private int mChallengeIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_challenge);

        setupToolbar();
        setupChallengeImageButton();
        setupRecyclerView();
        setupFloatingActionButton();
    }

    // Set up the challenge image button
    // It should be accessible only if the user grants the
    // READ_EXTERNAL_STORAGE permission
    private void setupChallengeImageButton() {
        ImageButton challengeImageButton = (ImageButton) findViewById(R.id.challenge_image_button);

        challengeImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                BuildChallengePermissionsDispatcher.pickChallengeImageWithCheck(BuildChallenge.this, view);
            }
        });

        updateChallengeImage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        BuildChallengePermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    // Setup FAB and its behaviour
    // The FAB implements the main action for this view:
    // create a new task for the current challenge.
    private void setupFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setImageDrawable(new IconicsDrawable(this)
                .icon(GoogleMaterial.Icon.gmd_add)
                .color(Color.WHITE));
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int newTaskIndex = mChallenge.tasks.size();
                Task newTask = new Task();
                mChallenge.tasks.add(newTask);
                mTaskAdapter.notifyItemInserted(newTaskIndex);
                mRecyclerView.scrollToPosition(newTaskIndex);

                Bundle bundle = new Bundle();
                bundle.putParcelable("task", newTask);

                Intent intent = new Intent(view.getContext(), CreateTask.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, CREATE_NEW_TASK_RESULT);
            }
        });
    }

    // Setup the adapter and layout for the recyclerView
    private void setupRecyclerView() {
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
                Intent loginIntent = new Intent(BuildChallenge.this, Preferences.class);
                BuildChallenge.this.startActivity(loginIntent);
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
                BuildChallenge.this.acceptCreateChallenge();
            }
        });

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText("Build Challenge");
    }

    // This method is called whenever an activity that was opened with
    // startActivityForResult returns. Its behaviour is dictated by the
    // requestcode that was used to start the activity, as well as the
    // resultCode and data the activity returns.
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

    // Save the result of task creation
    void handleCreateNewTaskResult(int resultCode, Intent intent) {
        int taskIndex = mChallenge.tasks.size() - 1;

        if (resultCode == RESULT_OK) {
            // User updated the task
            Bundle bundle = intent.getExtras();
            Task updatedTask = bundle.getParcelable("task");

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

    // Save the result of task editing
    private void handleEditTaskResult(int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            // User updated the task
            Bundle bundle = intent.getExtras();
            int taskIndex = bundle.getInt("taskIndex");
            Task updatedTask = bundle.getParcelable("task");
            mChallenge.tasks.set(taskIndex, updatedTask);

            mTaskAdapter.notifyItemChanged(taskIndex);
            final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
            recyclerView.scrollToPosition(mChallenge.tasks.size() - 1);
        }
    }

    // Save the result of the user selecting a challenge image
    private void handlePickChallengeImageResult(int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            // User selected an image from the gallery
            Uri imageURI = intent.getData();
            String filePath = getRealPathFromURI(this, imageURI);
            File sourceFile = new File(filePath);

            String rootPath = getFilesDir().getAbsolutePath();
            String challengesDirPath = rootPath + File.separator + "media_challenge";

            File challengesDir = new File(challengesDirPath);
            File destFile = new File(challengesDirPath +
                    File.separator + String.valueOf(mChallenge.id) + ".jpg");

            if(!challengesDir.exists()) {
                challengesDir.mkdir();
            }

            try {
                Files.copy(sourceFile, destFile);
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }

            mChallenge.localImagePath = destFile.getAbsolutePath();
            updateChallengeImage();
        }
    }

    // Get actual file path from a content:// URI (like those returned from the Gallery)
    // http://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Update the UI challenge image based on the Uri stored in the mChallenge instance
    private void updateChallengeImage() {
        ImageButton challengeImageButton = (ImageButton)findViewById(R.id.challenge_image_button);

        LoadVisual.withContext(this)
                .fromSource(mChallenge)
                .withDefaultIcon(GoogleMaterial.Icon.gmd_add_a_photo)
                .into(challengeImageButton);
    }

    // Callback for when the item at <itemIndex> is clicked in this view's recyclerView
    // Open createTask view with the selected item's data.
    @Override
    public void itemClicked(View view, int itemIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt("taskIndex", itemIndex);
        bundle.putParcelable("task", mChallenge.tasks.get(itemIndex));

        Intent intent = new Intent(this, CreateTask.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, EDIT_TASK_RESULT);
    }

    // Open the gallery so the user can choose a challengeImage
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    public void pickChallengeImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_CHALLENGE_IMAGE_RESULT);
    }

    // Upload the challenge to the server when the user accepts it
    private void acceptCreateChallenge() {
        EditText description = (EditText) findViewById(R.id.description);
        mChallenge.description = description.getText().toString();

        ServerChallengeStore serverChallengeStore = new ServerChallengeStore();
        serverChallengeStore.addChallenge(mChallenge, this);
    }


    @Override
    public void onChallengeAdded(int challengeID) {
        mChallenge.id = challengeID;
        uploadChallengeImage();
        finish();
    }

    private void uploadChallengeImage() {
        Log.w("harr", "in uploadChallenge");
        try {
            String uploadId =
                    new MultipartUploadRequest(this, "http://scavenger.labsrishabh.com/upload-media.php")
                            .addFileToUpload(mChallenge.localImagePath, "media")
                            .addParameter("challenge_id", String.valueOf(mChallenge.id))
//                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    @Override
    public void onError(String error) {
        Log.e("PlayChallenges", error);
    }
}
