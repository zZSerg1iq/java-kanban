package managers.task;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enity.task.status.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    private int index = 0;

    private final Random random = new Random();

    public TaskManagerTest() {
        this.taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();


    @AfterEach
    public void clearAll() {
        taskManager.removeAllEpicTasks();
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
    }

    @Test
    void shouldBeAThreeEpicTasksWithStatusNew() {
        taskManager.removeAllEpicTasks();

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

        taskManager.addTask(generateTask());
        taskManager.addTask(generateTask());
        taskManager.addTask(generateTask());

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

        taskManager.addSubTask(generateSubTask(epicTask.getTaskId()));
        taskManager.addSubTask(generateSubTask(epicTask.getTaskId()));
        taskManager.addSubTask(generateSubTask(epicTask.getTaskId()));

        List<SubTask> subTasks = taskManager.getSubtaskList();

        assertEquals(count + 3, taskManager.getEpicTask(epicTask.getTaskId()).getSubTaskList().size());

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

        taskManager.addTask(generateTask());
        taskManager.addTask(generateTask());
        taskManager.addTask(generateTask());
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

        taskManager.removeAllEpicTasks();

        assertEquals(0, taskManager.getEpicTaskList().size());
    }

    @Test
    void shouldBeZeroSizeRemoveAllSubtasks() {
        int subSize = taskManager.getSubtaskList().size();

        EpicTask epicTask = generateEpicTask();
        taskManager.addEpicTask(epicTask);

        taskManager.addSubTask(generateSubTask(epicTask.getTaskId()));
        taskManager.addSubTask(generateSubTask(epicTask.getTaskId()));
        taskManager.addSubTask(generateSubTask(epicTask.getTaskId()));
        taskManager.addSubTask(generateSubTask(epicTask.getTaskId()));
        assertEquals(subSize + 4, taskManager.getSubtaskList().size());

        taskManager.removeAllSubtasks();

        assertEquals(0, taskManager.getSubtaskList().size());
    }

    @Test
    void addTaskTest() {
        int size = taskManager.getTaskList().size();

        Task task = generateTask();
        taskManager.addTask(task);
        assertEquals(size + 1, taskManager.getTaskList().size());
        assertEquals(Status.NEW, task.getStatus());

        Executable executable = () -> taskManager.addTask(null);
        assertThrows(NullPointerException.class, executable);
    }

    @Test
    void getTaskTest() {
        Task task = generateTask();
        taskManager.addTask(task);

        assertEquals(task, taskManager.getTask(task.getTaskId()));
        assertEquals(Status.NEW, task.getStatus());

        assertNull(taskManager.getTask(123234455));
    }

    @Test
    void updateTaskTest() {
        Task task = generateTask();
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
        Task task = generateTask();
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

        taskManager.removeEpicTask(task.getTaskId());
        assertEquals(count, taskManager.getEpicTaskList().size());

        taskList = taskManager.getEpicTaskList();
        assertFalse(taskList.contains(task));
    }

    @Test
    void addSubTaskTest() {
        int size = taskManager.getSubtaskList().size();

        EpicTask task = generateEpicTask();
        taskManager.addEpicTask(task);
        taskManager.addSubTask(generateSubTask(task.getTaskId()));
        taskManager.addSubTask(generateSubTask(task.getTaskId()));
        taskManager.addSubTask(generateSubTask(task.getTaskId()));

        assertEquals(size + 3, taskManager.getSubtaskList().size());

        Executable executable = () -> taskManager.addSubTask(generateSubTask(234234234));
        assertThrows(RuntimeException.class, executable);
    }

    @Test
    void getSubTaskTest() {
        Task task = generateTask();
        taskManager.addTask(task);

        assertEquals(task, taskManager.getTask(task.getTaskId()));
        assertEquals(Status.NEW, task.getStatus());

        assertNull(taskManager.getTask(123234455));
    }

    @Test
    void updateSubTaskTest() {
        taskManager.removeAllEpicTasks();

        EpicTask epicTask = generateEpicTask();
        taskManager.addEpicTask(epicTask);

        SubTask task = generateSubTask(epicTask.getTaskId());
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

        SubTask task = generateSubTask(epicTask.getTaskId());
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
        Task task1 = generateTask("GET HISTORY1");
        Task task2 = generateTask("GET HISTORY2");
        Task task3 = generateTask("GET HISTORY3");

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        //добавление в историю
        taskManager.getTask(task2.getTaskId());
        taskManager.getTask(task1.getTaskId());
        taskManager.getTask(task3.getTaskId());

        var history = taskManager.getHistory();
        assertEquals(3, history.size());

        //проверка того, что элементы в верном порядке
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
        assertEquals(task3, history.get(2));

        //получение одного и проверка, что он переместился в конце
        taskManager.getTask(task1.getTaskId());
        history = taskManager.getHistory();
        assertEquals(task1, history.get(2));

        //получение одного и проверка, что он переместился в конце
        taskManager.getTask(task3.getTaskId());
        history = taskManager.getHistory();
        assertEquals(task3, history.get(2));

        //добавление 3-х одинаковых тасков и проверка, что в таск листе 4 итема, а не 6
        Task task4 = generateTask();
        taskManager.addTask(task4);
        taskManager.addTask(task4);
        taskManager.addTask(task4);
        assertEquals(4, taskManager.getTaskList().size());

        //получение одного и того же несколько раз и проверка, что он переместился в конец и нет дублирований
        taskManager.getTask(task4.getTaskId());
        taskManager.getTask(task4.getTaskId());
        taskManager.getTask(task4.getTaskId());
        taskManager.getTask(task4.getTaskId());
        taskManager.getTask(task4.getTaskId());
        history = taskManager.getHistory();
        assertEquals(4, history.size());
        assertEquals(task4, history.get(3));
        for (int i = 0; i < history.size(); i++) {
            assertEquals(task2, history.get(0));
            assertEquals(task1, history.get(1));
            assertEquals(task3, history.get(2));
            assertEquals(task4, history.get(3));
        }
    }


    @Test
    public void getPrioritizedTasks(){
        var list = taskManager.getPrioritizedTasks();
        assertEquals(0, list.size());

        Task task0 = generateTask("TASK 0", 2022, 10, 10, 10, 10);
        Task task1 = generateTask("TASK 1",2022, 10, 10, 10, 11);
        Task task2 = generateTask("TASK 2",2022, 10, 10, 10, 12);
        Task task3 = generateTask("TASK 3",2023, 10, 11, 10, 10);
        Task task4 = generateTask("TASK 4",2023, 10, 12, 10, 10);
        Task task5 = generateTask("TASK 5",2023, 10, 13, 10, 10);
        taskManager.addTask(task1);
        taskManager.addTask(task5);
        taskManager.addTask(task3);
        taskManager.addTask(task4);
        taskManager.addTask(task0);
        taskManager.addTask(task2);

        list = taskManager.getPrioritizedTasks();
        assertEquals(6, list.size());

        assertEquals(task0, list.get(0));
        assertEquals(task1, list.get(1));
        assertEquals(task2, list.get(2));
        assertEquals(task3, list.get(3));
        assertEquals(task4, list.get(4));
        assertEquals(task5, list.get(5));

        taskManager.removeTask(0);
        taskManager.removeTask(3);
        taskManager.removeTask(4);

        Task new1 = generateTask("NEW 1",2022, 7, 13, 10, 10);
        taskManager.addTask(new1);
        Task new2 = generateTask("NEW 2",2022, 8, 13, 10, 10);
        taskManager.addTask(new2);
        Task new3 = generateTask("NEW 3",2022, 12, 13, 10, 10);
        taskManager.addTask(new3);
        list = taskManager.getPrioritizedTasks();
        assertEquals(6, list.size());

        assertEquals(new1, list.get(0));
        assertEquals(new2, list.get(1));
        assertEquals(task2, list.get(2));
        assertEquals(new3, list.get(3));
        assertEquals(task3, list.get(4));
        assertEquals(task5, list.get(5));
    }

    protected Task generateTask() {
        return new Task("task_" + index, "task_" + index++,
                getDefaultLocalDateTime(), random.nextInt(500));
    }
    protected Task generateTask(String name) {
        return new Task(name, "task_" + index++,
                getDefaultLocalDateTime(), random.nextInt(500));
    }

    protected EpicTask generateEpicTask() {
        return new EpicTask("Epic task_" + index, "epic_" + index++);
    }

    protected SubTask generateSubTask(int epicId) {
        return new SubTask("Sub task_" + index, "sub_" + index++,
                getDefaultLocalDateTime(), random.nextInt(500), epicId);
    }

    private LocalDateTime getDefaultLocalDateTime() {
        LocalDate date = LocalDate.of(2022, random.nextInt(11) + 1, random.nextInt(26) + 1);
        LocalTime time = LocalTime.of(random.nextInt(24), random.nextInt(59), random.nextInt(59));
        return LocalDateTime.of(date, time);
    }

    private Task generateTask(String name, int year, int month, int day, int hour, int min) {
        return new Task(name, "task_" + index++,
                getDefaultLocalDateTime(year, month, day, hour, min), random.nextInt(500));
    }

    private SubTask generateSubTask(String name, int year, int month, int day, int hour, int min, int epicId) {
        return new SubTask(name, "sub_" + index++,
                getDefaultLocalDateTime(year, month, day, hour, min), random.nextInt(500), epicId);
    }

    private LocalDateTime getDefaultLocalDateTime(int year, int month, int day, int hour, int min) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalTime time = LocalTime.of(hour, min, 1);
        return LocalDateTime.of(date, time);
    }
}