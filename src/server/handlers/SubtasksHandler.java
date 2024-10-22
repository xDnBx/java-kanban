package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TaskTimeException;
import managers.interfaces.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.util.regex.Pattern;

public class SubtasksHandler extends BaseHttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
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
            if (Pattern.matches("^/subtasks$", path)) {
                String response = gson.toJson(taskManager.getSubTasks());
                sendText(httpExchange, response, 200);
            }

            if (Pattern.matches("^/subtasks/\\d+$", path)) {
                String pathId = path.replaceFirst("/subtasks/", "");
                int id = parsePathId(pathId);
                if (id != -1) {
                    if (taskManager.getSubtaskById(id) != null) {
                        String response = gson.toJson(taskManager.getSubtaskById(id));
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
            Subtask subtask = gson.fromJson(readText(httpExchange), Subtask.class);
            if (subtask == null) {
                throw new NotFoundException("Bad Request");
            }
            if (Pattern.matches("^/subtasks$", path)) {
                taskManager.addNewSubtask(subtask);
                String response = gson.toJson(subtask);
                sendText(httpExchange, response, 201);
            }

            if (Pattern.matches("^/subtasks/\\d+$", path)) {
                String pathId = path.replaceFirst("/subtasks/", "");
                int id = parsePathId(pathId);
                if (id != -1) {
                    subtask.setId(id);
                    taskManager.updateSubtask(subtask);
                    String response = gson.toJson(subtask);
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
            if (Pattern.matches("^/subtasks/\\d+$", path)) {
                String pathId = path.replaceFirst("/subtasks/", "");
                int id = parsePathId(pathId);
                if (id != -1) {
                    taskManager.deleteSubtaskById(id);
                    sendText(httpExchange, "Подзадача с id " + id + " удалена", 200);
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