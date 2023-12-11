package enity;

import enums.Status;
import enums.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SubTask extends Task{

    private int hostTaskID;

    public SubTask(String taskName, String taskDescription, LocalDateTime startTime, int durationInMinute, int hostTaskID) {
        super(taskName, taskDescription, startTime, durationInMinute);

        this.hostTaskID = hostTaskID;
    }

    public SubTask(int taskId, String taskName, Status status, String taskDescription, LocalDateTime startTime, int durationInMinute,  int hostTaskID) {
        super(taskId, taskName, status, taskDescription, startTime, durationInMinute);
        this.hostTaskID = hostTaskID;
    }

    public SubTask(SubTask subTask) {
        super(subTask.getId(), subTask.getTaskName(), subTask.getStatus(), subTask.getTaskDescription(), subTask.getStartTime(), subTask.getDuration());
        this.hostTaskID = subTask.getHostTaskID();
    }

    public int getHostTaskID() {
        return hostTaskID;
    }

    public void setHostTaskID(int hostTaskID) {
        this.hostTaskID = hostTaskID;
    }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return TaskType.SUB.name()+ ","
                + this.getId()+ ","
                + this.getTaskName()+ ","
                + this.getStatus()+ ","
                + getTaskDescription()+ ","
                + getStartTime().format(formatter)+ ","
                + getDuration()+ ","
                + hostTaskID;
    }
}
