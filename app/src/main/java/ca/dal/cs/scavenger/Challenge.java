// Created by odavison
package ca.dal.cs.scavenger;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

// Data model of a challenge (entire scavenger hunt)
// Struct-style class -- data should be validated before it is stored here.
class Challenge implements VisualDataSource, Parcelable {
    int id = 0;
    int playerID = 0;
    String playerImageURL = "";
    String playerName = "";
    String imageURL = "";
    String localImagePath = "";
    String description = "";
    boolean is_complete = false;
    boolean is_verified = false;
    ArrayList<Task> tasks = new ArrayList<>();

    Challenge() {}

    // Part of VisualDataSource interface
    @Override
    public String getLocalDataPath() {
        return localImagePath;
    }

    // Part of VisualDataSource interface
    @Override
    public String getDataURL() {
        return imageURL;
    }

    // Check if the player has completed this challenge
    public boolean isComplete() {
        boolean returnValue = true;
        for (Task t: tasks) {
            if (!t.isComplete()) {
                returnValue = false;
                break;
            }
        }
        return returnValue;
    }

    // Check if the author has validated this challenge's submissions
    public boolean isVerified() {
        boolean returnValue = true;
        for (Task t: tasks) {
            if (!t.isVerified()) {
                returnValue = false;
                break;
            }
        }
        return returnValue;
    }

    // True iff there is local data that should be sent to the server
    public boolean hasLocalData() {
        return (localImagePath != null && !localImagePath.isEmpty());
    }

    // True iff at least one of the image paths is not empty
    public boolean hasImage() {
        return (localImagePath != null && !localImagePath.isEmpty()) ||
                (imageURL != null && !imageURL.isEmpty());
    }

    // Generated method to implement Parcelable
    protected Challenge(Parcel in) {
        id = in.readInt();
        playerID = in.readInt();
        playerName = in.readString();
        imageURL = in.readString();
        localImagePath = in.readString();
        description = in.readString();
        is_complete = in.readByte() != 0x00;
        is_verified = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            tasks = new ArrayList<Task>();
            in.readList(tasks, Task.class.getClassLoader());
        } else {
            tasks = null;
        }
    }

    // Generated method to implement Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    // Generated method to implement Parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(playerID);
        dest.writeString(playerName);
        dest.writeString(imageURL);
        dest.writeString(localImagePath);
        dest.writeString(description);
        dest.writeByte((byte) (is_complete ? 0x01 : 0x00));
        dest.writeByte((byte) (is_verified ? 0x01 : 0x00));
        if (tasks == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(tasks);
        }
    }

    // Generated inner class to implement Parcelable
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Challenge> CREATOR = new Parcelable.Creator<Challenge>() {
        @Override
        public Challenge createFromParcel(Parcel in) {
            return new Challenge(in);
        }

        @Override
        public Challenge[] newArray(int size) {
            return new Challenge[size];
        }
    };
}
