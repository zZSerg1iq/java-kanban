package http.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import enity.ResponseEntity;
import enity.SubTask;
import managers.task.TaskManager;

public class SubtaskEndpointHandler extends EndpointHandler {

    public SubtaskEndpointHandler(TaskManager taskManager, HttpExchange exchange) {
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
        if (query != null && !query.contains("id")) {
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
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        if (pathParts.length == 4 && pathParts[3].equalsIgnoreCase("epic")){
            if (id != -1) {
                var epic = taskManager.getEpicTask(id);
                if (epic != null){
                    return new ResponseEntity(gson.toJson(epic.getSubTaskList()), 200);
                } else {
                    return new ResponseEntity("Epic не найден", 404);
                }
            }
        }

        if (id != -1) {
            //если есть id
            var temp = taskManager.getSubTask(id);
            if (temp != null) {
                return new ResponseEntity(gson.toJson(temp), 200);
            } else {
                return new ResponseEntity("Subtask не найден", 404);
            }
        } else {
            //если нет id
            var temp = taskManager.getSubtaskList();
            return new ResponseEntity(gson.toJson(temp), 200);
        }
    }

    private ResponseEntity handlePostMethod(String body) {
        if (body == null) {
            return new ResponseEntity("Некорректный JSON", 400);
        }

        //получаем из JSON-а задачу
        SubTask task = gson.fromJson(body, SubTask.class);

        //если id нету ( = 0 ), это добавление новой
        if (task.getTaskId() == 0) {
            taskManager.addSubTask(task);
            return new ResponseEntity("Subtask добавлен", 201);
        } else {
            //если id есть, это изменение
            taskManager.updateSubTask(task);
            return new ResponseEntity("Subtask обновлен", 201);
        }
    }

    private ResponseEntity handleDeleteMethod(int id) {
        if (id != -1) {
            //удалить одну задачу
            var temp = taskManager.removeSubTask(id);
            if (temp != null) {
                return new ResponseEntity("Subtask удален", 201);
            } else {
                return new ResponseEntity("Subtask не найден", 404);
            }
        } else {
            //удалить все задачи
            taskManager.removeAllSubtasks();
            return new ResponseEntity(gson.toJson("Все Subtask удалены"), 201);
        }
    }
}
