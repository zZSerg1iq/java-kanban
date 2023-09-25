import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import management.Managers;
import management.history.HistoryManager;
import management.task.TaskManager;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        for (int i = 0; i < 20; i++) {
            taskManager.addTask(getRandomTask(i));
        }

        HistoryManager historyManager = Managers.getDefaultHistory();
        historyManager.getHistory().forEach(System.out::println);
    }

    private static Task getRandomTask(int i) {
        Random random = new Random();
        int x = random.nextInt(11) + 1;
        if (x <= 5) {
            return new Task("regular_" + i, "");
        } else
            return new EpicTask("epic_" + i, "");
    }

}
