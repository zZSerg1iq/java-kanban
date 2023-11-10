package enity;

import enity.task.status.Status;
import enity.task.type.TaskType;

import java.util.Objects;


public class Task {

    private int taskId = 0;
    private TaskType type;
    private String taskName;
    private String taskDescription;
    private Status status;

    public Task(String taskName, String taskDescription) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;

        typeFieldInit();
    }

    public Task(int taskId, TaskType type, String taskName, String taskDescription, Status status) {
        this.taskId = taskId;
        this.type = type;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.status = status;
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

    @Override
    public String toString() {
        return TaskType.REGULAR.name()+ ","
                + this.getTaskId()+ ","
                + this.getTaskName()+ ","
                + this.getStatus()+ ","
                + getTaskDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && type == task.type && Objects.equals(taskName, task.taskName) && Objects.equals(taskDescription, task.taskDescription) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, type, taskName, taskDescription, status);
    }
}
