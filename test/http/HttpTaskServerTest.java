package http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import enity.EpicTask;
import enity.SubTask;
import enity.Task;
import enums.Status;
import http.server.HttpTaskServer;
import managers.Managers;
import managers.task.TaskManager;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HttpTaskServerTest {

    private static final String httpServerUrl = "http://localhost:8080";
    private static final String KVServerUrl = "http://localhost:8078";

    private final String subtaskPath = "/tasks/subtask";
    private final String taskPath = "/tasks/task";
    private final String epicPath = "/tasks/epic";

    private static Random random;
    private static Gson gson;
    private static HttpTaskServer httpTaskServer;
    private static TaskManager taskManager;

    private int index;


    @BeforeAll
    public static void setUp() throws IOException {
        httpTaskServer = new HttpTaskServer();
        taskManager = httpTaskServer.getHttpTaskManager();
        gson = Managers.getDefaultGson();
        random = new Random();
    }


    @Test
    void testForAllTaskEndpoint() {


        /**
         *  @endpoint  /tasks/task     ( add )
         *  @method    -POST
         */
        Task seed = generateCurrentTask("task1", 2020, 10, 10, 10, 10, 10);
        assertEquals("Задача добавлена", sendPost(taskPath, seed));

        Task task1 = generateRandomTask(seed);
        assertEquals("Задача добавлена", sendPost(taskPath, task1));

        Task task2 = generateRandomTask(task1);
        assertEquals("Задача добавлена", sendPost(taskPath, task2));

        Task task3 = generateRandomTask(task2);
        assertEquals("Задача добавлена", sendPost(taskPath, task3));

        Task task4 = generateRandomTask(task3);
        assertEquals("Задача добавлена", sendPost(taskPath, task4));


        /**
         *  @endpoint  /tasks    ( get sorted list )
         *  @method    -get
         */
        List<Task> taskList = getTaskList(taskPath);
        assertEquals(5, taskList.size());
        for (Task t : taskList) {
            assertEquals(Status.NEW, t.getStatus());
        }


        /**
         *  @endpoint  /tasks/task/?id=     ( get by id )
         *  @method    -get
         */
        String returnedTask1String = sendGet(taskPath, taskList.get(0).getId());
        Task returned1 = gson.fromJson(returnedTask1String, Task.class);
        assertEquals(taskList.get(0), returned1);

        String returnedTask2String = sendGet(taskPath, taskList.get(1).getId());
        Task returned2 = gson.fromJson(returnedTask2String, Task.class);
        assertEquals(taskList.get(1), returned2);

        String returnedTask3String = sendGet(taskPath, taskList.get(2).getId());
        Task returned3 = gson.fromJson(returnedTask3String, Task.class);
        assertEquals(taskList.get(2), returned3);

        String returnedTask4String = sendGet(taskPath, taskList.get(3).getId());
        Task returned4 = gson.fromJson(returnedTask4String, Task.class);
        assertEquals(taskList.get(3), returned4);

        String returnedTask5String = sendGet(taskPath, taskList.get(4).getId());
        Task returned5 = gson.fromJson(returnedTask5String, Task.class);
        assertEquals(taskList.get(4), returned5);


        /**
         *  @endpoint  /tasks/task/    ( update )
         *  @method    -POST
         */
        returned1.setStatus(Status.DONE);
        returned5.setStatus(Status.DONE);
        returned2.setStatus(Status.IN_PROGRESS);
        returned3.setStatus(Status.IN_PROGRESS);
        returned4.setStatus(Status.IN_PROGRESS);
        assertEquals("Задача обновлена", sendPost(taskPath, returned1));
        assertEquals("Задача обновлена", sendPost(taskPath, returned2));
        assertEquals("Задача обновлена", sendPost(taskPath, returned3));
        assertEquals("Задача обновлена", sendPost(taskPath, returned4));
        assertEquals("Задача обновлена", sendPost(taskPath, returned5));

        taskList = getTaskList(taskPath);

        int doneCount = 0;
        int inProgressCount = 0;
        int newCount = 0;
        for (Task task : taskList) {
            switch (task.getStatus()) {
                case DONE: {
                    doneCount++;
                    break;
                }
                case NEW: {
                    newCount++;
                    break;
                }
                case IN_PROGRESS: {
                    inProgressCount++;
                    break;
                }
            }
        }

        assertEquals(5, taskList.size());
        assertEquals(2, doneCount);
        assertEquals(3, inProgressCount);
        assertEquals(0, newCount);


        /**
         *  @endpoint  /tasks/task/?id=    ( delete by id )
         *  @method    -DELETE
         */

        assertEquals("Задача удалена", sendDelete(taskPath, returned1.getId()));
        assertEquals("Задача удалена", sendDelete(taskPath, returned5.getId()));
        assertEquals("Задача не найдена", sendDelete(taskPath, 222));
        taskList = getTaskList(taskPath);
        assertEquals(3, taskList.size());
        assertFalse(taskList.contains(returned1));
        assertFalse(taskList.contains(returned5));


        /**
         *  @endpoint  /tasks/task/    ( delete all )
         *  @method    -DELETE
         */
        assertEquals("Все задачи удалены", sendDelete(taskPath, -1));
        taskList = getTaskList(taskPath);
        assertEquals(0, taskList.size());
    }

    @Test
    void addForAllEpicAndSubTaskEndpoints() {


        /**
         *  @endpoint  /tasks/task     ( add )
         *  @method    -POST
         */
        EpicTask seed = generateEpicTask();
        assertEquals("Эпик добавлен", sendPost(epicPath, seed));

        EpicTask epicTask1 = generateEpicTask();
        assertEquals("Эпик добавлен", sendPost(epicPath, epicTask1));

        EpicTask epicTask2 = generateEpicTask();
        assertEquals("Эпик добавлен", sendPost(epicPath, epicTask2));


        /**
         *  @endpoint  /tasks    ( get epic list )
         *  @method    -get
         */

        List<EpicTask> epicList = getEpicTaskList(epicPath);
        assertEquals(3, epicList.size());

        for (Task t : epicList) {
            assertEquals(Status.NEW, t.getStatus());
        }


        /**
         *  @endpoint  /tasks/epic/?id=     ( get by id )
         *  @method    -get
         */
        EpicTask returnedEpic1 = gson.fromJson(sendGet(epicPath, epicList.get(0).getId()), EpicTask.class);
        assertEquals(epicList.get(0), returnedEpic1);

        EpicTask returnedEpic2 = gson.fromJson(sendGet(epicPath, epicList.get(1).getId()), EpicTask.class);
        assertEquals(epicList.get(1), returnedEpic2);

        EpicTask returnedEpic3 = gson.fromJson(sendGet(epicPath, epicList.get(2).getId()), EpicTask.class);
        assertEquals(epicList.get(2), returnedEpic3);


        /**
         *  @endpoint  /tasks/epic/     ( update )
         *  @method    -post
         */
        SubTask subTask1 = generateCurrentSubTask("name 1", 2020, 10, 10, 10, 10, 10, returnedEpic1.getId());
        SubTask subTask2 = generateSubTask(subTask1, returnedEpic1.getId());
        SubTask subTask3 = generateSubTask(subTask2, returnedEpic1.getId());
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);
        taskManager.addSubTask(subTask3);

        // получаем список сабтасков
        String subtaskResponse = sendGet("/tasks/subtask", -1);
        JsonElement subtasksElement = JsonParser.parseString(subtaskResponse);
        JsonArray subtasksArray = subtasksElement.getAsJsonArray();

        List<SubTask> subTaskList = new ArrayList<>();
        for (JsonElement sub : subtasksArray) {
            subTaskList.add(gson.fromJson(sub, SubTask.class));
        }
        assertEquals(3, subTaskList.size());

        //получаем эпик в который добавляли, проверяем
        EpicTask returnedTest = gson.fromJson(sendGet(epicPath, returnedEpic1.getId()), EpicTask.class);
        assertEquals(3, returnedTest.getSubTaskList().size());


        /**
         *  @endpoint  /tasks/task/?id=    ( delete by id )
         *  @method    -DELETE
         */
        assertEquals("Эпик удален", sendDelete(epicPath, returnedEpic1.getId()));
        assertEquals("Эпик не найден", sendDelete(epicPath, 222));

        epicList = getEpicTaskList(epicPath);
        assertEquals(2, epicList.size());


        /**
         *  @endpoint  /tasks/task/    ( delete all )
         *  @method    -DELETE
         */

        sendDelete(epicPath, -1);
        epicList = getEpicTaskList(epicPath);
        assertEquals("[]", epicList);
    }

    @Test
    void addSubTaskEndpoint() {
        /**
         *  @endpoint  /tasks/subtask     ( add )
         *  @method    -POST
         */
        EpicTask seedEpic = generateEpicTask();
        assertEquals("Эпик добавлен", sendPost(epicPath, seedEpic));

        List<EpicTask> epicList = getEpicTaskList(epicPath);
        assertEquals(1, epicList.size());

        EpicTask mainEpic = gson.fromJson(sendGet(epicPath, epicList.get(0).getId()), EpicTask.class);
        assertEquals(epicList.get(0), mainEpic);
        int epicId = mainEpic.getId();

        SubTask seed = generateCurrentSubTask("task1", 2020, 10, 10, 10, 10, 10, epicId);
        assertEquals("Subtask добавлен", sendPost(subtaskPath, seed));
        SubTask task1 = generateSubTask(seed, epicId);
        assertEquals("Subtask добавлен", sendPost(subtaskPath, task1));
        SubTask task2 = generateSubTask(task1, epicId);
        assertEquals("Subtask добавлен", sendPost(subtaskPath, task2));
        SubTask task3 = generateSubTask(task2, epicId);
        assertEquals("Subtask добавлен", sendPost(subtaskPath, task3));
        SubTask task4 = generateSubTask(task3, epicId);
        assertEquals("Subtask добавлен", sendPost(subtaskPath, task4));

        mainEpic = gson.fromJson(sendGet(epicPath, epicList.get(0).getId()), EpicTask.class);
        assertEquals(5, mainEpic.getSubTaskList().size());


        /**
         *  @endpoint  /tasks/subtask    ( get list )
         *  @method    -get
         */
        List<SubTask> subTaskList = getSubTaskList(subtaskPath);
        assertEquals(5, subTaskList.size());
        for (SubTask t : subTaskList) {
            assertEquals(Status.NEW, t.getStatus());
        }


        /**
         *  @endpoint  /tasks//subtask/?id=     ( get by id )
         *  @method    -get
         */
        String returnedTask1String = sendGet(subtaskPath, subTaskList.get(0).getId());
        SubTask returned1 = gson.fromJson(returnedTask1String, SubTask.class);
        assertEquals(subTaskList.get(0), returned1);

        String returnedTask2String = sendGet(subtaskPath, subTaskList.get(1).getId());
        SubTask returned2 = gson.fromJson(returnedTask2String, SubTask.class);
        assertEquals(subTaskList.get(1), returned2);

        String returnedTask3String = sendGet(subtaskPath, subTaskList.get(2).getId());
        SubTask returned3 = gson.fromJson(returnedTask3String, SubTask.class);
        assertEquals(subTaskList.get(2), returned3);

        String returnedTask4String = sendGet(subtaskPath, subTaskList.get(3).getId());
        SubTask returned4 = gson.fromJson(returnedTask4String, SubTask.class);
        assertEquals(subTaskList.get(3), returned4);

        String returnedTask5String = sendGet(subtaskPath, subTaskList.get(4).getId());
        SubTask returned5 = gson.fromJson(returnedTask5String, SubTask.class);
        assertEquals(subTaskList.get(4), returned5);


        /**
         *  @endpoint  /tasks/subtask/    ( update )
         *  @method    -POST
         */
        returned1.setStatus(Status.DONE);
        returned5.setStatus(Status.DONE);
        returned2.setStatus(Status.IN_PROGRESS);
        returned3.setStatus(Status.IN_PROGRESS);
        returned4.setStatus(Status.IN_PROGRESS);
        assertEquals("Subtask обновлен", sendPost(subtaskPath, returned1));
        assertEquals("Subtask обновлен", sendPost(subtaskPath, returned2));
        assertEquals("Subtask обновлен", sendPost(subtaskPath, returned3));
        assertEquals("Subtask обновлен", sendPost(subtaskPath, returned4));
        assertEquals("Subtask обновлен", sendPost(subtaskPath, returned5));

        subTaskList = getSubTaskList(subtaskPath);

        int doneCount = 0;
        int inProgressCount = 0;
        int newCount = 0;
        for (SubTask task : subTaskList) {
            switch (task.getStatus()) {
                case DONE: {
                    doneCount++;
                    break;
                }
                case NEW: {
                    newCount++;
                    break;
                }
                case IN_PROGRESS: {
                    inProgressCount++;
                    break;
                }
            }
        }
        mainEpic = gson.fromJson(sendGet(epicPath, epicList.get(0).getId()), EpicTask.class);
        assertEquals(5, mainEpic.getSubTaskList().size());
        assertEquals(Status.IN_PROGRESS, mainEpic.getStatus());
        assertEquals(5, subTaskList.size());
        assertEquals(0, newCount);
        assertEquals(2, doneCount);
        assertEquals(3, inProgressCount);


        /**
         *  @endpoint  /tasks/subtask/?id=    ( delete by id )
         *  @method    -DELETE
         */

        assertEquals("Subtask удален", sendDelete(subtaskPath, returned1.getId()));
        assertEquals("Subtask удален", sendDelete(subtaskPath, returned5.getId()));
        assertEquals("Subtask не найден", sendDelete(subtaskPath, 222));

        subTaskList = getSubTaskList(subtaskPath);

        assertEquals(3, subTaskList.size());
        assertFalse(subTaskList.contains(returned1));
        assertFalse(subTaskList.contains(returned5));


        /**
         *  @endpoint  /tasks/subtask    ( delete all )
         *  @method    -DELETE
         */
        assertEquals("Все Subtask удалены", sendDelete(subtaskPath, -1));
        subTaskList = getSubTaskList(subtaskPath);
        assertEquals(0, subTaskList.size());
    }

    private String sendGet(String endpoint, int id) {

        String result = null;

        HttpClient client = HttpClient.newHttpClient();

        String regUrl;

        if (id != -1) {
            regUrl = httpServerUrl + endpoint + "/?id=" + id;
        } else {
            regUrl = httpServerUrl + endpoint;
        }

        URI url = URI.create(regUrl);

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

    private String sendPost(String endpoint, Object body) {
        String result = null;

        HttpClient client = HttpClient.newHttpClient();

        String regUrl = httpServerUrl + endpoint;
        URI url = URI.create(regUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body)))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.body();

        } catch (IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n:" + e.getMessage());
        }
        return result;
    }

    private String sendDelete(String endpoint, int id) {

        String result;

        HttpClient client = HttpClient.newHttpClient();

        String regUrl;

        if (id != -1) {
            regUrl = httpServerUrl + endpoint + "/?id=" + id;
        } else {
            regUrl = httpServerUrl + endpoint;
        }

        URI url = URI.create(regUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .header("Accept", "application/json")
                .DELETE()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.body();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
            //System.out.println("Во время выполнения запроса ресурса по URL-адресу: '" + url + "', возникла ошибка.\n" + e.getMessage());
        }
        return result;
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

    private List<Task> getTaskList(String endpoint) {
        String list = sendGet(endpoint, -1);
        JsonElement jsonSubtasks = JsonParser.parseString(list);
        JsonArray tasksArray = jsonSubtasks.getAsJsonArray();

        List<Task> taskList = new ArrayList<>();
        for (JsonElement task : tasksArray) {
            taskList.add(gson.fromJson(task, Task.class));
        }
        return taskList;
    }

    private List<SubTask> getSubTaskList(String path) {
        String subtaskResponse = sendGet(path, -1);
        JsonElement subtasksElement = JsonParser.parseString(subtaskResponse);
        JsonArray subtasksArray = subtasksElement.getAsJsonArray();

        List<SubTask> subTaskList = new ArrayList<>();
        for (JsonElement sub : subtasksArray) {
            subTaskList.add(gson.fromJson(sub, SubTask.class));
        }
        return subTaskList;
    }

    private List<EpicTask> getEpicTaskList(String path) {
        String list = sendGet(path, -1);
        JsonElement epics = JsonParser.parseString(list);
        JsonArray tasksArray = epics.getAsJsonArray();

        List<EpicTask> epicList = new ArrayList<>();
        for (JsonElement task : tasksArray) {
            epicList.add(gson.fromJson(task, EpicTask.class));
        }
        return epicList;
    }
}