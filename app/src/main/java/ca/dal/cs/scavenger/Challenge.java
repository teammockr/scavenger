package ca.dal.cs.scavenger;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

class Challenge implements Serializable {
    Integer id = 0;
    String imageURIString = "";
    String description = "";
    ArrayList<Task> tasks = new ArrayList<>();
}
