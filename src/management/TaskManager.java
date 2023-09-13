package management;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enity.task.status.Status;
import enity.task.type.TaskType;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;


public class TaskManager {

    private int tasksNumber = 0;
    protected final EnumMap<TaskType, HashMap<Integer, Task>> taskTypeMap;

    public TaskManager() {
        taskTypeMap = new EnumMap<>(TaskType.class);
    }



    public ArrayList<Task> getSubtaskList() {
        return new ArrayList<>(taskTypeMap.get(TaskType.SUB).values());
    }

    public ArrayList<Task> getEpicTaskList() {
        return new ArrayList<>(taskTypeMap.get(TaskType.EPIC).values());
    }

    public ArrayList<Task> getRegularTaskList() {
        return new ArrayList<>(taskTypeMap.get(TaskType.REGULAR).values());
    }



    public void removeAllSubtasks() {
        var subTasks = taskTypeMap.get(TaskType.SUB);
        for (Integer ID: subTasks.keySet()) {
            removeSubtask(ID);
        }
        taskTypeMap.get(TaskType.SUB).clear();
    }

    public void removeAllEpicTasks() {
        var epicTasks = taskTypeMap.get(TaskType.EPIC);
        for (Integer ID: epicTasks.keySet()) {
            removeEpicTask(ID);
        }
    }

    public void removeAllRegularTasks() {
        taskTypeMap.get(TaskType.REGULAR).clear();
    }



    public Task getSubtask(int taskId) {
        return taskTypeMap.get(TaskType.SUB).get(taskId);
    }

    public Task getEpicTask(int taskId) {
        return taskTypeMap.get(TaskType.EPIC).get(taskId);
    }

    public Task getRegularTask(int taskId) {
        return taskTypeMap.get(TaskType.REGULAR).get(taskId);
    }



    public Task addTask(Task task) {
        task.setTaskId(++tasksNumber);
        task.setStatus(Status.NEW);

        var taskHashMap = taskTypeMap.computeIfAbsent(task.getType(), k -> new HashMap<>());

        if (task.getType() == TaskType.SUB) {
            SubTask subTask = (SubTask) task;
            EpicTask epicTask = (EpicTask) taskTypeMap.get(TaskType.EPIC).get(subTask.getHostTaskID());
            if (epicTask != null) {
                epicTask.addSubTask(subTask);
            }
        }

        taskHashMap.put(task.getTaskId(), task);
        taskTypeMap.put(task.getType(), taskHashMap);

        if (taskHashMap.get(tasksNumber) == null){
           return task;
        }
        return taskHashMap.put(tasksNumber, task);
    }



    public Task updateTask(Task task) {
        var taskHashMap = taskTypeMap.get(task.getType());

        if (taskHashMap != null) {
            if (task.getType() == TaskType.SUB){
                EpicTask epicTask = (EpicTask) taskTypeMap
                        .get(TaskType.EPIC)
                        .get(((SubTask) task).getHostTaskID());
                epicTask.resetStatus();
            }
            return taskHashMap.put(tasksNumber, task);
        }
        return null;
    }



    public Task removeSubtask(int taskId) {
        SubTask subTask = (SubTask) taskTypeMap.get(TaskType.SUB).get(taskId);
        if (subTask != null) {
            EpicTask epicTask = (EpicTask) taskTypeMap.get(TaskType.EPIC).get(subTask.getHostTaskID());
            epicTask.subTaskRemove(subTask);
            epicTask.resetStatus();
            taskTypeMap.get(TaskType.SUB).remove(taskId);
        }

        return subTask;
    }

    public Task removeEpicTask(int taskId) {
        EpicTask epicTask = (EpicTask) taskTypeMap.get(TaskType.EPIC).get(taskId);
        var subTaskList = epicTask.getSubTaskList();
        var subTaskHashMap = taskTypeMap.get(TaskType.SUB);

        for (Task subtask: subTaskList) {
            subTaskHashMap.remove(subtask.getTaskId());
        }
        return taskTypeMap.get(TaskType.EPIC).remove(taskId);
    }

    public Task removeRegularTask(int taskId) {
        return taskTypeMap.get(TaskType.REGULAR).remove(taskId);
    }


    public List<SubTask> getEpicSubTaskList(int taskId){
        var taskHashMap = taskTypeMap.get(TaskType.EPIC);
        EpicTask epic = (EpicTask)taskHashMap.get(taskId);
        if (epic != null){
            return epic.getSubTaskList();
        }
        return null;
    }

    protected EnumMap<TaskType, HashMap<Integer, Task>> getTaskTypeMap(){
        return taskTypeMap;
    }

}
