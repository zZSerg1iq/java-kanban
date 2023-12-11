import com.google.gson.Gson;
import http.server.HttpTaskServer;
import http.server.KVServer;
import managers.task.TaskManager;

import java.io.IOException;
import java.util.Random;

public class KVServerStart {
    private static Random random;
    private static Gson gson;
    private static HttpTaskServer httpTaskServer;
    private static TaskManager taskManager;

    public static void main(String[] args) throws IOException {
        new KVServer().start();
    }
}
