package ca.dal.cs.scavenger;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

class Challenge implements Serializable {
    String imageURIString = "";
    String description = "";
    ArrayList<Task> tasks = new ArrayList<>();
}
