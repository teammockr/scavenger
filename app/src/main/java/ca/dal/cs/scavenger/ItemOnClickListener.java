// created by odavison
package ca.dal.cs.scavenger;

// Interface for classes that want to be notified of click events
// on items in a recyclerview.
public interface ItemOnClickListener {
    void itemClicked(android.view.View view, int itemIndex);
    boolean itemLongClicked(android.view.View view, int itemIndex);
}
