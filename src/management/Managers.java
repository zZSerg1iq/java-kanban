package management;

import management.history.HistoryManager;
import management.history.impl.InMemoryHistoryManager;
import management.task.TaskManager;
import management.task.impl.InMemoryTaskManager;

public class Managers {

    /*
    ��� �� ���� � � ����������)
    � ��������� � ��� ����� ������, ��� ��� ��� ��� ���� ������ ����������������� �������, � ������ "���������"
    */

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}