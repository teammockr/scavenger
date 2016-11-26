package ca.dal.cs.scavenger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by odavi on 11/15/2016.
 */

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

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View taskCard = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.task_card, parent, false);

        TaskViewHolder taskViewHolder = new TaskViewHolder(taskCard);
        taskViewHolder.setItemOnClickListener(mItemOnClickListener);
        return taskViewHolder;
    }

    @Override
    public void onBindViewHolder(TaskViewHolder taskViewHolder, int i) {
        Task task = mTaskList.get(i);
        Context context = taskViewHolder.itemView.getContext();

        taskViewHolder.vImage.setImageDrawable(task.getIcon(context));
        taskViewHolder.vDescription.setText(task.description);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        ImageView vImage;
        TextView vDescription;
        private ItemOnClickListener itemOnClickListener;

        TaskViewHolder(View v) {
            super(v);
            vImage = (ImageView) v.findViewById(R.id.task_image);
            vDescription = (TextView) v.findViewById(R.id.description);
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
