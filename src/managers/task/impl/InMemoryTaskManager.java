package managers.task.impl;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enums.Status;
import excepton.ValidateDateTimeException;
import managers.history.HistoryManager;
import managers.task.TaskManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    private int tasksNumber = 1;
    protected final Map<Integer, EpicTask> epicTaskMap;
    protected final Map<Integer, SubTask> subTaskMap;
    protected final Map<Integer, Task> taskMap;
    private final Set<Task> sortedTaskList;
    private final Map<String, Task> taskDaTimeMap;

    protected final HistoryManager historyManager;


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        taskMap = new HashMap<>();
        epicTaskMap = new HashMap<>();
        subTaskMap = new HashMap<>();
        taskDaTimeMap = new HashMap<>();
        sortedTaskList = new TreeSet<>();
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
        if (task == null || task.getClass() != Task.class) {
            throw new NullPointerException("Task is not valid");
        }

        addTaskDateTime(task);

        task.setId(initId(task.getId()));
        task.setStatus(Status.NEW);

        sortedTaskList.add(task);
        return taskMap.put(task.getId(), task);
    }

    @Override
    public Task getTask(int taskId) {
        if (taskMap.containsKey(taskId)) {
            historyManager.add(taskMap.get(taskId));
        }

        /*Task task = taskMap.get(taskId);

        if (task != null){
            return new Task(task);
        }*/
        return taskMap.get(taskId);
    }

    @Override
    public Task updateTask(Task task) {
        if (task == null || task.getClass() != Task.class) {
            throw new NullPointerException("Task is not valid");
        }

        if (taskMap.containsKey(task.getId())) {
            updateTaskDateTime(task);
            return taskMap.put(task.getId(), task);
        }
        throw new RuntimeException("Task " + task + " not found");
    }

    @Override
    public Task removeTask(int taskId) {
        Task task = taskMap.remove(taskId);

        if (task != null) {
            sortedTaskList.remove(task);
            historyManager.remove(task.getId());
            removeTaskDateTime(task);
        }
        return task;
    }

    @Override
    public void removeAllTasks() {
        for (Task task : taskMap.values()) {
            sortedTaskList.remove(task);
            historyManager.remove(task.getId());
            removeTaskDateTime(task);
        }
        taskMap.clear();
    }

    @Override
    public EpicTask addEpicTask(EpicTask task) {
        if (task == null || task.getClass() != EpicTask.class) {
            throw new NullPointerException("Epic task is not valid");
        }
        task.setStatus(Status.NEW);
        task.setId(initId(task.getId()));
        return epicTaskMap.put(task.getId(), task);
    }

    @Override
    public EpicTask getEpicTask(int taskId) {
        if (epicTaskMap.containsKey(taskId)) {
            historyManager.add(epicTaskMap.get(taskId));
        }

        var epic = epicTaskMap.get(taskId);

        if (epic != null){
            return new EpicTask(epic);
        }
        return null;
    }

    @Override
    public EpicTask updateEpicTask(EpicTask task) {
        if (task == null || task.getClass() != EpicTask.class) {
            throw new NullPointerException("Epic task is not valid");
        }

        EpicTask oldEpic = epicTaskMap.get(task.getId());
        if (oldEpic != null) {
            epicTaskMap.put(task.getId(), task);
            task.resetStatus();
            return oldEpic;
        }
        throw new RuntimeException("Task " + task + " not found");
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
    public EpicTask removeEpic(int taskId) {
        EpicTask epicTask = epicTaskMap.get(taskId);

        if (epicTask != null) {
            var subTaskList = epicTask.getSubTaskList();
            for (SubTask subtask : subTaskList) {
                subTaskMap.remove(subtask.getId());
                sortedTaskList.remove(subtask);
                historyManager.remove(subtask.getId());
                removeTaskDateTime(subtask);
            }
            epicTaskMap.remove(taskId);
        }
        return epicTask;
    }


    @Override
    public void removeAllEpics() {
        Iterator<EpicTask> iterator = epicTaskMap.values().iterator();

        while (iterator.hasNext()) {
            EpicTask epicTask = iterator.next();
            var subTaskList = epicTask.getSubTaskList();

            for (SubTask subtask : subTaskList) {
                subTaskMap.remove(subtask.getId());
                historyManager.remove(subtask.getId());
                removeTaskDateTime(subtask);
            }
            iterator.remove();
        }

    }

    @Override
    public SubTask addSubTask(SubTask task) {
        if (task == null || task.getClass() != SubTask.class) {
            throw new NullPointerException("SubTask task is not valid");
        }

        addTaskDateTime(task);

        task.setId(initId(task.getId()));
        task.setStatus(Status.NEW);

        EpicTask epicTask = epicTaskMap.get(task.getHostTaskID());
        if (epicTask != null) {
            epicTask.addSubTask(task);
            epicTask.resetStatus();
        } else {
            throw new RuntimeException("Host class for task " + task + " not found");
        }

        sortedTaskList.add(task);
        return subTaskMap.put(task.getId(), task);
    }

    @Override
    public SubTask getSubTask(int taskId) {
        if (subTaskMap.containsKey(taskId)) {
            historyManager.add(subTaskMap.get(taskId));
        }

        SubTask subTask = subTaskMap.get(taskId);
        if (subTask != null){
            return new SubTask(subTask);
        }
        return null;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        if (task == null || task.getClass() != SubTask.class) {
            throw new NullPointerException("SubTask task is not valid");
        }

        if (subTaskMap.containsKey(task.getId())) {
            updateTaskDateTime(task);

            SubTask subTask = subTaskMap.put(task.getId(), task);
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
            if (epicTask != null) {
                epicTask.subTaskRemove(subTask);
                epicTask.resetStatus();
                sortedTaskList.remove(subTask);
                historyManager.remove(subTask.getId());
                removeTaskDateTime(subTask);
            }
        }

        return subTask;
    }

    @Override
    public void removeAllSubtasks() {
        for (Integer id : subTaskMap.keySet()) {
            removeSubTask(id);
            historyManager.remove(id);
            removeTaskDateTime(subTaskMap.get(id));
        }
        subTaskMap.clear();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTaskList);
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

    private void addTaskDateTime(Task task){
        LocalDateTime newTaskStartTime = task.getStartTime();
        LocalDateTime newTaskEndTime = task.getStartTime().plusMinutes(task.getDuration());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

        if (taskDaTimeMap.containsKey(newTaskStartTime.format(formatter)) || taskDaTimeMap.containsKey(newTaskEndTime.format(formatter))) {
            if (taskDaTimeMap.containsKey(newTaskStartTime.format(formatter))){
                throw new ValidateDateTimeException("Time interval of the task '"+task+"' intersects with another task: " + taskDaTimeMap.get(newTaskStartTime.format(formatter)));
            }
            throw new ValidateDateTimeException("Time interval of the task '"+task+"' intersects with another task: " + taskDaTimeMap.get(newTaskEndTime.format(formatter)));

        } else {
            LocalDateTime range = task.getStartTime();
            while (range.isBefore(newTaskEndTime)) {
                taskDaTimeMap.put(range.format(formatter), task);
                range = range.plusMinutes(1);
            }
        }
    }

    private void updateTaskDateTime(Task task) {
        Task oldTask;
        if (task.getClass().equals(Task.class)){
            oldTask = taskMap.get(task.getId());
        } else {
            oldTask = subTaskMap.get(task.getId());
        }

        removeTaskDateTime(oldTask);

        LocalDateTime newTaskStartTime = task.getStartTime();
        LocalDateTime newTaskEndTime = task.getStartTime().plusMinutes(task.getDuration());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

        if (taskDaTimeMap.containsKey(newTaskStartTime.format(formatter)) || taskDaTimeMap.containsKey(newTaskEndTime.format(formatter))) {
            addTaskDateTime(oldTask);
            throw new ValidateDateTimeException("Time interval of the task '"+task.getTaskName()+"' intersects with another task");
        } else {
            addTaskDateTime(task);
        }

    }


    private void removeTaskDateTime(Task task) {
        if (task == null){
            return;
        }
        Iterator<Task> iterator = taskDaTimeMap.values().iterator();
            while (iterator.hasNext()){
                Task current = iterator.next();
                if (current.equals(task)){
                    iterator.remove();
                }
            }
    }

}
