package ca.dal.cs.scavenger;

/**
 * Created by odavison on 27-11-2016.
 * Based on RegisterRequest by Rishabh
 */

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class InsertTaskRequest extends StringRequest {
    private static final String REQUEST_URL = "http://scavenger.labsrishabh.com/add-challenge-task.php";
    private Map<String, String> params;

    public InsertTaskRequest(int challengeID, String description, String taskType, Response.Listener<String> listener) {
        super(Method.POST, REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("challenge_id", String.valueOf(challengeID));
        params.put("description", description);
        params.put("type", taskType);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
