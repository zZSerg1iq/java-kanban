package management;

import management.history.HistoryManager;
import management.history.impl.InMemoryHistoryManager;
import management.task.TaskManager;
import management.task.impl.InMemoryTaskManager;

public class Managers {

    /*�������, � ����� ����� ���������� ����� ������ :\  */

    private static TaskManager taskManager;
    private static HistoryManager historyManager;


    public static TaskManager getDefault() {
        if (taskManager == null) {
            taskManager = new InMemoryTaskManager(getDefaultHistory());
        }
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null){
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }
}
