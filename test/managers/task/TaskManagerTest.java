package managers.task;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enums.Status;
import excepton.ValidateDateTimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    private int index = 1;

    private final Random random = new Random();

    public TaskManagerTest() {
        this.taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();


    @AfterEach
    public void clearAll() {
        taskManager.removeAllEpics();
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
    }

    @Test
    void shouldBeAThreeEpicTasksWithStatusNew() {
        taskManager.removeAllEpics();
        int count = taskManager.getEpicTaskList().size();

        taskManager.addEpicTask(generateEpicTask());
        taskManager.addEpicTask(generateEpicTask());
        taskManager.addEpicTask(generateEpicTask());

        List<EpicTask> epicTasks = taskManager.getEpicTaskList();

        assertEquals(count + 3, epicTasks.size());

        for (EpicTask epic : epicTasks) {
            assertEquals(Status.NEW, epic.getStatus());
        }
    }

    @Test
    void shouldThrowNPEEpicTest() {
        Executable executable = () -> taskManager.addEpicTask(null);
        assertThrows(NullPointerException.class, executable);
    }


    @Test
    void shouldBeAThreeTasksWithStatusNew() {
        int count = taskManager.getTaskList().size();

        Task task1 = generateRandomTask(null);
        taskManager.addTask(task1);
        taskManager.addTask(generateRandomTask(task1));
        taskManager.addTask(generateRandomTask(task1));

        List<Task> tasks = taskManager.getTaskList();

        assertEquals(count + 3, tasks.size());

        for (Task task : tasks) {
            assertEquals(Status.NEW, task.getStatus());
        }
    }

    @Test
    void shouldThrowNPETaskTest() {
        Executable executable = () -> taskManager.addTask(null);
        assertThrows(NullPointerException.class, executable);
    }

    @Test
    void shouldBeAThreeSubTaskWithStatusNew() {
        EpicTask epicTask = generateEpicTask();
        taskManager.addEpicTask(epicTask);
        int count = taskManager.getEpicTask(epicTask.getTaskId()).getSubTaskList().size();

        SubTask subTask1 = generateSubTask(null, epicTask.getTaskId());
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = generateSubTask(subTask1, epicTask.getTaskId());
        taskManager.addSubTask(generateSubTask(subTask2, epicTask.getTaskId()));

        SubTask subTask3 = generateSubTask(subTask2, epicTask.getTaskId());
        taskManager.addSubTask(generateSubTask(subTask3, epicTask.getTaskId()));

        List<SubTask> subTasks = taskManager.getSubtaskList();
        assertEquals(count + 3, subTasks.size());

        EpicTask epicTask1 = taskManager.getEpicTask(epicTask.getTaskId());
        assertEquals(count + 3, epicTask1.getSubTaskList().size());

        for (Task task : subTasks) {
            assertEquals(Status.NEW, task.getStatus());
        }
    }

    @Test
    void shouldThrowNPESubTaskTest() {
        Executable executable = () -> taskManager.addSubTask(null);
        assertThrows(NullPointerException.class, executable);
    }

    @Test
    void shouldBeZeroSizeRemoveAllTasksTest() {
        int subSize = taskManager.getTaskList().size();

        Task task = new Task(generateRandomTask(null));
        taskManager.addTask(task);
        taskManager.addTask(generateRandomTask(task));
        taskManager.addTask(generateRandomTask(task));
        assertEquals(subSize + 3, taskManager.getTaskList().size());

        taskManager.removeAllTasks();

        assertEquals(0, taskManager.getTaskList().size());
    }

    @Test
    void shouldBeZeroSizeRemoveAllEpicTasks() {
        int subSize = taskManager.getEpicTaskList().size();

        taskManager.addEpicTask(generateEpicTask());
        taskManager.addEpicTask(generateEpicTask());
        taskManager.addEpicTask(generateEpicTask());
        assertEquals(subSize + 3, taskManager.getEpicTaskList().size());

        taskManager.removeAllEpics();

        assertEquals(0, taskManager.getEpicTaskList().size());
    }

    @Test
    void shouldBeZeroSizeRemoveAllSubtasks() {
        int subSize = taskManager.getSubtaskList().size();

        EpicTask epicTask = generateEpicTask();
        taskManager.addEpicTask(epicTask);

        SubTask subTask1 = generateSubTask(null, epicTask.getTaskId());
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = generateSubTask(subTask1, epicTask.getTaskId());
        taskManager.addSubTask(generateSubTask(subTask2, epicTask.getTaskId()));

        SubTask subTask3 = generateSubTask(subTask2, epicTask.getTaskId());
        taskManager.addSubTask(generateSubTask(subTask3, epicTask.getTaskId()));

        SubTask subTask4 = generateSubTask(subTask3, epicTask.getTaskId());
        taskManager.addSubTask(generateSubTask(subTask4, epicTask.getTaskId()));

        assertEquals(subSize + 4, taskManager.getSubtaskList().size());

        taskManager.removeAllSubtasks();

        assertEquals(0, taskManager.getSubtaskList().size());
    }

    @Test
    void addTaskTest() {
        int size = taskManager.getTaskList().size();

        Task task = generateRandomTask(null);
        taskManager.addTask(task);
        assertEquals(size + 1, taskManager.getTaskList().size());
        assertEquals(Status.NEW, task.getStatus());

        Executable executable = () -> taskManager.addTask(null);
        assertThrows(NullPointerException.class, executable);
    }

    @Test
    void getTaskTest() {
        Task task = generateRandomTask(null);
        taskManager.addTask(task);

        assertEquals(task, taskManager.getTask(task.getTaskId()));
        assertEquals(Status.NEW, task.getStatus());

        assertNull(taskManager.getTask(123234455));
    }

    @Test
    void updateTaskTest() {
        Task task = generateRandomTask(null);
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTask(task.getTaskId()));
        assertEquals(Status.NEW, taskManager.getTask(task.getTaskId()).getStatus());

        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task);
        assertEquals(Status.IN_PROGRESS, taskManager.getTask(task.getTaskId()).getStatus());

        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        assertEquals(Status.DONE, taskManager.getTask(task.getTaskId()).getStatus());
    }

    @Test
    void removeTaskTest() {
        int count = taskManager.getTaskList().size();
        Task task = generateRandomTask(null);
        taskManager.addTask(task);

        assertEquals(count + 1, taskManager.getTaskList().size());
        var taskList = taskManager.getTaskList();
        assertTrue(taskList.contains(task));

        taskManager.removeTask(task.getTaskId());
        assertEquals(count, taskManager.getTaskList().size());
        taskList = taskManager.getTaskList();
        assertFalse(taskList.contains(task));
    }

    @Test
    void addEpicTaskTest() {
        int size = taskManager.getEpicTaskList().size();

        EpicTask task = generateEpicTask();
        taskManager.addEpicTask(task);
        assertEquals(size + 1, taskManager.getEpicTaskList().size());
        assertEquals(Status.NEW, task.getStatus());

        Executable executable = () -> taskManager.addEpicTask(null);
        assertThrows(RuntimeException.class, executable);
    }

    @Test
    void getEpicTaskTest() {
        EpicTask task = generateEpicTask();
        taskManager.addEpicTask(task);

        assertEquals(task, taskManager.getEpicTask(task.getTaskId()));
        assertEquals(Status.NEW, task.getStatus());

        assertNull(taskManager.getEpicTask(123234455));
    }

    @Test
    void removeEpicTaskTest() {
        int count = taskManager.getEpicTaskList().size();
        EpicTask task = generateEpicTask();
        taskManager.addEpicTask(task);

        assertEquals(count + 1, taskManager.getEpicTaskList().size());
        var taskList = taskManager.getEpicTaskList();
        assertTrue(taskList.contains(task));

        taskManager.removeEpic(task.getTaskId());
        assertEquals(count, taskManager.getEpicTaskList().size());

        taskList = taskManager.getEpicTaskList();
        assertFalse(taskList.contains(task));
    }

    @Test
    void addSubTaskTest() {
        int size = taskManager.getSubtaskList().size();

        //добавление подзадач в эпик и проверка, что все ок
        EpicTask task = generateEpicTask();
        taskManager.addEpicTask(task);
        SubTask subTask1 = generateSubTask(null, task.getTaskId());
        taskManager.addSubTask(subTask1);
        SubTask subTask2 = generateSubTask(subTask1, task.getTaskId());
        taskManager.addSubTask(generateSubTask(subTask2, task.getTaskId()));
        SubTask subTask3 = generateSubTask(subTask2, task.getTaskId());
        taskManager.addSubTask(generateSubTask(subTask3, task.getTaskId()));
        assertEquals(size + 3, taskManager.getSubtaskList().size());


        //попытка добавления подзадачи к несуществующему эпику
        SubTask subTask4 = generateSubTask(subTask3, task.getTaskId());
        Executable executable = () -> taskManager.addSubTask(generateSubTask(subTask4,234234234));
        assertThrows(RuntimeException.class, executable);
    }

    @Test
    void getSubTaskTest() {
        Task task = generateRandomTask(null);
        taskManager.addTask(task);

        assertEquals(task, taskManager.getTask(task.getTaskId()));
        assertEquals(Status.NEW, task.getStatus());

        assertNull(taskManager.getTask(123234455));
    }

    @Test
    void updateSubTaskTest() {
        taskManager.removeAllEpics();

        EpicTask epicTask = generateEpicTask();
        taskManager.addEpicTask(epicTask);

        SubTask task = generateSubTask(null, epicTask.getTaskId());
        taskManager.addSubTask(task);

        assertEquals(task, taskManager.getSubTask(task.getTaskId()));
        assertEquals(Status.NEW, taskManager.getSubTask(task.getTaskId()).getStatus());

        task.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(task);
        assertEquals(Status.IN_PROGRESS, taskManager.getSubTask(task.getTaskId()).getStatus());

        task.setStatus(Status.DONE);
        taskManager.updateSubTask(task);
        assertEquals(Status.DONE, taskManager.getSubTask(task.getTaskId()).getStatus());
    }

    @Test
    void removeSubTaskTest() {
        int count = taskManager.getSubtaskList().size();
        EpicTask epicTask = generateEpicTask();
        taskManager.addEpicTask(epicTask);

        int countEpic = epicTask.getSubTaskList().size();
        assertEquals(0, countEpic);

        SubTask task = generateSubTask(null, epicTask.getTaskId());
        taskManager.addSubTask(task);
        assertEquals(count + 1, taskManager.getSubtaskList().size());
        assertEquals(countEpic + 1, epicTask.getSubTaskList().size());

        var taskList = taskManager.getSubtaskList();
        assertTrue(taskList.contains(task));
        taskManager.removeSubTask(task.getTaskId());

        count = taskManager.getSubtaskList().size();
        assertEquals(count, taskManager.getSubtaskList().size());
    }

    @Test
    void getHistoryTest() {
        //создание и добавление задач в историю
        Task task0 = generateRandomTask(null);
        Task task1 = generateRandomTask(task0);
        Task task2 = generateRandomTask(task1);
        taskManager.addTask(task0);
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.getTask(task0.getTaskId());
        taskManager.getTask(task1.getTaskId());
        taskManager.getTask(task2.getTaskId());
        var history = taskManager.getHistory();


        //проверка, что задачи расположены в верном порядке
        assertEquals(task0, history.get(0));
        assertEquals(task1, history.get(1));
        assertEquals(task2, history.get(2));


        //получение задач и проверка изменения истории
        taskManager.getTask(task2.getTaskId());
        taskManager.getTask(task0.getTaskId());
        taskManager.getTask(task1.getTaskId());
        history = taskManager.getHistory();
        assertEquals(task2, history.get(0));
        assertEquals(task0, history.get(1));
        assertEquals(task1, history.get(2));


        //добавление новой задачи и проверка истории
        Task task3 = generateRandomTask(task2);
        taskManager.addTask(task3);
        taskManager.getTask(task3.getTaskId());
        history = taskManager.getHistory();
        assertEquals(task2, history.get(0));
        assertEquals(task0, history.get(1));
        assertEquals(task1, history.get(2));
        assertEquals(task3, history.get(3));


        //создание новой задачи и попытка добавления ее несколько раз
        Task task4 = generateRandomTask(task3);
        taskManager.addTask(task4);
        Executable executable1 = () -> taskManager.addTask(task4);
        assertThrows(ValidateDateTimeException.class, executable1);
        Executable executable2 = () -> taskManager.addTask(task4);
        assertThrows(ValidateDateTimeException.class, executable2);
        Executable executable3 = () -> taskManager.addTask(task4);
        assertThrows(ValidateDateTimeException.class, executable3);
        Executable executable4 = () -> taskManager.addTask(task4);
        assertThrows(ValidateDateTimeException.class, executable4);
        history = taskManager.getHistory();
        assertEquals(4, history.size());


        //получение новой задачи и проверка соответствия истории
        taskManager.getTask(task4.getTaskId());
        history = taskManager.getHistory();
        assertEquals(task2, history.get(0));
        assertEquals(task0, history.get(1));
        assertEquals(task1, history.get(2));
        assertEquals(task3, history.get(3));
        assertEquals(task4, history.get(4));


        //добавление подзадач
        EpicTask epicTask = generateEpicTask();
        taskManager.addEpicTask(epicTask);
        SubTask sub0 = generateSubTask(null, epicTask.getTaskId());
        SubTask sub1 = generateSubTask(sub0, epicTask.getTaskId());
        SubTask sub2 = generateSubTask(sub1, epicTask.getTaskId());
        taskManager.addSubTask(sub0);
        taskManager.addSubTask(sub1);
        taskManager.addSubTask(sub2);
        taskManager.getSubTask(sub2.getTaskId());
        taskManager.getSubTask(sub0.getTaskId());
        taskManager.getSubTask(sub1.getTaskId());

        history = taskManager.getHistory();
        assertEquals(task2, history.get(0));
        assertEquals(task0, history.get(1));
        assertEquals(task1, history.get(2));
        assertEquals(task3, history.get(3));
        assertEquals(task4, history.get(4));
        assertEquals(sub2, history.get(5));
        assertEquals(sub0, history.get(6));
        assertEquals(sub1, history.get(7));


        //перемешивание истории и сверка
        taskManager.getTask(task0.getTaskId());
        taskManager.getTask(task4.getTaskId());
        taskManager.getTask(task2.getTaskId());
        taskManager.getSubTask(sub2.getTaskId());

        history = taskManager.getHistory();
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
        assertEquals(sub0, history.get(2));
        assertEquals(sub1, history.get(3));
        assertEquals(task0, history.get(4));
        assertEquals(task4, history.get(5));
        assertEquals(task2, history.get(6));
        assertEquals(sub2, history.get(7));
    }


    @Test
    public void getPrioritizedTasks() {
        var list = taskManager.getPrioritizedTasks();
        assertEquals(0, list.size());

        //сортировка по часам
        Task task1 = generateCurrentTask("TASK 1", 2022, 10, 10, 8, 0, 10);
        Task task2 = generateCurrentTask("TASK 2", 2022, 10, 10, 9, 0, 10);
        Task task3 = generateCurrentTask("TASK 3", 2022, 10, 10, 10, 0, 10);

        //сортировка по дням
        Task task4 = generateCurrentTask("TASK 4", 2022, 10, 11, 10, 0, 10);
        Task task5 = generateCurrentTask("TASK 5", 2022, 10, 12, 10, 0, 10);
        Task task6 = generateCurrentTask("TASK 6", 2022, 10, 13, 10, 0, 10);

        //рандомное добавление задач
        taskManager.addTask(task2);
        taskManager.addTask(task6);
        taskManager.addTask(task4);
        taskManager.addTask(task5);
        taskManager.addTask(task1);
        taskManager.addTask(task3);

        //сверка
        list = taskManager.getPrioritizedTasks();
        assertEquals(6, list.size());
        assertEquals(task1, list.get(0));
        assertEquals(task2, list.get(1));
        assertEquals(task3, list.get(2));
        assertEquals(task4, list.get(3));
        assertEquals(task5, list.get(4));
        assertEquals(task6, list.get(5));



        //удаление некоторых задач
        taskManager.removeTask(1);
        taskManager.removeTask(2);
        taskManager.removeTask(3);


        //создание новых задач
        Task new1 = generateCurrentTask("NEW 1", 2022, 7, 13, 10, 0, 10);
        taskManager.addTask(new1);
        Task new2 = generateCurrentTask("NEW 2", 2022, 8, 13, 10, 0, 10);
        taskManager.addTask(new2);
        Task new3 = generateCurrentTask("NEW 3", 2022, 12, 13, 10, 0, 10);
        taskManager.addTask(new3);
        list = taskManager.getPrioritizedTasks();
        assertEquals(6, list.size());




        assertEquals(new1, list.get(0));
        assertEquals(new2, list.get(1));
        assertEquals(task1, list.get(2));
        assertEquals(task3, list.get(3));
        assertEquals(task5, list.get(4));
        assertEquals(new3, list.get(5));
    }

    protected Task generateRandomTask(Task task) {
        if (task == null) {
            return new Task("task_" + index, "task_" + index++, getDefaultLocalDateTime(), random.nextInt(500));
        }
        return new Task(task.getTaskName() + "_new" + index, "task_" + index++, getDefaultLocalDateTime().plusDays(random.nextInt(1000)), random.nextInt(500));
    }

    protected EpicTask generateEpicTask() {
        return new EpicTask("Epic task_" + index, "epic_" + index++);
    }

    protected SubTask generateSubTask(SubTask task, int epicId) {
        if (task == null) {
            return new SubTask("Sub task_" + index, "sub_" + index++,
                    getDefaultLocalDateTime().plusDays(random.nextInt(1000)), random.nextInt(500), epicId);
        }
        return new SubTask(task.getTaskName() + "_new Sub task_" + index, "sub_" + index++,
                getDefaultLocalDateTime().plusDays(random.nextInt(1000)), random.nextInt(500), epicId);
    }

    private LocalDateTime getDefaultLocalDateTime() {
        LocalDate date = LocalDate.of(2022, random.nextInt(11) + 1, random.nextInt(26) + 1);
        LocalTime time = LocalTime.of(random.nextInt(24), random.nextInt(59), random.nextInt(59));
        return LocalDateTime.of(date, time);
    }

    private Task generateCurrentTask(String name, int year, int month, int day, int hour, int min, int duration) {
        return new Task(name, "task_" + index++,
                getDefaultLocalDateTime(year, month, day, hour, min), duration);
    }

    private SubTask generateCurrentSubTask(String name, int year, int month, int day, int hour, int min, int duration, int epicId) {
        return new SubTask(name, "sub_" + index++,
                getDefaultLocalDateTime(year, month, day, hour, min), duration, epicId);
    }

    private LocalDateTime getDefaultLocalDateTime(int year, int month, int day, int hour, int min) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalTime time = LocalTime.of(hour, min, 1);
        return LocalDateTime.of(date, time);
    }
}