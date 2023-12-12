package managers;

import adaper.LocalDateTimeAdapter;
import adaper.StatusAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.Status;
import managers.history.HistoryManager;
import managers.history.impl.InMemoryHistoryManager;
import managers.task.TaskManager;
import managers.task.impl.FileBackedTasksManager;
import managers.task.impl.HttpTaskManager;
import managers.task.impl.InMemoryTaskManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {

    static {
        defaultGson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .create();
    }

    private final static Gson defaultGson;

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static TaskManager getDefault(String url) {
        return new HttpTaskManager(url);
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        checkFile(file);
        return new FileBackedTasksManager(new InMemoryHistoryManager(), file.getPath());
    }

    private static void checkFile(File file) {
        if (file == null || file.getPath().isEmpty()) {
            throw new RuntimeException("Target file path is empty.");
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getDefaultGson(){
        return defaultGson;
    }
}