package enity;

import enity.task.status.Status;
import enity.task.type.TaskType;

import java.util.Objects;

public class SubTask extends Task{

    private int hostTaskID;

    public SubTask(String taskName, String taskDescription, int hostTaskID) {
        super(taskName, taskDescription);

        this.hostTaskID = hostTaskID;
    }

    public SubTask(int taskId, TaskType type, String taskName, String taskDescription, Status status, int hostTaskID) {
        super(taskId, type, taskName, taskDescription, status);

        this.hostTaskID = hostTaskID;
    }

    public int getHostTaskID() {
        return hostTaskID;
    }

    public void setHostTaskID(int hostTaskID) {
        this.hostTaskID = hostTaskID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SubTask subTask = (SubTask) o;
        return hostTaskID == subTask.hostTaskID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hostTaskID);
    }

    @Override
    public String toString() {
        return TaskType.SUB.name()+ ","
                + this.getTaskId()+ ","
                + this.getTaskName()+ ","
                + this.getStatus()+ ","
                + getTaskDescription()+ ","
                + hostTaskID;
    }
}
