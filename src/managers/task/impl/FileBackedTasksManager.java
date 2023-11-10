package managers.task.impl;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enity.task.status.Status;
import enity.task.type.TaskType;
import excepton.ManagerSaveException;
import managers.history.HistoryManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FileBackedTasksManager extends InMemoryTaskManager {

    private final String filePath;
    private final String historyPath;

    public FileBackedTasksManager(HistoryManager historyManager, String filePath) {
        super(historyManager);

        this.filePath = filePath;
        this.historyPath = filePath + ".h";
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

    /**
     * Не было четких указаний как именно надо делать бэк, я решил, делать в двух разных файлах.
     */
    private void save() {
        saveTasks();
        HistoryFileBackInitializer.saveHistory(historyManager, historyPath);
    }

    private void saveTasks() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(mapToString(taskMap) + "\n");
            writer.write(mapToString(epicTaskMap) + "\n");
            writer.write(mapToString(subTaskMap) + "\n");
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }

    private <T extends Task> String mapToString(Map<Integer, T> taskMap) {
        return taskMap.values().stream()
                .map(Task::toString)
                .collect(Collectors.joining("\n"));
    }

    private void mapFromString(List<String> taskList) {
        for (String task : taskList) {
            taskFromString(task);
        }
    }

    private void taskFromString(String taskString) {
        String[] temp = taskString.split(",");
        TaskType type = TaskType.valueOf(temp[0]);
        int id = Integer.parseInt(temp[1]);
        String name = temp[2];
        Status status = Status.valueOf(temp[3]);
        String desc = temp[4];
        int hostId = 0;
        if (temp.length == 6) {
            hostId = Integer.parseInt(temp[5]);
        }

        switch (type) {
            case REGULAR: {
                taskMap.put(id, new Task(id, type, name, desc, status));
                break;
            }
            case EPIC: {
                epicTaskMap.put(id, new EpicTask(id, type, name, desc, status));
                break;
            }
            case SUB: {
                subTaskMap.put(id, new SubTask(id, type, name, desc, status, hostId));
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
