package ca.dal.cs.scavenger;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

class Challenge implements Parcelable {
    Integer id = 0;
    String imageURIString = "";
    String description = "";
    ArrayList<Task> tasks = new ArrayList<>();

    Challenge() {}

    protected Challenge(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        imageURIString = in.readString();
        description = in.readString();
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
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        dest.writeString(imageURIString);
        dest.writeString(description);
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
