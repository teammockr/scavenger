package ca.dal.cs.scavenger;

import android.content.Context;
import android.content.Intent;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

import java.io.Serializable;

import ca.dal.cs.scavenger.R;

// Represents a task (single item in a scavenger hunt)
// Is the single point of truth for the types of task available,
// their icons, and which views are used to complete and verify them.
class Task implements Serializable{
    enum Type {
        IMAGE(GoogleMaterial.Icon.gmd_camera,
                R.string.imagePrompt,
                CompleteCameraTask.class,
                VerifyCameraTask.class),
        VIDEO(GoogleMaterial.Icon.gmd_videocam,
                R.string.videoPrompt,
                CompleteCameraTask.class,
                VerifyCameraTask.class),
        AUDIO(GoogleMaterial.Icon.gmd_volume_up,
                R.string.audioPrompt,
                CompleteAudioTask.class,
                VerifyAudioTask.class),
        LOCATION(GoogleMaterial.Icon.gmd_my_location,
                R.string.locationPrompt,
                CompleteLocationTask.class,
                VerifyLocationTask.class);

        private final IIcon icon; // Icon for the task type
        private final int promptID; // ID of the string resource for the task prompt
        private final Class<?> completeClass; // View for completing this type of task
        private final Class<?> verifyClass; // View for verifying this type of task

        private Type(IIcon icon, int promptID, Class<?> completeClass, Class<?> verifyClass) {
            this.icon = icon;
            this.promptID = promptID;
            this.completeClass = completeClass;
            this.verifyClass = verifyClass;
        }

        public IIcon getIcon() {
            return this.icon;
        }

        public int getPromptStringResourceID() {
            return this.promptID;
        }

        public Class<?> getCompleteClass() {
            return this.completeClass;
        }

        public Class<?> getVerifyClass() {
            return this.verifyClass;
        }
    }

    int id = 0;
    String description = "";
    Type type = Type.IMAGE;
    String localDataPath = "";

    // Create and return the IconicsDrawable for this task type
    IconicsDrawable getIcon(Context context) {
        return new IconicsDrawable(context).icon(type.getIcon());
    }

    // Create and return the prompt String for this task type
    String getPrompt(Context context) {
        return context.getResources().getString(type.getPromptStringResourceID());
    }

    // Return an intent which will be used to start the completion view for this task
    Intent getIntentForCompletion(Context context) {
        return new Intent(context, type.getCompleteClass());
    }

    // Return an intent which will be used to start the verification view for this task
    Intent getIntentForVerification(Context context) {
        return new Intent(context, type.getVerifyClass());
    }
}
