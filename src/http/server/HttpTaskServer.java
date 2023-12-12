package http.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import enity.ResponseEntity;
import http.server.handlers.EpicEndpointHandler;
import http.server.handlers.SubtaskEndpointHandler;
import http.server.handlers.TaskEndpointHandler;
import managers.Managers;
import managers.task.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer implements HttpHandler {

    enum Endpoint {TASKS, TASK, EPIC, SUB, HISTORY, UNKNOWN, STOP}

    private final int PORT = 8080;
    private final TaskManager httpTaskManager;
    private final Gson gson;
    private final String SERVER_URL = "http://localhost:8078";

    public HttpTaskServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", this);
        server.createContext("/stop", this);
        server.start();

        gson = Managers.getDefaultGson();

        httpTaskManager = Managers.getDefault(SERVER_URL);
    }

    public TaskManager getHttpTaskManager() {
        return httpTaskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI());
        ResponseEntity response = new ResponseEntity("", 0);

        switch (endpoint) {
            case TASKS: {
                response = getPriorityTasks();
                break;
            }
            case TASK: {
                response = new TaskEndpointHandler(httpTaskManager, exchange).handleEndpoint();
                break;
            }
            case EPIC: {
                response = new EpicEndpointHandler(httpTaskManager, exchange).handleEndpoint();
                break;
            }
            case SUB: {
                response = new SubtaskEndpointHandler(httpTaskManager, exchange).handleEndpoint();
                break;
            }
            case HISTORY: {
                response = getHistory();
                break;
            }
            case STOP: {
                stopServer(exchange);
                break;
            }
            default:
                response = new ResponseEntity("Такого эндпоинта не существует", 404);
        }

        sendResponse(exchange, response.getAnswer(), response.getCode());
    }

    private void stopServer(HttpExchange exchange) {
        try {
            sendResponse(exchange, "Сервер остановлен", 200);
            System.exit(0);
        }catch (IOException e){
        }finally {
            exchange.close();
        }
    }

    private Endpoint getEndpoint(URI requestUri) {
        String[] pathParts = requestUri.getPath().split("/");

        if (pathParts.length == 2) {
            if (pathParts[1].equalsIgnoreCase("stop")) {
                return Endpoint.STOP;
            } else {
                return Endpoint.TASKS;
            }
        } else {
            switch (pathParts[2].toLowerCase()) {
                case "task": {
                    return Endpoint.TASK;
                }
                case "subtask": {
                    return Endpoint.SUB;
                }
                case "epic": {
                    return Endpoint.EPIC;
                }
                case "history": {
                    return Endpoint.HISTORY;
                }
            }
        }

        return Endpoint.UNKNOWN;
    }

    protected void sendResponse(HttpExchange h, String text, int code) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    private ResponseEntity getPriorityTasks() {
        var tasklist = httpTaskManager.getPrioritizedTasks();
        return new ResponseEntity(gson.toJson(tasklist), 200);
    }

    private ResponseEntity getHistory() {
        var history = httpTaskManager.getHistory();
        return new ResponseEntity(gson.toJson(history), 200);
    }
}
