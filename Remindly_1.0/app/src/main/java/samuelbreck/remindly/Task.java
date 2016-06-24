package samuelbreck.remindly;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Task extends RealmObject{

    @PrimaryKey private String taskName;

    private String  dueDate;
    private String priority;
    private String taskNote;

    public Task() {
        taskName = "";
        dueDate = "";
        priority = "";
        taskNote = "";
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String name) {
        this.taskName = name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String date) {
        this.dueDate = date;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String prior) {
        this.priority = prior;
    }

    public String getTaskNote() {
        return taskNote;
    }

    public void setTaskNote(String note) {
        this.taskNote = note;
    }

}