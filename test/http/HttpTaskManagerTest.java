package http;

import adaper.LocalDateTimeAdapter;
import adaper.StatusAdapter;
import com.google.gson.*;
import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enums.Status;
import http.server.HttpTaskServer;
import managers.task.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest {


    private static final String httpServerUrl = "http://localhost:8080";
    private static final String KVServerUrl = "http://localhost:8078";


    private static Random random;
    private static Gson gson;


    private int index;


    @BeforeAll
    public static void prepare() throws IOException {
        //new KVServer().start();

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .create();

        random = new Random();
    }

    //@AfterAll
    public static void closeAll() {
        stopServer(httpServerUrl);
        stopServer(KVServerUrl);
    }

    @Test
    public void getSortedTaskList() throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        TaskManager taskManager = httpTaskServer.getHttpTaskManager();

        //добавляем задачи и историю
        Task seed = generateCurrentTask("task1", 2020, 10, 10, 10, 10, 10);
        taskManager.addTask(seed);

        Task task1 = generateRandomTask(seed);
        taskManager.addTask(task1);

        Task task2 = generateRandomTask(task1);
        taskManager.addTask(task2);

        Task task3 = generateRandomTask(task2);
        taskManager.addTask(task3);

        Task task4 = generateRandomTask(task3);
        taskManager.addTask(task4);

        taskManager.getTask(seed.getTaskId());
        taskManager.getTask(task1.getTaskId());
        taskManager.getTask(task2.getTaskId());
        taskManager.getTask(task3.getTaskId());
        taskManager.getTask(task4.getTaskId());

        assertEquals(5, taskManager.getTaskList().size());
        assertEquals(5, taskManager.getHistory().size());


/*        httpTaskServer = new HttpTaskServer();
        taskManager = httpTaskServer.getHttpTaskManager();

        var taskList = taskManager.getTaskList();
        taskList.forEach(System.out::println);*/
    }

    private String sendStop() {
        String result = null;

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/stop");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.body();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
            //System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n"+ Arrays.toString(e.getStackTrace()));
        }
        return result;
    }

    public void sendNewTask() {
        Task task = generateCurrentTask("new", 2025, 5, 5, 5, 5, 5);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task), StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public void updateTask() {
        HttpClient client = HttpClient.newHttpClient();


        String requestBody = "{\"key1\":\"value1\", \"key2\":\"value2\"}";

        // получаем ответ в формате JSON с помощью заголовка
        URI url = URI.create("http://localhost:8080/tasks/task/");
        // сообщаем серверу, что готовы принять ответ в формате JSON
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public void getTaskById() {
        HttpClient client = HttpClient.newHttpClient();

        // получаем ответ в формате JSON с помощью заголовка
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        // сообщаем серверу, что готовы принять ответ в формате JSON
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
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


    private static void stopServer(String link) {
        HttpClient client = HttpClient.newHttpClient();

        String regUrl = link+"/stop";
        URI url = URI.create(regUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

}