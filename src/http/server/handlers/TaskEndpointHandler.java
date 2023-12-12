package http.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import enity.ResponseEntity;
import enity.Task;
import managers.task.TaskManager;

public class TaskEndpointHandler extends EndpointHandler {

    public TaskEndpointHandler(TaskManager taskManager, HttpExchange exchange) {
        super(taskManager, exchange);
    }

    @Override
    public ResponseEntity handleEndpoint() {
        ResponseEntity responseEntity;
        String requestType = exchange.getRequestMethod();
        String query = exchange.getRequestURI().getQuery();
        String responseBody = getResponseBody(exchange);

        //проверка id
        int id = -1;
        if (query != null && !query.contains("id=")) {
            return new ResponseEntity("Некорректные параметры запроса", 400);
        }
        if (query != null) {
            id = Integer.parseInt(query.substring("id=".length()));
        }


        if (requestType.equalsIgnoreCase("get")) {
            responseEntity = handleGetMethod(id);
        } else if (requestType.equalsIgnoreCase("post")) {
            responseEntity = handlePostMethod(responseBody);
        } else if (requestType.equalsIgnoreCase("delete")) {
            responseEntity = handleDeleteMethod(id);
        } else {
            responseEntity = new ResponseEntity("Неподдерживаемый тип запроса", 405);
        }

        return responseEntity;
    }

    private ResponseEntity handleGetMethod(int id) {
        if (id != -1) {
            //если есть id
            var temp = taskManager.getTask(id);
            if (temp != null) {
                return new ResponseEntity(gson.toJson(temp), 200);
            } else {
                return new ResponseEntity("Задача не найдена", 404);
            }
        } else {
            //если нет id
            var temp = taskManager.getTaskList();
            return new ResponseEntity(gson.toJson(temp), 200);
        }
    }

    private ResponseEntity handlePostMethod(String body) {
        if (body == null) {
            return new ResponseEntity("Некорректный JSON", 400);
        }

        //получаем из JSON-а задачу
        Task task = gson.fromJson(body, Task.class);

        //если id нету ( = 0 ), это добавление новой
        if (task.getId() == 0) {
            taskManager.addTask(task);
            return new ResponseEntity("Задача добавлена", 201);
        } else {
            //если id есть, это изменение
            System.out.println(task);
            var result = taskManager.updateTask(task);
            if (result == null) {
                return new ResponseEntity("Задача не найдена", 404);
            }
            return new ResponseEntity("Задача обновлена", 201);
        }
    }

    private ResponseEntity handleDeleteMethod(int id) {
        if (id != -1) {
            //удалить одну задачу
            var temp = taskManager.removeTask(id);
            if (temp == null) {
                return new ResponseEntity("Задача не найдена", 404);
            }
            return new ResponseEntity("Задача удалена", 201);
        } else {
            //удалить все задачи
            taskManager.removeAllTasks();
            return new ResponseEntity("Все задачи удалены", 201);
        }
    }


}
