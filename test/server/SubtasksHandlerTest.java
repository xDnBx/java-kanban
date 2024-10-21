package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.TaskStatus;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SubtasksHandlerTest {
    private static final String URL = "http://localhost:8080/subtasks";
    private TaskManager taskManager;
    private HttpTaskServer httpTaskServer;
    private HttpClient httpClient;
    private final Gson gson = Managers.getGson();
    Epic epic1;
    Subtask subtask1;
    Subtask subtask2;

    @BeforeEach
    public void BeforeEach() throws IOException {
        taskManager = new InMemoryTaskManager();

        epic1 = new Epic("epic1", "description1");
        taskManager.addNewEpic(epic1);
        subtask1 = new Subtask("subtask1", "description1", TaskStatus.NEW,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic1.getId());
        subtask2 = new Subtask("subtask2", "description2", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 2, 11, 46), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        httpTaskServer = new HttpTaskServer(taskManager);
        httpClient = HttpClient.newHttpClient();
        httpTaskServer.start();
    }

    @AfterEach
    public void AfterEach() {
        httpTaskServer.stop();
    }

    @Test
    void canGetSubtasks() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL)).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> subtasks = gson.fromJson(response.body(), subtaskType);

        assertEquals(200, response.statusCode(), "Неверный статус");
        assertEquals(2, subtasks.size(), "Неверное количество подзадач");
        assertEquals(subtask1, subtasks.get(0), "Неверная подзадача 1");
    }

    @Test
    void canGetSubtaskById() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL + "/2")).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Subtask subtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode(), "Неверный статус");
        assertEquals(subtask1, subtask, "Неверная подзадача 1");
    }

    @Test
    void canReturnNotFoundSubtask() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL + "/4")).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный статус");
    }

    @Test
    void canAddSubtask() throws IOException, InterruptedException {
        Subtask subtask3 = new Subtask(4, "subtask3", "description3", TaskStatus.DONE,
                Duration.ofMinutes(80), LocalDateTime.of(2024, Month.JANUARY, 5, 12, 47), epic1.getId());
        String json = gson.toJson(subtask3);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Subtask subtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(201, response.statusCode(), "Неверный статус");
        assertEquals(subtask3, subtask, "Неверная подзадача 3");
    }

    @Test
    void canReturnNotAcceptableSubtask() throws IOException, InterruptedException {
        Subtask subtask3 = new Subtask(4, "subtask3", "description3", TaskStatus.DONE,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 2, 11, 56), epic1.getId());
        String json = gson.toJson(subtask3);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Неверный статус");
    }

    @Test
    void canUpdateSubtask() throws IOException, InterruptedException {
        Subtask subtask3 = new Subtask(2, "subtask3", "description3", TaskStatus.DONE,
                Duration.ofMinutes(80), LocalDateTime.of(2024, Month.JANUARY, 5, 12, 47), epic1.getId());
        String json = gson.toJson(subtask3);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/2"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Subtask subtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(201, response.statusCode(), "Неверный статус");
        assertEquals(subtask3, subtask, "Неверная подзадача 3");
    }

    @Test
    void canDeleteSubtask() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL + "/2")).DELETE().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус");
        assertNull(taskManager.getSubtaskById(2), "Задача не удалена");
    }
}