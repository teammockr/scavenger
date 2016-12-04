package ca.dal.cs.scavenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String LOGIN_URL = "http://scavenger.labsrishabh.com/login.php";

    //Defining views
    private EditText etUsername;
    private EditText etPassword;
    private Button bSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initializing views
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

        bSignIn = (Button) findViewById(R.id.bSignIn);

        //Adding click listener
        bSignIn.setOnClickListener(this);

        final TextView tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(User.isLoggedIn()){
            //We will start the Profile Activity
            Intent intent = new Intent(LoginActivity.this, Lobby.class);
            startActivity(intent);
        }
    }

    private void login(){
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
                                User.save();

                                //Starting profile activity
                                Intent intent = new Intent(LoginActivity.this, Lobby.class);
                                startActivity(intent);
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
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                    }
                });

        VolleyRequestQueue.add(request);
    }

    @Override
    public void onClick(View v) {
        //Calling the login function
        login();
    }
}