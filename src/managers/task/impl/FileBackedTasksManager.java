package managers.task.impl;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enums.Status;
import enums.TaskType;
import excepton.ManagerSaveException;
import managers.history.HistoryManager;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class FileBackedTasksManager extends InMemoryTaskManager {

    private final String filePath;
    private final String historyPath;

    public FileBackedTasksManager(HistoryManager historyManager, String filePath) {
        super(historyManager);

        this.filePath = filePath+"\\tasks";
        this.historyPath = filePath + "\\tasks.h";

        load();
    }

    protected void load(){
        initTasks();
        initHistory();
    }

    private void initTasks() {
        List<String> taskList = readBackedTasks();

        if (!taskList.isEmpty()) {
            mapFromString(taskList);
        }
    }

    private void initHistory() {
        String historyIndexes = HistoryFileBackInitializer.readBackedHistory(historyPath);
        List<Integer> historyList = HistoryFileBackInitializer.historyFromString(historyIndexes);
        HistoryFileBackInitializer.init(this, historyList);
    }

    private List<String> readBackedTasks() {
        List<String> taskList = new ArrayList<>();

        if (new File(filePath).exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                while (reader.ready()) {
                    taskList.add(reader.readLine());
                }
            } catch (IOException e) {
                throw new ManagerSaveException();
            }
        }
        return taskList;
    }

    @Override
    public Task addTask(Task task) {
        Task current = super.addTask(task);
        save();
        return current;
    }

    @Override
    public SubTask addSubTask(SubTask task) {
        SubTask current = super.addSubTask(task);
        save();
        return current;
    }

    @Override
    public EpicTask addEpicTask(EpicTask task) {
        EpicTask current = super.addEpicTask(task);
        save();
        return current;
    }

    @Override
    public Task updateTask(Task task) {
        return super.updateTask(task);
    }

    @Override
    public EpicTask updateEpicTask(EpicTask newEpic) {
        var epic = super.updateEpicTask(newEpic);
        save();
        return epic;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        var sub = super.updateSubTask(task);
        save();
        return sub;
    }


    @Override
    public Task removeTask(int taskId) {
        var task = super.removeTask(taskId);
        save();
        return task;
    }

    @Override
    public EpicTask removeEpic(int taskId) {
        var task = super.removeEpic(taskId);
        save();
        return task;
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public SubTask removeSubTask(int taskId) {
        var task = super.removeSubTask(taskId);
        save();
        return task;
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public Task getTask(int taskId) {
        var temp = super.getTask(taskId);
        save();
        return temp;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public EpicTask getEpicTask(int taskId) {
        var temp = super.getEpicTask(taskId);
        save();
        return temp;
    }

    @Override
    public SubTask getSubTask(int taskId) {
        var temp = super.getSubTask(taskId);
        save();
        return temp;
    }




    protected void save() {
        saveTasks();
        HistoryFileBackInitializer.saveHistory(historyManager, historyPath);
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(taskMapToString());
            writer.write(epicMapToString());
            writer.write(subtaskMapToString());
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }


    protected String taskMapToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (var task: taskMap.values()) {
            stringBuilder.append(task.toString()).append("\n");
        }
        return stringBuilder.toString();
    }

    protected String epicMapToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (var task: epicTaskMap.values()) {
            stringBuilder.append(task.toString()).append("\n");
        }
        return stringBuilder.toString();
    }

    protected String subtaskMapToString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (var task: subTaskMap.values()) {
            stringBuilder.append(task.toString()).append("\n");
        }
        return stringBuilder.toString();
    }

    protected void mapFromString(List<String> taskList) {
        for (String task : taskList) {
            if (!task.isEmpty() & !task.isBlank()) {
                taskFromString(task);
            }
        }
    }

    private void taskFromString(String taskString) {
        String[] temp = taskString.split(",");

        TaskType type = TaskType.valueOf(temp[0]);
        int id = Integer.parseInt(temp[1]);
        String name = temp[2];
        Status status = Status.valueOf(temp[3]);
        String desc = temp[4];

        LocalDateTime startTime = null;
        if (!temp[5].equals("null")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            startTime = LocalDateTime.parse(temp[5], formatter);
        }
        int duration = Integer.parseInt(temp[6]);


        int hostId = 0;
        if (temp.length == 8) {
            hostId = Integer.parseInt(temp[7]);
        }

        switch (type) {
            case REGULAR: {
                taskMap.put(id, new Task(id, name, status, desc, startTime, duration));
                break;
            }
            case EPIC: {
                EpicTask epicTask = new EpicTask(id, name, desc);
                epicTask.resetStatus();
                epicTaskMap.put(id, epicTask);
                break;
            }
            case SUB: {
                EpicTask epicTask = epicTaskMap.get(hostId);
                if (epicTask != null) {
                    SubTask subTask = new SubTask(id, name, status, desc, startTime, duration, hostId);
                    subTaskMap.put(id, subTask);
                    epicTask.addSubTask(subTask);
                    epicTask.resetStatus();
                }
            }
        }
    }


    /**
     * Внутренний класс, отвечающий за бэкап истории
     */
    static class HistoryFileBackInitializer {

        static void saveHistory(HistoryManager historyManager, String historyPath) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyPath))) {
                writer.write(HistoryFileBackInitializer.historyToString(historyManager));
            } catch (IOException e) {
                throw new ManagerSaveException();
            }
        }

        static String readBackedHistory(String historyPath) {
            String historyIndexes = null;

            if (new File(historyPath).exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(historyPath))) {
                    if (reader.ready()) {
                        historyIndexes = reader.readLine();
                    }
                } catch (IOException e) {
                    throw new ManagerSaveException();
                }
            }
            return historyIndexes;
        }

        static List<Integer> historyFromString(String value) {
            if (value != null && !value.isEmpty()) {
                return Arrays.stream(value.split(","))
                        .map(Integer::valueOf)
                        .collect(Collectors.toList());
            }
            return new ArrayList<>();
        }

        static String historyToString(HistoryManager manager) {
            return manager.getHistory().stream()
                    .map(Task::getTaskId)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
       }

        static void init(FileBackedTasksManager manager, List<Integer> indexes) {
            for (Integer index : indexes) {
                manager.getTask(index);
                manager.getEpicTask(index);
                manager.getSubTask(index);
            }
        }
    }
}
