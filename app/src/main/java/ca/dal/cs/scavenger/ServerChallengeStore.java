//created by odavison
package ca.dal.cs.scavenger;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

// Interface representing some sort of data store of challenges
// We make requests to the ChallengeStore and register listeners to receive
// the data when it is ready.
interface ChallengeStore {
    void listChallenges(OnChallengeListReceivedListener listener, JSONObject requestJSON);
    void getChallenge(int challengeID, OnChallengeReceivedListener listener);
    void addChallenge(Challenge challenge, @NonNull OnChallengeAddedListener listener);
    void acceptChallenge(Challenge challenge, @NonNull OnChallengeAcceptedListener listener);
    void markChallengeComplete(Challenge challenge, @NonNull OnChallengeMarkedCompleteListener listener);
    void markTaskVerified(Task task, @NonNull OnTaskMarkedVerifiedListener listener);
    void markChallengeVerified(Challenge challenge, @NonNull OnChallengeMarkedVerifiedListener listener);
    void uploadLocationTask(Task task, @NonNull OnLocationTaskUploadedListener listener);
}

// Interface to listen for updated challenge lists
interface OnChallengeListReceivedListener {
    void onChallengeListReceived(ArrayList<Challenge> challenges);
    void onError(String error);
}

// Interface to listen for an updated challenge
interface OnChallengeReceivedListener {
    void onChallengeReceived(Challenge challenge);
    void onError(String error);
}

// Interface to listen for confirmation a challenge was added
interface OnChallengeAddedListener {
    void onChallengeAdded(int challengeID);
    void onError(String error);
}

// Interface to listen for confirmation a challenge was accepted
interface OnChallengeAcceptedListener {
    void onChallengeAccepted();
    void onError(String error);
}

// Interface to listen for confirmation that a challenge was marked completed
interface OnChallengeMarkedCompleteListener {
    void onChallengeMarkedComplete();
    void onError(String error);
}

// Interface to listen for confirmation that a challenge was marked verified
interface OnChallengeMarkedVerifiedListener {
    void onChallengeMarkedVerified();
    void onError(String error);
}

// Interface to listen for confirmation that a task was marked verified
interface OnTaskMarkedVerifiedListener {
    void onTaskMarkedVerified();
    void onError(String error);
}

// Interface to listen for confirmation that a task was uploaded successfully
interface OnLocationTaskUploadedListener {
    void onLocationTaskUploaded();
    void onError(String error);
}

class ChallengeList extends ArrayList<Challenge> {}

// A concrete ChallengeStore representing a server storing challenges
class ServerChallengeStore implements ChallengeStore{
    private static final String BASE_URL = "http://scavenger.labsrishabh.com/";
    private static final String CHALLENGE_LIST_URL = BASE_URL + "list-challenges.php";
    private static final String GET_CHALLENGE_URL = BASE_URL + "get-challenge-by-id.php";
    private static final String ADD_CHALLENGE_URL = BASE_URL + "add-challenge.php";
    private static final String ACCEPT_CHALLENGE_URL = BASE_URL + "accept-challenge.php";
    private static final String MARK_CHALLENGE_COMPLETED_URL = BASE_URL + "mark-challenge-completed.php";
    private static final String MARK_TASK_VERIFIED_URL = BASE_URL + "mark-task-verified.php";
    private static final String MARK_CHALLENGE_VERIFIED_URL = BASE_URL + "mark-challenge-verified.php";
    private static final String UPLOAD_LOCATION_TASK_URL = BASE_URL + "upload-location-task.php";

    // Setup a request for a list of challenges
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

    // Setup a request for a single challenge
    @Override
    public void getChallenge(final int challengeID, @NonNull final OnChallengeReceivedListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("challenge_id", challengeID);
            json.put("player_id", User.getID());
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

                                Log.w("challengeJson", challengeJson.toString());

                                Challenge challenge = new Gson().fromJson(String.valueOf(challengeJson), Challenge.class);

                                Log.w("parsedChallenge", new Gson().toJson(challenge));
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

    // Setup a request to add a challenge to the ServerChallengeStore
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

    // Setup a request to mark a challenge as accepted
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
                ACCEPT_CHALLENGE_URL,
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

    // Setup a request to mark a challenge as completed
    @Override
    public void markChallengeComplete(Challenge challenge, @NonNull final OnChallengeMarkedCompleteListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("challenge_id", challenge.id);
            json.put("player_id", User.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                MARK_CHALLENGE_COMPLETED_URL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        try {
                            if (obj.getBoolean("success")) {
                                listener.onChallengeMarkedComplete();
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

    // Setup a request to mark a task as verified
    @Override
    public void markTaskVerified(Task task, @NonNull final OnTaskMarkedVerifiedListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("task_id", task.id);
            json.put("player_id", User.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                MARK_TASK_VERIFIED_URL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        try {
                            if (obj.getBoolean("success")) {
                                listener.onTaskMarkedVerified();
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

    // Setup a request to mark a challenge as verified
    @Override
    public void markChallengeVerified(Challenge challenge, @NonNull final OnChallengeMarkedVerifiedListener listener) {
        JSONObject json = new JSONObject();
        try {
            json.put("challenge_id", challenge.id);
            json.put("player_id", User.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                MARK_CHALLENGE_VERIFIED_URL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        try {
                            if (obj.getBoolean("success")) {
                                listener.onChallengeMarkedVerified();
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

    // Setup a request to upload a location task
    @Override
    public void uploadLocationTask(Task task, @NonNull final OnLocationTaskUploadedListener listener) {
        JSONObject json = new JSONObject();
        try {
            json = new JSONObject(new Gson().toJson(task));
            json.put("player_id", User.getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.w("locUpSubmitted", json.toString());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                UPLOAD_LOCATION_TASK_URL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        try {
                            if (obj.getBoolean("success")) {
                                Log.w("locUpResponse", obj.toString());
                                listener.onLocationTaskUploaded();
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
