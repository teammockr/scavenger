package ca.dal.cs.scavenger;

import android.app.Application;

import net.gotev.uploadservice.UploadService;

/**
 * Created by odavi on 11/25/2016.
 */
public class ScavengerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
    }
}
