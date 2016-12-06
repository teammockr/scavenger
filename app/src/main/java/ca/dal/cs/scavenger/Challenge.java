package ca.dal.cs.scavenger;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

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

    @Override
    public String getLocalDataPath() {
        return localImagePath;
    }

    @Override
    public String getDataURL() {
        return imageURL;
    }

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

    public boolean hasLocalData() {
        return (localImagePath != null && !localImagePath.isEmpty());
    }

    public boolean hasImage() {
        return (localImagePath != null && !localImagePath.isEmpty()) ||
                (imageURL != null && !imageURL.isEmpty());
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

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
