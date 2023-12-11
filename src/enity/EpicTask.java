package enity;

import enums.Status;
import enums.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;

public class EpicTask extends Task {

    private final LinkedList<SubTask> subTaskList;

    private LocalDateTime mostEarliestDate = null;

    private LocalDateTime mostLatestDate = null;

    private int totalDuration = 0;
    private int lastDuration = 0;


    public EpicTask(String taskName, String taskDescription) {
        super(taskName, taskDescription, null, 0);

        subTaskList = new LinkedList<>();
    }


    public EpicTask(int taskId, String taskName, String taskDescription) {
        super(taskId, taskName, Status.NEW, taskDescription, null, 0);

        subTaskList = new LinkedList<>();
    }

    public EpicTask(EpicTask epicTask) {
        super(epicTask.getId(), epicTask.getTaskName(), null, epicTask.getTaskDescription(), null, 0);

        subTaskList = new LinkedList<>();

        for (SubTask s: epicTask.getSubTaskList() ) {
            SubTask subTask = new SubTask(s);
            addSubTask(subTask);
        }

        resetStatus();
        updateDateTime();
    }

    public void addSubTask(SubTask subTask) {
        subTaskList.add(subTask);
        Collections.sort(subTaskList);
        increaseDuration(subTask.getDuration());
        updateDateTime();
    }

    public void subTaskRemove(SubTask subTask) {
            subTaskList.remove(subTask);
            Collections.sort(subTaskList);
            decreaseDuration(subTask.getDuration());
            updateDateTime();
    }

    public LinkedList<SubTask> getSubTaskList() {
        return new LinkedList<>(subTaskList);
    }

    @Override
    public LocalDateTime getStartTime() {
        return mostEarliestDate;
    }

    @Override
    public LocalDateTime getEndTime() {
        return mostLatestDate.plusMinutes(lastDuration);
    }

    @Override
    public int getDuration() {
        return totalDuration;
    }

    private void increaseDuration(int duration) {
        totalDuration += duration;
    }

    private void decreaseDuration(int duration) {
        totalDuration -= duration;
    }

    private void updateDateTime() {
        if (!subTaskList.isEmpty()) {
            mostEarliestDate = subTaskList.getFirst().getStartTime();
            mostLatestDate = subTaskList.getLast().getStartTime();
            lastDuration = subTaskList.getLast().getDuration();
        } else {
            mostEarliestDate = null;
            mostLatestDate = null;
            lastDuration = 0;
        }

    }

    public void resetStatus() {
        if (getStatus() == null) {
            setStatus(Status.NEW);
        }

        int done = 0;
        int inProgress = 0;

        for (SubTask subTask : subTaskList) {
            switch (subTask.getStatus()) {
                case DONE:
                    done++;
                    break;
                case IN_PROGRESS:
                    inProgress++;
                    break;
            }
        }

        if (subTaskList.size() > 0 & done == subTaskList.size()) {
            setStatus(Status.DONE);
        } else if (inProgress > 0) {
            setStatus(Status.IN_PROGRESS);
        } else {
            setStatus(Status.NEW);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EpicTask epicTask = (EpicTask) o;
        return totalDuration == epicTask.totalDuration && lastDuration == epicTask.lastDuration && Objects.equals(mostEarliestDate, epicTask.mostEarliestDate) && Objects.equals(mostLatestDate, epicTask.mostLatestDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mostEarliestDate, mostLatestDate, totalDuration, lastDuration);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return TaskType.EPIC.name() + ","
                + this.getId() + ","
                + this.getTaskName() + ","
                + this.getStatus() + ","
                + getTaskDescription() + ","
                + (getStartTime() != null ? getStartTime().format(formatter) : "null") + ","
                + getDuration();
    }


}
