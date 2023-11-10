import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import managers.Managers;
import managers.task.TaskManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    private final static int TASK_COUNT = 50;

    public static void main(String[] args) {
        var manager = Managers.loadFromFile(new File("d:/1.txt"));

        //new Main().setUpValues(manager);
        new Main().getTest(manager);
    }

    private void setUpValues(TaskManager manager) {
        Random r = new Random();
        List<Task> tasks = new ArrayList<>();
        List<EpicTask> epicTasks = new ArrayList<>();

        for (int i = 1; i < TASK_COUNT; i++) {
            int x = r.nextInt(12);
            if (x <= 4) {
                Task task = new Task("task_" + i, "desc");
                task.setTaskId(i);
                manager.addTask(task);
                tasks.add(task);
            } else if (x <= 6) {
                EpicTask epicTask = new EpicTask("Epic_" + i, "epic desc");
                epicTask.setTaskId(i);
                epicTasks.add(epicTask);
                manager.addEpicTask(epicTask);
            } else {
                if (epicTasks.size() > 0) {
                    EpicTask epicTask = epicTasks.get(r.nextInt(epicTasks.size()));
                    SubTask task = new SubTask("Sub_" + i, "sub desc", epicTask.getTaskId());
                    task.setTaskId(i);
                    manager.addSubTask(task);
                    epicTask.addSubTask(task);
                }
            }
        }

        tasks.forEach(task -> {
            manager.getTask(task.getTaskId());
        });

        Task task = new Task("task_" + 100, "desc");
        task.setTaskId(100);
        manager.addTask(task);
    }

    private void getTest(TaskManager manager) {
        manager.test();


        System.out.println("history: ");
        var l = manager.getHistory();

        for (var d : l) {
            System.out.println(d);
        }

        for (int i = 0; i < TASK_COUNT; i++) {

            SubTask sub = manager.getSubTask(i);
            Task task = manager.getTask(i);
            EpicTask epic = manager.getEpicTask(i);

            if (task != null) {
                System.out.println("Task: " + task);
            }
            if (epic != null) {
                System.out.println("Epic: " + epic);
            }
            if (sub != null) {
                System.out.println("Sub: " + sub);
            }
        }

        System.out.println("history: ");
        var l2 = manager.getHistory();

        for (var d : l2) {
            System.out.println(d);
        }

    }

}
