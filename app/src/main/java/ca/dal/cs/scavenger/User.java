package ca.dal.cs.scavenger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by odavi on 12/3/2016.
 */

class User implements VisualDataSource {

    static private User mInstance;
    static private SharedPreferences mSharedPreferences;

    int id = 0;
    String name = "";
    String email = "";
    String password = "";
    int age = 0;
    String localImagePath = "";
    String imageURL = "";

    private User () {};

    static void initialize(Application application) {
        mSharedPreferences = application
                .getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String userJSON = mSharedPreferences.getString("user", "{}");
        User.loadFromJson(userJSON);
    }

    static User getInstance() {
        return mInstance;
    }

    static int getID() {
        return mInstance.id;
    }

    static void loadFromJson(String userJSON) {
        mInstance = new Gson().fromJson(userJSON, User.class);
    }

    static void save() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        String userJSON = new Gson().toJson(mInstance);
        editor.putString("user", userJSON);
        editor.commit();
    }

    static void logout() {
        mInstance = new User();
        User.save();
    }

    static boolean isLoggedIn() {
        return mInstance.id > 0;
    }

    @Override
    public String getLocalDataPath() {
        return localImagePath;
    }

    @Override
    public String getDataURL() {
        return imageURL;
    }
}
