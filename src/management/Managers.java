package management;

import management.history.HistoryManager;
import management.history.impl.InMemoryHistoryManager;
import management.task.TaskManager;
import management.task.impl.InMemoryTaskManager;

public class Managers {

    /*
    Вот по тому я и сомневался)
    Я рассуждал с той точки зрения, что это как раз таки полная противоположность фабрики, а скорее "хранитель"
    */

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}