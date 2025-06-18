package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import managers.TaskManager;
import models.StatusEnum;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TasksHandlerTest {

    private HttpTaskServer taskServer;
    private HttpClient httpClient;
    private TaskManager taskManager;
    private Gson gson;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() throws IOException {
        // передаём его в качестве аргумента в конструктор HttpTaskServer
        taskManager = Managers.getInMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        httpClient = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    void handle_GetAllTasks_ShouldReturnTasksList() throws IOException, InterruptedException {

        Task task = new Task("Test", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));

        taskManager.addTask(task);

        // создаём HTTP-клиент и запрос
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks"))
                .GET()
                .build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // проверяем код ответа
        Type listOfTasks = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(response.body(), listOfTasks);

        assertEquals(200, response.statusCode());
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.getFirst());
    }

   @Test
   void handle_GetTaskById_ShouldReturnTask() throws IOException, InterruptedException {

       Task task1 = new Task("Test", "Description", StatusEnum.NEW,
               LocalDateTime.of(2023, 1, 1, 10, 0),
               Duration.ofMinutes(30));
       task1.setId(1);

       Task task2 = new Task("Test", "Description", StatusEnum.NEW,
               LocalDateTime.of(2023, 2, 1, 10, 0),
               Duration.ofMinutes(30));
       task2.setId(2);

       taskManager.addTask(task1);
       taskManager.addTask(task2);

       HttpRequest request = HttpRequest.newBuilder()
               .uri(URI.create(baseUrl + "/tasks/2"))
               .GET()
               .build();

       HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

       Task actualTask2 = gson.fromJson(response.body(), Task.class);

       assertEquals(200, response.statusCode());
       assertEquals(task2, actualTask2);
   }

    @Test
    void handle_GetTaskById_NotFound_ShouldReturn404() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks/123"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void handle_PostTask_ShouldAddTask() throws IOException, InterruptedException {

        Task task = new Task("task", "description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Task createdTask = gson.fromJson(response.body(), Task.class);
        task.setId(createdTask.getId());

        assertEquals(task, createdTask);
        assertEquals(task.getName(), createdTask.getName());
        assertEquals(createdTask, taskManager.getTask(createdTask.getId()));
    }

    @Test
    void handle_PostTask_ShouldReturnError_WhenTaskExists() throws IOException, InterruptedException {

        Task task = new Task("task", "description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        task.setId(1);
        taskManager.addTask(task);

        Task duplicateTask = new Task("Duplicate task", "description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        duplicateTask.setId(1);
        String taskJson = gson.toJson(duplicateTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    void handle_DeleteAllTasks_ShouldDeleteAllTasks() throws IOException, InterruptedException {

        Task task1 = new Task("task1", "description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        task1.setId(1);
        taskManager.addTask(task1);

        Task task2 = new Task("task2", "description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 11, 0),
                Duration.ofMinutes(30));
        task2.setId(2);
        taskManager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("{\"message\":\"All tasks deleted\"}", response.body());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getTasks().size());
    }

    @Test
    void handle_DeleteTaskById_ShouldDeleteTask() throws IOException, InterruptedException {

        Task task1 = new Task("task1", "description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        task1.setId(1);
        taskManager.addTask(task1);

        Task task2 = new Task("task2", "description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 11, 0),
                Duration.ofMinutes(30));
        task2.setId(2);
        taskManager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("{\"message\":\"Task deleted\"}", response.body());
        assertEquals(200, response.statusCode());
        assertEquals(1, taskManager.getTasks().size());
        assertEquals(task2, taskManager.getTasks().getFirst());
    }
}
