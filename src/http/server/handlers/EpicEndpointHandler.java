package http.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import enity.EpicTask;
import enity.ResponseEntity;
import managers.task.TaskManager;

public class EpicEndpointHandler extends EndpointHandler {

    public EpicEndpointHandler(TaskManager taskManager, HttpExchange exchange) {
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
        if (id != -1) {
            //если есть id
            var temp = taskManager.getEpicTask(id);

            if (temp != null) {
                return new ResponseEntity(gson.toJson(temp), 200);
            } else {
                return new ResponseEntity("Epic не найден", 404);
            }
        } else {
            return new ResponseEntity(gson.toJson(taskManager.getEpicTaskList()), 200);
        }
    }

    private ResponseEntity handlePostMethod(String body) {
        if (body == null) {
            return new ResponseEntity("Некорректный JSON", 400);
        }

        //получаем из JSON-а задачу
        EpicTask task = gson.fromJson(body, EpicTask.class);

        //если id нету ( = 0 ), это добавление новой
        if (task.getId() == 0) {
            taskManager.addEpicTask(task);
            return new ResponseEntity("Эпик добавлен", 201);
        } else {
            //если id есть, это изменение
            taskManager.updateEpic(task);
            return new ResponseEntity("Эпик обновлен", 201);
        }
    }

    private ResponseEntity handleDeleteMethod(int id) {
        if (id != -1) {
            //удалить один эпик
            var temp = taskManager.removeEpic(id);
            if (temp != null) {
                return new ResponseEntity("Эпик удален", 201);
            } else {
                return new ResponseEntity("Эпик не найден", 404);
            }
        } else {
            //удалить все задачи
            taskManager.removeAllEpics();
            return new ResponseEntity("Все эпики удалены", 201);
        }
    }
}
