package ca.dal.cs.scavenger;

/**
 * Based on the sample code provided by Google
 * on the implementation of the Video recording
 * feature of the Android Camera2 API
 * Modified by Choudhury Mahmid (Sandy) on 2016-12-03.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class CompleteVideoTaskActivity extends Activity implements CompleteVideoTaskFragment.OnDataPass{
    private Task mTask;
    private int taskIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_video_task);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mTask = bundle.getParcelable("task");
        taskIndex = bundle.getInt("taskIndex");
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.activity_complete_video_task, CompleteVideoTaskFragment.newInstance())
                    .commit();
        }
    }

    public void sendVideoFilePath(){
        Bundle bundle = new Bundle();
        bundle.putParcelable("task", mTask);
        bundle.putInt("taskIndex", taskIndex);

        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void OnDataPass(String filePath) {
        mTask.localDataPath = filePath;
        sendVideoFilePath();
    }
}
