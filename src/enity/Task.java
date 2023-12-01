package enity;

import enity.task.status.Status;
import enity.task.type.TaskType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Task implements Comparable<Task> {

    @Override
    public int compareTo(Task otherTask) {
        if (this.getStartTime() == null){
            return -1;
        } else if (otherTask.getStartTime() == null){
            return -1;
        }
        return this.startTime.compareTo(otherTask.startTime);
    }

    private int taskId = 0;
    private TaskType type;
    private String taskName;
    private String taskDescription;
    private Status status;
    private LocalDateTime startTime;
    private int duration = 0;

    public Task(String taskName, String taskDescription, LocalDateTime startTime, int durationInMinutes) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.startTime = startTime;
        this.duration = durationInMinutes;

        typeFieldInit();
    }

    public Task(int taskId, String taskName, Status status, String taskDescription, LocalDateTime startTime, int durationInMinutes) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.status = status;
        this.taskDescription = taskDescription;
        this.startTime = startTime;
        this.duration = durationInMinutes;

        typeFieldInit();
    }

    private void typeFieldInit() {
        String className = this.getClass().getName();

        if (className.contains("Epic")) {
            type = TaskType.EPIC;
        } else if (className.contains("Sub")) {
            type = TaskType.SUB;
        } else {
            type = TaskType.REGULAR;
        }
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public int getTaskId() {
        return taskId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }


    public LocalDateTime getStartTime() {
        return startTime;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime(){
        if (startTime != null) {
            return startTime.plusMinutes(duration);
        } else
            throw new RuntimeException("");
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return TaskType.REGULAR.name()+ ","
                + this.getTaskId()+ ","
                + this.getTaskName()+ ","
                + this.getStatus()+ ","
                + getTaskDescription()+ ","
                + this.startTime.format(formatter)+ ","
                + this.duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && duration == task.duration && type == task.type && Objects.equals(taskName, task.taskName) && Objects.equals(taskDescription, task.taskDescription) && status == task.status && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, type, taskName, taskDescription, status, startTime, duration);
    }
}
