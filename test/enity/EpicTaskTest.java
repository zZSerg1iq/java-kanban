package enity;

import enums.Status;
import managers.Managers;
import managers.task.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTaskTest {

    private static TaskManager manager;
    private static EpicTask epicTask;
    private int index = 1;
    private Random random = new Random();


    @BeforeAll
    static void setUp() {
        manager = Managers.loadFromFile(new File("resources"));
    }

    @BeforeEach
    public void addNewEpicForTest() {
        epicTask = new EpicTask("New Epic", "Epic");
        manager.addEpicTask(epicTask);
    }

    @Test
    void shouldBeNewStatusOnEpicJustCreated() {
        assertEquals(epicTask.getStatus(), Status.NEW);
    }

    @Test
    void shouldBeNewStatusWhenAllSubTaskIsNEW() {
        SubTask subTask1 = generateCurrentSubTask("Sub task1",  2020, 1,1,1,1,1, epicTask.getId());
        SubTask subTask2 = generateCurrentSubTask("Sub task2",  2022, 1,1,2,1,1, epicTask.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        assertEquals(epicTask.getStatus(), Status.NEW);
        assertEquals(2, epicTask.getSubTaskList().size());
    }

    @Test
    void shouldBeNewStatusWhenAllSubTaskWasDeleted() {
        SubTask subTask1 = generateCurrentSubTask("Sub task1",  2024, 1,1,1,1,1, epicTask.getId());
        SubTask subTask2 = generateCurrentSubTask("Sub task2",  2026, 1,1,1,1,1, epicTask.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.getEpicTask(epicTask.getId());

        manager.removeAllSubtasks();
        assertEquals(Status.NEW, epicTask.getStatus());
    }

    @Test
    void shouldBeNewStatusWhenOneTaskIsNewAndOneIsDone() {
        SubTask subTask1 = new SubTask("Sub task1", "sub 1", getDefaultLocalDateTime(), 50, epicTask.getId());
        SubTask subTask2 = new SubTask("Sub task2", "sub 2", getDefaultLocalDateTime(), 50, epicTask.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        subTask1.setStatus(Status.DONE);
        manager.updateSubTask(subTask1);

        assertEquals(Status.NEW, epicTask.getStatus());
    }

    @Test
    void shouldBeInProgressStatusWhenOneOfSubtasksIsInProgress() {
        SubTask subTask1 = new SubTask("Sub task1", "sub 1", getDefaultLocalDateTime(), 50, epicTask.getId());
        SubTask subTask2 = new SubTask("Sub task2", "sub 2", getDefaultLocalDateTime(), 50, epicTask.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        subTask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask1);

        assertEquals(Status.IN_PROGRESS, epicTask.getStatus());
    }

    @Test
    void shouldBeDoneStatusIfAllOfSubTasksIsDone() {
        SubTask subTask1 = new SubTask("Sub task1", "sub 1", getDefaultLocalDateTime(), 50, epicTask.getId());
        SubTask subTask2 = new SubTask("Sub task2", "sub 2", getDefaultLocalDateTime(), 50, epicTask.getId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        manager.updateSubTask(subTask1);
        manager.updateSubTask(subTask2);

        assertEquals(Status.DONE, epicTask.getStatus());
    }

    @Test
    void shouldShowCorrectAllTimeStats() {
        LocalDate date = LocalDate.of(2000, 1, 1);
        LocalTime time = LocalTime.of(12, 12, 12);
        LocalDateTime startTime = LocalDateTime.of(date, time);

        // добавление сабов
        SubTask subTask1 = new SubTask("Sub task1", "sub 1", startTime, 50, epicTask.getId());
        manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Sub task2", "sub 2", startTime.minusDays(10), 50, epicTask.getId());
        manager.addSubTask(subTask2);
        SubTask subTask3 = new SubTask("Sub task3", "sub 3", startTime.minusDays(3), 50, epicTask.getId());
        manager.addSubTask(subTask3);
        SubTask subTask4 = new SubTask("Sub task4", "sub 4", startTime.plusDays(10), 50, epicTask.getId());
        manager.addSubTask(subTask4);
        SubTask subTask5 = new SubTask("Sub task5", "sub 5", startTime.plusDays(35), 50, epicTask.getId());
        manager.addSubTask(subTask5);
        assertEquals(startTime.minusDays(10), epicTask.getStartTime());//startDate
        assertEquals(50 * 5, epicTask.getDuration());// duration
        assertEquals(startTime.plusDays(35).plusMinutes(50), epicTask.getEndTime()); //endTime


        // удаление рандомных сабов
        manager.removeSubTask(subTask1.getId());
        manager.removeSubTask(subTask2.getId());
        manager.removeSubTask(subTask3.getId());
        assertEquals(startTime.plusDays(10), epicTask.getStartTime()); //startDate
        assertEquals(50 * 2, epicTask.getDuration());//duration
        assertEquals(startTime.plusDays(35).plusMinutes(50), epicTask.getEndTime());//endTime


        // добавление новых сабов
        subTask1 = new SubTask("Sub task11", "sub 11", startTime.minusDays(300), 550, epicTask.getId());
        manager.addSubTask(subTask1);
        subTask2 = new SubTask("Sub task22", "sub 22", startTime.plusDays(1210), 456, epicTask.getId());
        manager.addSubTask(subTask2);
        subTask3 = new SubTask("Sub task33", "sub 33", startTime.plusDays(5), 50, epicTask.getId());
        manager.addSubTask(subTask3);
        assertEquals(startTime.minusDays(300), epicTask.getStartTime()); //startDate
        assertEquals(50 * 3 + 550 + 456, epicTask.getDuration());//duration
        assertEquals(startTime.plusDays(1210).plusMinutes(456), epicTask.getEndTime());//endTime
    }



    private LocalDateTime getDefaultLocalDateTime() {
        LocalDate date = LocalDate.of(2022, random.nextInt(11) + 1, random.nextInt(26) + 1);
        LocalTime time = LocalTime.of(random.nextInt(24), random.nextInt(59), random.nextInt(59));
        return LocalDateTime.of(date, time);
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