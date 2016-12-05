package ca.dal.cs.scavenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_URL = "http://scavenger.labsrishabh.com/login.php";
    private static final int REGISTERACTIVITY_RESULT = 1;

    //Defining views
    private EditText etUsername;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing views
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        final TextView tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivityForResult(registerIntent, REGISTERACTIVITY_RESULT);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(User.isLoggedIn()){
            //We will start the Profile Activity
            Intent intent = new Intent(LoginActivity.this, PlayOrBuild.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REGISTERACTIVITY_RESULT:
                handleRegisterActivityResult(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleRegisterActivityResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_LONG).show();
        }
    }

    // onClick for the Login button
    public void login(View view){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("email", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                LOGIN_URL,
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject obj) {
                        JSONObject userJSON;
                        try {
                            if (obj.getBoolean("success")) {
                                userJSON = obj.getJSONObject("user");
                                User.loadFromJson(userJSON.toString());

                                if (User.getID() > 0) {
                                    User.save();
                                    Intent intent = new Intent(LoginActivity.this, PlayOrBuild.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.e("LoginActivityResponse", obj.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "Error connecting to login server", Toast.LENGTH_LONG).show();
                    }
                });

        VolleyRequestQueue.add(request);
    }
}