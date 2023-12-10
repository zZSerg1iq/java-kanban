package http.server.handlers;

import adaper.LocalDateTimeAdapter;
import adaper.StatusAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import enity.ResponseEntity;
import enums.Status;
import managers.task.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

public abstract class EndpointHandler {

    protected final TaskManager taskManager;
    protected final HttpExchange exchange;
    protected final Gson gson;

    public EndpointHandler(TaskManager taskManager, HttpExchange exchange) {
        this.taskManager = taskManager;
        this.exchange = exchange;

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Status.class, new StatusAdapter())
                .create();
    }

    protected String getResponseBody(HttpExchange exchange) {
        String body;

        try (InputStream os = exchange.getRequestBody()) {
            body = new String(os.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return body;
    }

    public abstract ResponseEntity handleEndpoint();

}
