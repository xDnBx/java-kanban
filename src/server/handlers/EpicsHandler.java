package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TaskTimeException;
import managers.Managers;
import managers.interfaces.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.util.regex.Pattern;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    Gson gson = Managers.getGson();

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
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
            if (Pattern.matches("^/epics$", path)) {
                String response = gson.toJson(taskManager.getEpics());
                sendText(httpExchange, response, 200);
            }

            if (Pattern.matches("^/epics/\\d+$", path)) {
                String pathId = path.replaceFirst("/epics/", "");
                int id = parsePathId(pathId);
                if (id != -1) {
                    if (taskManager.getEpicById(id) != null) {
                        String response = gson.toJson(taskManager.getEpicById(id));
                        sendText(httpExchange, response, 200);
                    } else {
                        sendNotFound(httpExchange);
                    }
                } else {
                    sendNotFound(httpExchange);
                }
            } else if (Pattern.matches("^/epics/\\d+/subtasks", path)) {
                String pathId = path.replaceFirst("/epics/", "")
                        .replaceFirst("/subtasks", "");
                int id = parsePathId(pathId);
                if (id != -1) {
                    if (taskManager.getSubtasksFromEpic(id) != null) {
                        String response = gson.toJson(taskManager.getSubtasksFromEpic(id));
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
            Epic epic = gson.fromJson(readText(httpExchange), Epic.class);
            if (Pattern.matches("^/epics$", path)) {
                taskManager.addNewEpic(epic);
                String response = gson.toJson(epic);
                sendText(httpExchange, response, 201);
            }

            if (Pattern.matches("^/epics/\\d+$", path)) {
                String pathId = path.replaceFirst("/epics/", "");
                int id = parsePathId(pathId);
                if (id != -1) {
                    epic.setId(id);
                    taskManager.updateEpicTask(epic);
                    String response = gson.toJson(epic);
                    sendText(httpExchange, response, 201);
                } else {
                    sendNotFound(httpExchange);
                }
            }
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
            if (Pattern.matches("^/epics/\\d+$", path)) {
                String pathId = path.replaceFirst("/epics/", "");
                int id = parsePathId(pathId);
                if (id != -1) {
                    taskManager.deleteEpicTaskById(id);
                    sendText(httpExchange, "Эпик с id " + id + " удален", 200);
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