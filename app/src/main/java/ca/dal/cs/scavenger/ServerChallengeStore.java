package ca.dal.cs.scavenger;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// Interface representing a collection of challenges
interface ChallengeStore {
    void listChallenges(OnChallengeListReceivedListener listener, JSONObject requestJSON);
    void getChallenge(int challengeID, OnChallengeReceivedListener listener);
    void addChallenge(Challenge challenge, @NonNull OnChallengeAddedListener listener);
    void acceptChallenge(Challenge challenge, @NonNull OnChallengeAcceptedListener listener);
}

interface OnChallengeListReceivedListener {
    void onChallengeListReceived(ArrayList<Challenge> challenges);
    void onError(String error);
}

interface OnChallengeReceivedListener {
    void onChallengeReceived(Challenge challenge);
    void onError(String error);
}

interface OnChallengeAddedListener {
    void onChallengeAdded(int challengeID);
    void onError(String error);
}

interface OnChallengeAcceptedListener {
    void onChallengeAccepted();
    void onError(String error);
}

class StupidListener implements OnChallengeAddedListener, OnChallengeListReceivedListener, OnChallengeReceivedListener {

    @Override
    public void onChallengeAdded(int challengeID) {

    }

    @Override
    public void onChallengeListReceived(ArrayList<Challenge> challenges) {
        Gson gson = new GsonBuilder().create();
        for (Challenge challenge : challenges) {
            Log.w("harr", String.valueOf(challenge.id));
            Log.w("harr", gson.toJson(challenge));
        }
    }

    @Override
    public void onChallengeReceived(Challenge challenge) {

    }

    @Override
    public void onError(String error) {

    }
}

class ChallengeList extends ArrayList<Challenge> {}

class ServerChallengeStore implements ChallengeStore{
    private static final String BASE_URL = "http://scavenger.labsrishabh.com/";
    private static final String CHALLENGE_LIST_URL = BASE_URL + "list-challenges.php";
    private static final String GET_CHALLENGE_URL = BASE_URL + "get-challenge-by-id.php";
    private static final String ADD_CHALLENGE_URL = BASE_URL + "add-challenge.php";
    private static final String ACCEPT_CHALLENGE_URL = BASE_URL + "accept-challenge.php";

    @Override
    public void listChallenges(@NonNull final OnChallengeListReceivedListener listener, JSONObject json) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                CHALLENGE_LIST_URL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        JSONArray jsonArray;
                        try {
                            if (obj.getBoolean("success")) {
                                jsonArray = obj.getJSONArray("challenges");
                                ArrayList<Challenge> challenges = new Gson().fromJson(String.valueOf(jsonArray), ChallengeList.class);
                                listener.onChallengeListReceived(challenges);
                            } else {
                                listener.onError(obj.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError(e.getLocalizedMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.getLocalizedMessage());
                    }
                });

        VolleyRequestQueue.add(request);
    }

    @Override
    public void getChallenge(int challengeID, @NonNull final OnChallengeReceivedListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("challenge_id", challengeID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                GET_CHALLENGE_URL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        JSONObject challengeJson;
                        try {
                            if (obj.getBoolean("success")) {
                                challengeJson = obj.getJSONObject("challenge");
                                Log.w("harr", "downloaded_json: " + String.valueOf(challengeJson));

                                Challenge challenge = new Gson().fromJson(String.valueOf(challengeJson), Challenge.class);
                                listener.onChallengeReceived(challenge);
                            } else {
                                listener.onError(obj.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError(e.getLocalizedMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.getLocalizedMessage());
                    }
                });

        VolleyRequestQueue.add(request);
    }

    @Override
    public void addChallenge(Challenge challenge, @NonNull final OnChallengeAddedListener listener) {
        String challengeJson = new Gson().toJson(challenge);
        JSONObject json = null;
        try {
            json = new JSONObject(challengeJson);
            json.put("author_id", User.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("harr", json.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                ADD_CHALLENGE_URL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        try {
                            if (obj.getBoolean("success")) {
                                int challengeID = obj.getInt("challenge_id");
                                listener.onChallengeAdded(challengeID);
                            } else {
                                listener.onError(obj.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError(e.getLocalizedMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.getLocalizedMessage());
                    }
                });

        VolleyRequestQueue.add(request);
    }

    @Override
    public void acceptChallenge(Challenge challenge, @NonNull final OnChallengeAcceptedListener listener) {
        String challengeJson = new Gson().toJson(challenge);
        JSONObject json = null;
        try {
            json = new JSONObject(challengeJson);
            json.put("player_id", User.getID());
            json.put("challenge_id", challenge.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                ADD_CHALLENGE_URL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        try {
                            if (obj.getBoolean("success")) {
                                listener.onChallengeAccepted();
                            } else {
                                listener.onError(obj.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            listener.onError(e.getLocalizedMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error.getLocalizedMessage());
                    }
                });

        VolleyRequestQueue.add(request);
    }
}
