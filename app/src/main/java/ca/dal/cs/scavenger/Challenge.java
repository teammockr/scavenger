package ca.dal.cs.scavenger;

import android.graphics.drawable.Drawable;

/*
Class representing a challenge (list of tasks)
 */

class Challenge {
    Drawable challengeDrawable;
    String title;
    String description;

    Challenge(Drawable drawable, String title, String description) {
        this.challengeDrawable = drawable;
        this.title = title;
        this.description = description;
    }
}
