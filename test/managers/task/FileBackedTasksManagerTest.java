package managers.task;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enity.task.status.Status;
import managers.Managers;
import managers.task.impl.FileBackedTasksManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    private Random random = new Random();

    @Override
    protected FileBackedTasksManager createTaskManager() {
        return Managers.loadFromFile(new File("resources/tasks"));
    }


    @Test
    public void bothFileShouldBeACorrectlyWrote() {
        /**
         * проверка файла тасков
         */
        //добавляю несколько задач, проверяю корректность записи файла
        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            taskList.add(generateTask());
            taskManager.addTask(taskList.get(i));
        }
        Assertions.assertTrue(readTaskBackedFile(taskList));

        //добавляю несколько эпиков, проверяю корректность записи файла
        List<EpicTask> epicTasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            EpicTask epicTask = generateEpicTask();
            epicTasks.add(epicTask);
            taskList.add(epicTask);
            taskManager.addEpicTask(epicTask);
        }
        Assertions.assertTrue(readTaskBackedFile(taskList));


        //добавляю несколько сабов в эпики, проверяю корректность записи файла
        List<SubTask> subTasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int subCount = random.nextInt(10) + 1;
            EpicTask epicTask = epicTasks.get(i);

            for (int j = 0; j < subCount; j++) {
                SubTask subTask = generateSubTask(epicTask.getTaskId());
                subTasks.add(subTask);
                taskList.add(subTask);
                taskManager.addSubTask(subTask);
            }

        }
        Assertions.assertTrue(readTaskBackedFile(taskList));


        //изменяю статусы сабов, статусы эпиков меняются сами,
        //проверяю корректность записи файла
        for (SubTask sub : subTasks) {
            int statusKey = random.nextInt(3);
            Status status;
            switch (statusKey) {
                case 0: {
                    status = Status.NEW;
                    break;
                }
                case 1: {
                    status = Status.IN_PROGRESS;
                    break;
                }
                default:
                    status = Status.DONE;
            }
            sub.setStatus(status);
            taskManager.updateSubTask(sub);
        }
        Assertions.assertTrue(readTaskBackedFile(taskList));


        /**
         * проверка файла истории
         */
        //заполнение листа истории
        List<Task> historyList = new ArrayList<>();
        for (int i = 0; i < taskList.size(); i++) {
            int randomTask = random.nextInt(taskList.size());
            historyList.add(taskList.get(randomTask));
            taskManager.getTask(taskList.get(randomTask).getTaskId());
        }
        Collections.reverse(historyList);
        Assertions.assertTrue(readTaskBackedFile(taskList));
    }


    private boolean readTaskBackedFile(List<Task> taskList) {
        int line = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("resources/tasks"))) {
            while (bufferedReader.ready()) {
                String s = bufferedReader.readLine();
                if (!s.isEmpty() && !s.isBlank()) {
                    if (!s.equals(taskList.get(line).toString())) {
                        //System.out.println(s + "  |  " + taskList.get(line).toString());
                        return false;
                    }
                    line++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

}