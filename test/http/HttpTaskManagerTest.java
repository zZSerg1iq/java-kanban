package http;

import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import http.client.KVTaskClient;
import http.server.HttpTaskServer;
import http.server.KVServer;
import managers.Managers;
import managers.task.TaskManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

class HttpTaskManagerTest {


    private static final String httpServerStopUrl = "http://localhost:8080/stop";
    private static final String KVServerStopUrl = "http://localhost:8078/stop";
    private static final String KVServerClearUrl = "http://localhost:8078/clear";


    private static Random random;
    private int index;
    private static HttpTaskServer httpTaskServer;
    private static TaskManager taskManager;


    @BeforeAll
    public static void prepare() throws IOException {
        new KVServer().start();

        httpTaskServer = new HttpTaskServer();
        taskManager = httpTaskServer.getHttpTaskManager();
        random = new Random();

    }

    @AfterAll
    public static void closeAll() {
        sendGet(httpServerStopUrl);
        sendGet(KVServerStopUrl);
    }

    @Test
    public void readWriteTest() {
        sendGet(KVServerClearUrl);
        int taskCount = taskManager.getTaskList().size();
        Assertions.assertEquals(0, taskCount);

        //добавляем задачи и историю
        Task seed = generateCurrentTask("task1", random.nextInt(200)+1990, 10, 10, 10, 10, 10);
        taskManager.addTask(seed);

        Task task1 = generateRandomTask(seed);
        taskManager.addTask(task1);

        Task task2 = generateRandomTask(task1);
        taskManager.addTask(task2);

        Task task3 = generateRandomTask(task2);
        taskManager.addTask(task3);

        Task task4 = generateRandomTask(task3);
        taskManager.addTask(task4);

        taskManager.getTask(seed.getId());
        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());
        taskManager.getTask(task4.getId());

        Assertions.assertEquals(taskCount+5, taskManager.getTaskList().size());
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
                    getDefaultLocalDateTime(), random.nextInt(500), epicId);
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



    private void addEpicTasksAndSubtasks(int count) {

    }


    public String getResponseBody(String toUrl) {
        String responseBody = null;

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(toUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            responseBody = response.body();

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return responseBody;
    }


    private static void sendGet(String link) {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(link);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Невозможно выполнить запрос на "+link+", исключение: "+e );
        }
    }

}