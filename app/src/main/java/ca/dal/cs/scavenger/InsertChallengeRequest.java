package ca.dal.cs.scavenger;

/**
 * Created by odavison on 27-11-2016.
 * Based on RegisterRequest by Rishabh
 */

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class InsertChallengeRequest extends StringRequest {
    private static final String REQUEST_URL = "http://scavenger.labsrishabh.com/add-challenge.php";
    private Map<String, String> params;

    public InsertChallengeRequest(int userID, String description, Response.Listener<String> listener) {
        super(Method.POST, REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("user_id", String.valueOf(userID));
        params.put("description", description);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
