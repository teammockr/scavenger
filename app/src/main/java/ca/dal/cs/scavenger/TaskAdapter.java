//created by odavison
package ca.dal.cs.scavenger;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

// Implements the ViewHolder pattern to display tasks in a few recyclerview
// items which are reused as the recyclerview is scrolled
class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> mTaskList;
    private ItemOnClickListener mItemOnClickListener;

    TaskAdapter(ArrayList<Task> taskList, ItemOnClickListener itemOnClickListener) {
        this.mTaskList = taskList;
        this.mItemOnClickListener = itemOnClickListener;
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    // Setup elements of the ViewHolder that remain the same as it is reused
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View taskCard = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.task_card, parent, false);

        TaskViewHolder taskViewHolder = new TaskViewHolder(taskCard);
        taskViewHolder.setItemOnClickListener(mItemOnClickListener);
        return taskViewHolder;
    }

    // Setup elements of the ViewHolder that are updated to match the currently-bound Task
    @Override
    public void onBindViewHolder(TaskViewHolder taskViewHolder, int i) {
        Task task = mTaskList.get(i);
        Context context = taskViewHolder.itemView.getContext();

        taskViewHolder.vDescription.setText(task.description);
        LoadVisual.withContext(context)
                .fromSource(task)
                .withDefaultIcon(task.type.getIcon())
                .into(taskViewHolder.vImage);

        if (task.is_complete) {
            taskViewHolder.vCompleted.setVisibility(View.VISIBLE);
        } else {
            taskViewHolder.vCompleted.setVisibility(View.INVISIBLE);
        }

        if (task.is_verified) {
            taskViewHolder.vVerified.setVisibility(View.VISIBLE);
        } else {
            taskViewHolder.vVerified.setVisibility(View.INVISIBLE);
        }
    }

    // ViewHolder class that displays a Task in a task_card layout
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        ImageView vImage;
        TextView vDescription;
        ImageView vCompleted;
        ImageView vVerified;
        private ItemOnClickListener itemOnClickListener;

        TaskViewHolder(View v) {
            super(v);
            vImage = (ImageView) v.findViewById(R.id.task_image);
            vDescription = (TextView) v.findViewById(R.id.description);

            vCompleted = (ImageView) v.findViewById(R.id.completed);
            vCompleted.setImageDrawable(new IconicsDrawable(v.getContext())
                    .icon(GoogleMaterial.Icon.gmd_check)
                    .color(Color.GREEN)
            );
            vVerified = (ImageView) v.findViewById(R.id.verified);
            vVerified.setImageDrawable(new IconicsDrawable(v.getContext())
                    .icon(GoogleMaterial.Icon.gmd_check)
                    .color(Color.GREEN)
            );

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemOnClickListener.itemClicked(view, getAdapterPosition());
                }
            });
        }

        void setItemOnClickListener(ItemOnClickListener itemOnClickListener) {
            this.itemOnClickListener = itemOnClickListener;
        }
    }
}
