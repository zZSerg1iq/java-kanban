import com.google.gson.Gson;
import enity.EpicTask;
import enity.SubTask;
import http.server.HttpTaskServer;
import http.server.KVServer;
import managers.Managers;
import managers.task.TaskManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

public class Test {
    private static Random random;
    private static Gson gson;
    private static HttpTaskServer httpTaskServer;
    private static TaskManager taskManager;

    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }
}
