package ca.dal.cs.scavenger;

import java.io.Serializable;
import java.util.ArrayList;

class Challenge implements Serializable{
    String description;
    ArrayList<Task> tasks = new ArrayList<>();

    Challenge(String description) {
        this.description = description;
    }
}
