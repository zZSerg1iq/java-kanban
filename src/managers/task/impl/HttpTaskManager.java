package managers.task.impl;

import adaper.LocalDateTimeAdapter;
import com.google.gson.*;
import http.client.KVTaskClient;
import managers.Managers;

import java.time.LocalDateTime;

public class HttpTaskManager extends FileBackedTasksManager {

    private static String defaultFilepath = "resources";

    private final KVTaskClient client;
    private final Gson gson;


    public HttpTaskManager(String url) {
        super(Managers.getDefaultHistory(), defaultFilepath);

        client = new KVTaskClient(url);
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        load();
    }

    @Override
    protected void save() {
        String task = taskMapToString();
        client.put("taskMap", gson.toJson(task));

        String epic = epicMapToString();
        client.put("epicMap", gson.toJson(epic));

        String subtask = subtaskMapToString();
        client.put("subtaskMap", gson.toJson(subtask));

      //  System.out.println("-history-");
        client.put("history", gson.toJson(getHistory()));
    }

    @Override
    protected void load() {
        if (client == null) {
            return;
        }

        System.out.println(client.load("taskMap"));
        System.out.println(client.load("epicMap"));
        System.out.println(client.load("subtaskMap"));
        System.out.println(client.load("history"));


        //List<Task> tasksFromServer = gson.fromJson(client.load("taskMap"), new TypeToken<List<Task>>();
    }
}
