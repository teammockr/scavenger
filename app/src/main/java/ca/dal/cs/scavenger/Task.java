//created by odavison
package ca.dal.cs.scavenger;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;

// Represents a task (single item in a scavenger hunt)
// Is the single point of truth for the types of task available,
// their icons, and which views are used to complete and verify them.
class Task implements VisualDataSource, Parcelable {

    // Enum representing the possible types of task
    // Includes information about the icon and prompt to use for the task type,
    // and the Complete- and Verify-Activities for it
    enum Type {
        IMAGE(GoogleMaterial.Icon.gmd_camera,
                R.string.imagePrompt,
                CompleteImageTask.class,
                VerifyImageTask.class),
        VIDEO(GoogleMaterial.Icon.gmd_videocam,
                R.string.videoPrompt,
                CompleteVideoTaskActivity.class,
                VerifyVideoTask.class),
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
    private int playerID = 0;
    String description = "";
    Type type = Type.IMAGE;
    String localDataPath = "";
    String dataURL = "";
    LatLng requestedLocation;
    LatLng submittedLocation;
    boolean is_complete;
    boolean is_verified;

    Task () {}

    // Is complete iff it is a Location task and contains a submitted location
    // or it is any other type of tasks, and has a local path or a url to submitted media
    public boolean isComplete() {
        if (type == Type.LOCATION) {
            return (submittedLocation != null);
        } else {
            return (localDataPath != null && !localDataPath.isEmpty()) ||
                    (dataURL != null && !dataURL.isEmpty());
        }
    }

    // True iff task has media that needs to be uploaded
    public boolean hasLocalData() {
        return (localDataPath != null && !localDataPath.isEmpty());
    }

    public boolean isVerified() {
        return is_verified;
    }

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

    // Part of VisualDataSource interface
    @Override
    public String getLocalDataPath() {
        return this.localDataPath;
    }

    // Part of VisualDataSource interface
    @Override
    public String getDataURL() {
        return this.dataURL;
    }

    // Part of Parcelable interface
    protected Task(Parcel in) {
        id = in.readInt();
        playerID = in.readInt();
        description = in.readString();
        type = (Type) in.readValue(Type.class.getClassLoader());
        localDataPath = in.readString();
        dataURL = in.readString();
        requestedLocation = (LatLng) in.readValue(LatLng.class.getClassLoader());
        submittedLocation = (LatLng) in.readValue(LatLng.class.getClassLoader());
        is_complete = in.readByte() != 0x00;
        is_verified = in.readByte() != 0x00;
    }

    // Part of Parcelable interface
    @Override
    public int describeContents() {
        return 0;
    }

    // Part of Parcelable interface
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(playerID);
        dest.writeString(description);
        dest.writeValue(type);
        dest.writeString(localDataPath);
        dest.writeString(dataURL);
        dest.writeValue(requestedLocation);
        dest.writeValue(submittedLocation);
        dest.writeByte((byte) (is_complete ? 0x01 : 0x00));
        dest.writeByte((byte) (is_verified ? 0x01 : 0x00));
    }

    // Part of Parcelable interface
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
