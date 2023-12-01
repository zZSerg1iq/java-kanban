package enity;

import enity.task.status.Status;
import managers.Managers;
import managers.task.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTaskTest {

    private static TaskManager manager;
    private static EpicTask epicTask;


    @BeforeAll
    static void setUp() {
        manager = Managers.loadFromFile(new File("resources/tasks"));
    }

    @BeforeEach
    public void addNewEpicForTest() {
        epicTask = new EpicTask("New Epic", "Epic");
        manager.addEpicTask(epicTask);
    }

    private LocalDateTime getDefaultLocalDateTime() {
        LocalDate date = LocalDate.of(2023, 11, 27);
        LocalTime time = LocalTime.of(12, 30, 0);
        return LocalDateTime.of(date, time);
    }

    @Test
    void shouldBeNewStatusOnEpicJustCreated() {
        assertEquals(epicTask.getStatus(), Status.NEW);
    }

    @Test
    void shouldBeNewStatusWhenAllSubTaskIsNEW() {
        SubTask subTask1 = new SubTask("Sub task1", "sub 1", getDefaultLocalDateTime(), 50, epicTask.getTaskId());
        SubTask subTask2 = new SubTask("Sub task2", "sub 2", getDefaultLocalDateTime(), 20, epicTask.getTaskId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        assertEquals(epicTask.getStatus(), Status.NEW);
    }

    @Test
    void shouldBeNewStatusWhenAllSubTaskWasDeleted() {
        SubTask subTask1 = new SubTask("Sub task1", "sub 1", getDefaultLocalDateTime(), 50, epicTask.getTaskId());
        SubTask subTask2 = new SubTask("Sub task2", "sub 2", getDefaultLocalDateTime(), 50, epicTask.getTaskId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.getEpicTask(epicTask.getTaskId());

        manager.removeAllSubtasks();
        assertEquals(Status.NEW, epicTask.getStatus());
    }

    @Test
    void shouldBeNewStatusWhenOneTaskIsNewAndOneIsDone() {
        SubTask subTask1 = new SubTask("Sub task1", "sub 1", getDefaultLocalDateTime(), 50, epicTask.getTaskId());
        SubTask subTask2 = new SubTask("Sub task2", "sub 2", getDefaultLocalDateTime(), 50, epicTask.getTaskId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        subTask1.setStatus(Status.DONE);
        manager.updateSubTask(subTask1);

        assertEquals(Status.NEW, epicTask.getStatus());
    }

    @Test
    void shouldBeInProgressStatusWhenOneOfSubtasksIsInProgress() {
        SubTask subTask1 = new SubTask("Sub task1", "sub 1", getDefaultLocalDateTime(), 50, epicTask.getTaskId());
        SubTask subTask2 = new SubTask("Sub task2", "sub 2", getDefaultLocalDateTime(), 50, epicTask.getTaskId());
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        subTask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subTask1);

        assertEquals(Status.IN_PROGRESS, epicTask.getStatus());
    }

    @Test
    void shouldBeDoneStatusIfAllOfSubTasksIsDone() {
        SubTask subTask1 = new SubTask("Sub task1", "sub 1", getDefaultLocalDateTime(), 50, epicTask.getTaskId());
        SubTask subTask2 = new SubTask("Sub task2", "sub 2", getDefaultLocalDateTime(), 50, epicTask.getTaskId());
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
        SubTask subTask1 = new SubTask("Sub task1", "sub 1", startTime, 50, epicTask.getTaskId());
        manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("Sub task2", "sub 2", startTime.minusDays(10), 50, epicTask.getTaskId());
        manager.addSubTask(subTask2);
        SubTask subTask3 = new SubTask("Sub task3", "sub 3", startTime.minusDays(3), 50, epicTask.getTaskId());
        manager.addSubTask(subTask3);
        SubTask subTask4 = new SubTask("Sub task4", "sub 4", startTime.plusDays(10), 50, epicTask.getTaskId());
        manager.addSubTask(subTask4);
        SubTask subTask5 = new SubTask("Sub task5", "sub 5", startTime.plusDays(35), 50, epicTask.getTaskId());
        manager.addSubTask(subTask5);
        assertEquals(startTime.minusDays(10), epicTask.getStartTime());//startDate
        assertEquals(50 * 5, epicTask.getDuration());// duration
        assertEquals(startTime.plusDays(35).plusMinutes(50), epicTask.getEndTime()); //endTime


        // удаление рандомных сабов
        manager.removeSubTask(subTask1.getTaskId());
        manager.removeSubTask(subTask2.getTaskId());
        manager.removeSubTask(subTask3.getTaskId());
        assertEquals(startTime.plusDays(10), epicTask.getStartTime()); //startDate
        assertEquals(50 * 2, epicTask.getDuration());//duration
        assertEquals(startTime.plusDays(35).plusMinutes(50), epicTask.getEndTime());//endTime


        // добавление новых сабов
        subTask1 = new SubTask("Sub task11", "sub 11", startTime.minusDays(300), 550, epicTask.getTaskId());
        manager.addSubTask(subTask1);
        subTask2 = new SubTask("Sub task22", "sub 22", startTime.plusDays(1210), 456, epicTask.getTaskId());
        manager.addSubTask(subTask2);
        subTask3 = new SubTask("Sub task33", "sub 33", startTime.plusDays(5), 50, epicTask.getTaskId());
        manager.addSubTask(subTask3);
        assertEquals(startTime.minusDays(300), epicTask.getStartTime()); //startDate
        assertEquals(50 * 3 + 550 + 456, epicTask.getDuration());//duration
        assertEquals(startTime.plusDays(1210).plusMinutes(456), epicTask.getEndTime());//endTime
    }


}