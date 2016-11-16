package ca.dal.cs.scavenger;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import java.io.Serializable;

/*
Class representing a challenge (list of tasks) and do it, make it work NOW
 */

class Task implements Serializable{
    enum Type {
        IMAGE, VIDEO, AUDIO, LOCATION
    }

    private static final class Icons {
        private static final IIcon IMAGE = GoogleMaterial.Icon.gmd_camera;
        private static final IIcon VIDEO = GoogleMaterial.Icon.gmd_videocam;
        private static final IIcon AUDIO = GoogleMaterial.Icon.gmd_mic;
        private static final IIcon LOCATION = GoogleMaterial.Icon.gmd_my_location;
        private static final IIcon ERROR = GoogleMaterial.Icon.gmd_error;
    }

    Type type;
    String description;

    Task(Type type, String description) {
        this.type = type;
        this.description = description;
    }

    static IconicsDrawable getTaskIcon(@NonNull Context context, @NonNull Type type) {
        IconicsDrawable icon = null;
        switch (type) {
            case IMAGE:
                icon = new IconicsDrawable(context).icon(Icons.IMAGE);
                break;
            case VIDEO:
                icon = new IconicsDrawable(context).icon(Icons.VIDEO);
                break;
            case AUDIO:
                icon = new IconicsDrawable(context).icon(Icons.AUDIO);
                break;
            case LOCATION:
                icon = new IconicsDrawable(context).icon(Icons.LOCATION);
                break;
            default:
                icon = new IconicsDrawable(context).icon(Icons.ERROR);
                break;
        }
        return icon;
    }

    static Intent getIntentForCompletion(Context context, Type type) {
        Intent intent;
        switch (type){
            case IMAGE:
                intent = new Intent(context, CompleteCameraTask.class);
                break;
            case VIDEO:
                intent = new Intent(context, CompleteCameraTask.class);
                break;
            case AUDIO:
                intent = new Intent(context, CompleteAudioTask.class);
                break;
            case LOCATION:
                intent = new Intent(context, CompleteLocationTask.class);
                break;
            default:
                intent = new Intent(context, CompleteCameraTask.class);
                break;
        }
        return intent;
    }

    static Intent getIntentForVerification(Context context, Type type) {
        Intent intent;
        switch (type){
            case IMAGE:
                intent = new Intent(context, VerifyCameraTask.class);
                break;
            case VIDEO:
                intent = new Intent(context, VerifyCameraTask.class);
                break;
            case AUDIO:
                intent = new Intent(context, VerifyAudioTask.class);
                break;
            case LOCATION:
                intent = new Intent(context, VerifyLocationTask.class);
                break;
            default:
                intent = new Intent(context, VerifyCameraTask.class);
                break;
        }
        return intent;
    }
}
