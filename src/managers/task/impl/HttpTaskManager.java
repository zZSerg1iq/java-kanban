package managers.task.impl;

import http.client.KVTaskClient;
import managers.Managers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private static final String defaultFilepath = "resources";

    private final KVTaskClient client;

    public HttpTaskManager(String url) {
        super(Managers.getDefaultHistory(), defaultFilepath);
        client = new KVTaskClient(url);
        load();
    }

    @Override
    protected void save() {
        String taskMap = taskMapToString();
        if (!taskMap.isEmpty()) {
            client.put("taskMap", taskMap);
        }

        String epicMap = epicMapToString();
        if (!epicMap.isEmpty()) {
            client.put("epicMap", epicMap);
        }

        String subtaskMap = subtaskMapToString();
        if (!subtaskMap.isEmpty()) {
            client.put("subtaskMap", subtaskMap);
        }

        String history = getHistoryString();
        if (!history.isEmpty()) {
            client.put("history", history);
        }
    }

    @Override
    protected void load() {
        if (client == null) {
            return;
        }

        List<String> taskList = Arrays
                .stream(client.load("taskMap").split("\n"))
                .collect(Collectors.toList());

        if (!taskList.contains("Данные по ключу не найдены")) {
            taskList.forEach(this::tasksBackFromString);
            System.out.println("Восстановлено: "+taskList.size() + " задач");
        }

        List<String> epicList = Arrays
                .stream(client.load("epicMap").split("\n"))
                .collect(Collectors.toList());
        if (!epicList.contains("Данные по ключу не найдены")) {
            epicList.forEach(this::tasksBackFromString);
            System.out.println("Восстановлено: "+epicList.size() + " эпиков");
        }

        List<String> subTaskList = Arrays
                .stream(client.load("subtaskMap").split("\n"))
                .collect(Collectors.toList());
        if (!epicList.contains("Данные по ключу не найдены")) {
            subTaskList.forEach(this::tasksBackFromString);
            System.out.println("Восстановлено: "+subTaskList.size() + " сабтасков");
        }

        String history = client.load("history");
        if (!"Данные по ключу не найдены".equals(history)) {
            initHistory(history);
            System.out.println("История восстановлена");
        }
    }

}
