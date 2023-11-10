package managers;

import managers.history.HistoryManager;
import managers.history.impl.InMemoryHistoryManager;
import managers.task.TaskManager;
import managers.task.impl.FileBackedTasksManager;
import managers.task.impl.InMemoryTaskManager;

import java.io.File;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
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


}