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

class EpicsHandlerTest {
    private static final String URL = "http://localhost:8080/epics";
    private TaskManager taskManager;
    private HttpTaskServer httpTaskServer;
    private HttpClient httpClient;
    private final Gson gson = Managers.getGson();
    Epic epic1;
    Epic epic2;

    @BeforeEach
    public void BeforeEach() throws IOException {
        taskManager = new InMemoryTaskManager();

        epic1 = new Epic("epic1", "description1");
        epic2 = new Epic("epic1", "description1");
        epic1.setStartTime(LocalDateTime.now());
        epic1.setEndTime(LocalDateTime.now());
        epic2.setStartTime(LocalDateTime.now());
        epic2.setEndTime(LocalDateTime.now());
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        httpTaskServer = new HttpTaskServer(taskManager);
        httpClient = HttpClient.newHttpClient();
        httpTaskServer.start();
    }

    @AfterEach
    public void AfterEach() {
        httpTaskServer.stop();
    }

    @Test
    void canGetEpics() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL)).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Type epicType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> epics = gson.fromJson(response.body(), epicType);

        assertEquals(200, response.statusCode(), "Неверный статус");
        assertEquals(2, epics.size(), "Неверное количество эпиков");
        assertEquals(epic1, epics.get(0), "Неверный эпик 1");
    }

    @Test
    void canGetEpicById() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL + "/1")).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Epic epic = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(), "Неверный статус");
        assertEquals(epic1, epic, "Неверный эпик 1");
    }

    @Test
    void canReturnNotFoundEpic() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL + "/3")).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный статус");
    }

    @Test
    void canGetEpicSubtasks() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask("subtask1", "description1", TaskStatus.NEW,
                Duration.ofMinutes(55), LocalDateTime.of(2024, Month.JANUARY, 3, 10, 45), epic1.getId());
        Subtask subtask2 = new Subtask("subtask2", "description2", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2024, Month.JANUARY, 2, 11, 46), epic1.getId());
        Subtask subtask3 = new Subtask("subtask3", "description3", TaskStatus.DONE,
                Duration.ofMinutes(80), LocalDateTime.of(2024, Month.JANUARY, 5, 12, 47), epic1.getId());
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL + "/1/subtasks")).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<ArrayList<Subtask>>() {
        }.getType();
        List<Subtask> subtasks = gson.fromJson(response.body(), subtaskType);

        assertEquals(200, response.statusCode(), "Неверный статус");
        assertEquals(3, subtasks.size(), "Неверное количество подзадач");
        assertEquals(subtask1, subtasks.get(0), "Неверная подзадача 1");
    }

    @Test
    void canReturnNotFoundEpicSubtasks() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL + "/3/subtasks")).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный статус");
    }

    @Test
    void canAddEpic() throws IOException, InterruptedException {
        Epic epic3 = new Epic(3, "epic3", "description3");
        epic3.setStartTime(LocalDateTime.now());
        epic3.setEndTime(LocalDateTime.now());
        String json = gson.toJson(epic3);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Epic epic = gson.fromJson(response.body(), Epic.class);

        assertEquals(201, response.statusCode(), "Неверный статус");
        assertEquals(epic3, epic, "Неверный эпик 3");
    }

    @Test
    void canDeleteEpic() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL + "/1")).DELETE().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус");
        assertNull(taskManager.getEpicById(1), "Задача не удалена");
    }
}