package enity;

import enity.task.status.Status;

import java.util.LinkedList;
import java.util.Objects;

public class EpicTask extends Task {

    private final LinkedList<SubTask> subTaskList;

    public EpicTask(String taskName, String taskDescription) {
        super(taskName, taskDescription);

        subTaskList = new LinkedList<>();
    }

    public void addSubTask(SubTask task){
        subTaskList.push(task);
    }

    public void subTaskRemove(SubTask subTask){
        subTaskList.remove(subTask);
    }

    public LinkedList<SubTask> getSubTaskList() {
        return subTaskList;
    }

    public LinkedList<Task> getSubTaskList2() {
        return new LinkedList<>(subTaskList);
    }

    public void resetStatus(){
        int DONE = 0;
        int IN_PROGRESS = 0;

        for (SubTask subTask: subTaskList ) {
            switch (subTask.getStatus()){
                case DONE: {DONE++; break;}
                case IN_PROGRESS:{IN_PROGRESS++; break;}
            }

            if (DONE == subTaskList.size()){
                setStatus(Status.DONE);
            } else if (IN_PROGRESS > 0) {
                setStatus(Status.IN_PROGRESS);
            } else {
                setStatus(Status.NEW);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EpicTask epicTask = (EpicTask) o;
        return Objects.equals(subTaskList, epicTask.subTaskList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subTaskList);
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subTaskList=" + subTaskList +
                '}';
    }
}
