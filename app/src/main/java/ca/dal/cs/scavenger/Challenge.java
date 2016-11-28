package ca.dal.cs.scavenger;

import java.util.ArrayList;

class Challenge implements VisualDataSource {
    int id = 0;
    String imageURL = "";
    String localImagePath = "";
    String description = "";
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
}
