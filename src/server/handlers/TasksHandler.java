package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TaskTimeException;
import managers.interfaces.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.regex.Pattern;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try (httpExchange) {
            String requestMethod = httpExchange.getRequestMethod();

            switch (requestMethod) {
                case "GET" -> handleGet(httpExchange);
                case "POST" -> handlePost(httpExchange);
                case "DELETE" -> handleDelete(httpExchange);
                default -> sendText(httpExchange, "Ждем GET, POST или DELETE запрос, а получили - " + requestMethod,
                        405);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            if (Pattern.matches("^/tasks$", path)) {
                String response = gson.toJson(taskManager.getTasks());
                sendText(httpExchange, response, 200);
            }

            if (Pattern.matches("^/tasks/\\d+$", path)) {
                String pathId = path.replaceFirst("/tasks/", "");
                int id = parsePathId(pathId);
                if (id != -1) {
                    if (taskManager.getTaskById(id) != null) {
                        String response = gson.toJson(taskManager.getTaskById(id));
                        sendText(httpExchange, response, 200);
                    } else {
                        sendNotFound(httpExchange);
                    }
                } else {
                    sendNotFound(httpExchange);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendText(httpExchange, "Internal Server Error", 500);
        }
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            Task task = gson.fromJson(readText(httpExchange), Task.class);
            if (task == null) {
                throw new NotFoundException("Bad Request");
            }
            if (Pattern.matches("^/tasks$", path)) {
                taskManager.addNewTask(task);
                String response = gson.toJson(task);
                sendText(httpExchange, response, 201);
            }

            if (Pattern.matches("^/tasks/\\d+$", path)) {
                String pathId = path.replaceFirst("/tasks/", "");
                int id = parsePathId(pathId);
                if (id != -1) {
                    task.setId(id);
                    taskManager.updateTask(task);
                    String response = gson.toJson(task);
                    sendText(httpExchange, response, 201);
                } else {
                    sendNotFound(httpExchange);
                }
            }
        } catch (NotFoundException e) {
            sendText(httpExchange, "Bad Request", 400);
        } catch (TaskTimeException e) {
            sendHasInteractions(httpExchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(httpExchange, "Internal Server Error", 500);
        }
    }

    private void handleDelete(HttpExchange httpExchange) throws IOException {
        try {
            String path = httpExchange.getRequestURI().getPath();
            if (Pattern.matches("^/tasks/\\d+$", path)) {
                String pathId = path.replaceFirst("/tasks/", "");
                int id = parsePathId(pathId);
                if (id != -1) {
                    taskManager.deleteTaskById(id);
                    sendText(httpExchange, "Задача с id " + id + " удалена", 200);
                } else {
                    sendNotFound(httpExchange);
                }
            } else {
                sendNotFound(httpExchange);
            }
        } catch (NotFoundException e) {
            sendNotFound(httpExchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendText(httpExchange, "Internal Server Error", 500);
        }
    }
}
