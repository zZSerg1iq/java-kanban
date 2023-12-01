import enity.SubTask;
import enity.Task;
import excepton.ValidateDateTimeException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Test {

    private Map<String, Task> taskDaTimeMap = new HashMap<>();

    private void addTaskDateTime(Task task) {
        LocalDateTime newTaskStartTime = task.getStartTime();
        LocalDateTime newTaskEndTime = task.getStartTime().plusMinutes(task.getDuration());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

        if (taskDaTimeMap.containsKey(newTaskStartTime.format(formatter)) || taskDaTimeMap.containsKey(newTaskEndTime.format(formatter))) {
            throw new ValidateDateTimeException("Time interval of the task '"+task.getTaskName()+"' intersects with another task");
        } else {
            LocalDateTime range = task.getStartTime();
            while (range.isBefore(newTaskEndTime)) {
                taskDaTimeMap.put(range.format(formatter), task);
                range = range.plusMinutes(1);
            }
        }
    }

    private void removeTaskDateTime(Task task) {
        Iterator<Task> iterator = taskDaTimeMap.values().iterator();
        while (iterator.hasNext()){
            Task current = iterator.next();
            if (current.equals(task)){
                iterator.remove();
            }
        }
    }

    private Task generateTask(String name, int year, int month, int day, int hour, int min, int duration) {
        return new Task(name, "task_",
                getDefaultLocalDateTime(year, month, day, hour, min), duration);
    }

    private SubTask generateSubTask(String name, int year, int month, int day, int hour, int min, int duration, int epicId) {
        return new SubTask(name, "sub_",
                getDefaultLocalDateTime(year, month, day, hour, min), duration, epicId);
    }

    private LocalDateTime getDefaultLocalDateTime(int year, int month, int day, int hour, int min) {
        LocalDate date = LocalDate.of(year, month, day);
        LocalTime time = LocalTime.of(hour, min, 0);
        return LocalDateTime.of(date, time);
    }

    public static void main(String[] args) {
        new Test().run();
    }

    void run() {
        Task task1 = generateTask("1", 2023, 10, 15, 10, 1, 10);
        addTaskDateTime(task1);
        System.out.println(taskDaTimeMap.size());

        Task task2 = generateTask("2", 2023, 10, 15, 12, 50, 20);
        addTaskDateTime(task2);
        System.out.println(taskDaTimeMap.size());

        Task task3 = generateTask("2", 2023, 10, 15, 15, 0, 30);
        addTaskDateTime(task3);
        System.out.println(taskDaTimeMap.size());
        taskDaTimeMap.forEach((s, task) -> System.out.println(s));
        System.out.println("===============");
        removeTaskDateTime(task2);
        System.out.println(taskDaTimeMap.size());
        taskDaTimeMap.forEach((s, task) -> System.out.println(s));
    }
}
