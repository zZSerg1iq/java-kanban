package management;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enity.task.status.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TaskManager {

    private int tasksNumber = 0;
    private final HashMap<Integer, EpicTask> epicTaskMap;
    private final HashMap<Integer, SubTask> subTaskMap;
    private final HashMap<Integer, Task> taskMap;

    public TaskManager() {
        taskMap = new HashMap<>();
        epicTaskMap = new HashMap<>();
        subTaskMap = new HashMap<>();
    }


    public ArrayList<EpicTask> getEpicTaskList() {
        return new ArrayList<>(epicTaskMap.values());
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskMap.values());
    }

    public ArrayList<SubTask> getSubtaskList() {
        return new ArrayList<>(subTaskMap.values());
    }


    public Task addTask(Task task) {
        task.setTaskId(++tasksNumber);
        task.setStatus(Status.NEW);

        return taskMap.put(task.getTaskId(), task);
    }

    public Task getTask(int taskId) {
        return taskMap.get(taskId);
    }

    public Task updateTask(Task task) {
        if (taskMap.containsKey(task.getTaskId())) {
            return taskMap.put(task.getTaskId(), task);
        }
        throw new RuntimeException("Task " + task + " not found");
    }

    public Task removeTask(int taskId) {
        return taskMap.remove(taskId);
    }

    public void removeAllTasks() {
        taskMap.clear();
    }


    public EpicTask addEpicTask(EpicTask task) {
        task.setTaskId(++tasksNumber);
        task.setStatus(Status.NEW);

        return epicTaskMap.put(task.getTaskId(), task);
    }

    public EpicTask getEpicTask(int taskId) {
        return epicTaskMap.get(taskId);
    }

    //Во избежание ошибок, у меня статус эпика рассчитывает сам эпик. И я конечно же забыл его позвать :(
    //Спасибо )
    public EpicTask updateEpicTask(EpicTask newEpic) {
        EpicTask oldEpic = epicTaskMap.get(newEpic.getTaskId());
        if (oldEpic != null) {
            epicTaskMap.put(newEpic.getTaskId(), newEpic);
            newEpic.resetStatus();
            return oldEpic;
        }
        throw new RuntimeException("Task " + newEpic + " not found");
    }

    public List<SubTask> getEpicSubTaskList(int taskId) {
        EpicTask epic = epicTaskMap.get(taskId);
        if (epic != null) {
            return epic.getSubTaskList();
        }
        return null;
    }

    public EpicTask removeEpicTask(int taskId) {
        EpicTask epicTask = epicTaskMap.get(taskId);

        if (epicTask != null) {
            var subTaskList = epicTask.getSubTaskList();
            for (SubTask subtask : subTaskList) {
                subTaskList.remove(subtask.getTaskId());
            }
            epicTaskMap.remove(taskId);
        }
        return epicTask;
    }

    public void removeAllEpicTasks() {
        for (Integer ID : epicTaskMap.keySet()) {
            removeEpicTask(ID);
        }
    }


    public SubTask addSubTask(SubTask task) {
        task.setTaskId(++tasksNumber);
        task.setStatus(Status.NEW);

        EpicTask epicTask = epicTaskMap.get(task.getHostTaskID());
        if (epicTask != null) {
            epicTask.addSubTask(task);
            epicTask.resetStatus();
        } else {
            throw new RuntimeException("Host class for task " + task + " not found");
        }

        return subTaskMap.put(task.getTaskId(), task);
    }

    public SubTask getSubTask(int taskId) {
        return subTaskMap.get(taskId);
    }

    public SubTask updateSubTask(SubTask task) {
        if (subTaskMap.containsKey(task.getTaskId())) {
            SubTask subTask = subTaskMap.put(task.getTaskId(), task);
            epicTaskMap.get(task.getHostTaskID()).resetStatus();
            return subTask;
        }
        throw new RuntimeException("Task " + task + " not found");
    }

    public SubTask removeSubTask(int taskId) {
        SubTask subTask = subTaskMap.get(taskId);
        if (subTask != null) {
            EpicTask epicTask = epicTaskMap.get(subTask.getHostTaskID());
            epicTask.subTaskRemove(subTask);
            epicTask.resetStatus();
            subTaskMap.remove(taskId);
        }

        return subTask;
    }

    public void removeAllSubtasks() {
        for (Integer ID : subTaskMap.keySet()) {
            removeSubTask(ID);
        }
        subTaskMap.clear();
    }

    /*
    Спасибо за ревью)
    Не понимаю, как я не замечаю таких очевидных косяков сам. Стыдно(
    */

}
