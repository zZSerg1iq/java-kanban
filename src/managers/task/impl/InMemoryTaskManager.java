package managers.task.impl;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enity.task.status.Status;
import managers.history.HistoryManager;
import managers.task.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryTaskManager implements TaskManager {
    @Override
    public void test() {
        epicTaskMap.forEach((integer, epicTask) -> System.out.println(epicTask));
        subTaskMap.forEach((integer, epicTask) -> System.out.println(epicTask));
        taskMap.forEach((integer, epicTask) -> System.out.println(epicTask));
    }

    private int tasksNumber = 0;
    protected final Map<Integer, EpicTask> epicTaskMap;
    protected final Map<Integer, SubTask> subTaskMap;
    protected final Map<Integer, Task> taskMap;

    protected final HistoryManager historyManager;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        taskMap = new HashMap<>();
        epicTaskMap = new HashMap<>();
        subTaskMap = new HashMap<>();
    }

    @Override
    public List<EpicTask> getEpicTaskList() {
        return new ArrayList<>(epicTaskMap.values());
    }

    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<SubTask> getSubtaskList() {
        return new ArrayList<>(subTaskMap.values());
    }

    @Override
    public Task addTask(Task task) {
        task.setTaskId(initId(task.getTaskId()));
        task.setStatus(Status.NEW);
        return taskMap.put(task.getTaskId(), task);
    }

    @Override
    public Task getTask(int taskId) {
        if (taskMap.containsKey(taskId)) {
            historyManager.add(taskMap.get(taskId));
        }
        return taskMap.get(taskId);
    }

    @Override
    public Task updateTask(Task task) {
        if (taskMap.containsKey(task.getTaskId())) {
            return taskMap.put(task.getTaskId(), task);
        }
        throw new RuntimeException("Task " + task + " not found");
    }

    @Override
    public Task removeTask(int taskId) {
        return taskMap.remove(taskId);
    }

    @Override
    public void removeAllTasks() {
        taskMap.clear();
    }

    @Override
    public EpicTask addEpicTask(EpicTask task) {
        task.setTaskId(initId(task.getTaskId()));
        task.setStatus(Status.NEW);

        return epicTaskMap.put(task.getTaskId(), task);
    }

    @Override
    public EpicTask getEpicTask(int taskId) {
        if (epicTaskMap.containsKey(taskId)) {
            historyManager.add(epicTaskMap.get(taskId));
        }
        return epicTaskMap.get(taskId);
    }

    @Override
    public EpicTask updateEpicTask(EpicTask newEpic) {
        EpicTask oldEpic = epicTaskMap.get(newEpic.getTaskId());
        if (oldEpic != null) {
            epicTaskMap.put(newEpic.getTaskId(), newEpic);
            newEpic.resetStatus();
            return oldEpic;
        }
        throw new RuntimeException("Task " + newEpic + " not found");
    }

    @Override
    public List<SubTask> getEpicSubTaskList(int taskId) {
        EpicTask epic = epicTaskMap.get(taskId);
        if (epic != null) {
            return epic.getSubTaskList();
        }
        return null;
    }

    @Override
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

    @Override
    public void removeAllEpicTasks() {
        for (Integer ID : epicTaskMap.keySet()) {
            removeEpicTask(ID);
        }
    }

    @Override
    public SubTask addSubTask(SubTask task) {
        task.setTaskId(initId(task.getTaskId()));
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

    @Override
    public SubTask getSubTask(int taskId) {
        if (subTaskMap.containsKey(taskId)) {
            historyManager.add(subTaskMap.get(taskId));
        }
        return subTaskMap.get(taskId);
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        if (subTaskMap.containsKey(task.getTaskId())) {
            SubTask subTask = subTaskMap.put(task.getTaskId(), task);
            epicTaskMap.get(task.getHostTaskID()).resetStatus();
            return subTask;
        }
        throw new RuntimeException("Task " + task + " not found");
    }

    @Override
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

    @Override
    public void removeAllSubtasks() {
        for (Integer ID : subTaskMap.keySet()) {
            removeSubTask(ID);
        }
        subTaskMap.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private int initId(int id) {
        if (id != 0) {
            return id;
        }

        while (mapContainsId(tasksNumber)) {
            tasksNumber++;
        }
        return tasksNumber;
    }

    private boolean mapContainsId(int id) {
        return taskMap.containsKey(id) || epicTaskMap.containsKey(id) || subTaskMap.containsKey(id);
    }
}
