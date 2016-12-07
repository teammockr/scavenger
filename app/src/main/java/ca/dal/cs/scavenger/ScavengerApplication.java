//created by odavison
package ca.dal.cs.scavenger;

import android.app.Application;

import net.gotev.uploadservice.UploadService;

// Application class used for initializing singletons
public class ScavengerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;

        VolleyRequestQueue.initialize(this);
        User.initialize(this);
    }
}
