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
import tasks.Task;

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

class TasksHandlerTest {
    private static final String URL = "http://localhost:8080/tasks";
    private TaskManager taskManager;
    private HttpTaskServer httpTaskServer;
    private HttpClient httpClient;
    private final Gson gson = Managers.getGson();
    Task task1;
    Task task2;

    @BeforeEach
    public void BeforeEach() throws IOException {
        taskManager = new InMemoryTaskManager();

        task1 = new Task("task1", "description1", TaskStatus.NEW, Duration.ofMinutes(100),
                LocalDateTime.of(2024, Month.JANUARY, 1, 8, 12));
        task2 = new Task("task2", "description2", TaskStatus.IN_PROGRESS, Duration.ofMinutes(50),
                LocalDateTime.of(2024, Month.JANUARY, 4, 9, 44));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        httpTaskServer = new HttpTaskServer(taskManager);
        httpClient = HttpClient.newHttpClient();
        httpTaskServer.start();
    }

    @AfterEach
    public void AfterEach() {
        httpTaskServer.stop();
    }

    @Test
    void canGetTasks() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL)).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Неверный статус");
        assertEquals(2, tasks.size(), "Неверное количество задач");
        assertEquals(task1, tasks.get(0), "Неверная задача 1");
    }

    @Test
    void canGetTaskById() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL + "/1")).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Неверный статус");
        assertEquals(task1, task, "Неверная задача 1");
    }

    @Test
    void canReturnNotFoundTask() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL + "/3")).GET().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Неверный статус");
    }

    @Test
    void canAddTask() throws IOException, InterruptedException {
        Task task3 = new Task(3, "task3", "description3", TaskStatus.IN_PROGRESS, Duration.ofMinutes(70),
                LocalDateTime.of(2024, Month.JANUARY, 5, 10, 44));
        String json = gson.toJson(task3);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(response.body(), Task.class);

        assertEquals(201, response.statusCode(), "Неверный статус");
        assertEquals(task3, task, "Неверная задача 3");
    }

    @Test
    void canReturnNotAcceptableTask() throws IOException, InterruptedException {
        Task task3 = new Task(3, "task3", "description3", TaskStatus.IN_PROGRESS, Duration.ofMinutes(100),
                LocalDateTime.of(2024, Month.JANUARY, 1, 8, 22));
        String json = gson.toJson(task3);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Неверный статус");
    }

    @Test
    void canUpdateTask() throws IOException, InterruptedException {
        Task task3 = new Task(1, "task3", "description3", TaskStatus.IN_PROGRESS, Duration.ofMinutes(70),
                LocalDateTime.of(2024, Month.JANUARY, 10, 10, 44));
        String json = gson.toJson(task3);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL + "/1"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        Task task = gson.fromJson(response.body(), Task.class);

        assertEquals(201, response.statusCode(), "Неверный статус");
        assertEquals(task3, task, "Неверная задача 3");
    }

    @Test
    void canDeleteTask() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(URL + "/1")).DELETE().build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Неверный статус");
        assertNull(taskManager.getTaskById(1), "Задача не удалена");
    }
}
