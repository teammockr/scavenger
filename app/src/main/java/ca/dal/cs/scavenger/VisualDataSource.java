package ca.dal.cs.scavenger;

/**
 * Created by odavi on 11/27/2016.
 */

interface VisualDataSource {
    String getLocalDataPath();
    String getDataURL();
    boolean isComplete();
}
