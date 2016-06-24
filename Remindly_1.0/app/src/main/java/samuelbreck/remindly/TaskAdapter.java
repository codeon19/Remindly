package samuelbreck.remindly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class TaskAdapter extends ArrayAdapter<Task> {
    public TaskAdapter(Context context, ArrayList<Task> task) {
        super(context, 0, task);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }
        // Lookup view for data population
        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        TextView tvPriority = (TextView) convertView.findViewById(R.id.tvPriority);
        TextView tvNote = (TextView) convertView.findViewById(R.id.tvNote);

        // Populate the data into the template view using the data object
        tvName.setText(task.getTaskName());
        tvDate.setText(task.getDueDate());
        tvPriority.setText(task.getPriority());
        tvNote.setText(task.getTaskNote());

        // Return the completed view to render on screen
        return convertView;
    }
}
