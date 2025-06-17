package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import managers.TaskManager;
import models.EpicTask;
import models.StatusEnum;
import models.SubTask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer server;
    private HttpClient httpClient;
    private final String baseUrl = "http://localhost:8080";
    private TaskManager taskManager = Managers.getInMemoryTaskManager();
    private Gson gson;

    @BeforeEach
    void setUp() throws IOException {
        server = new HttpTaskServer(taskManager);
        server.start();
        httpClient = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    private List<Task> parseTasks (String bodyGson) {
        Type listOfTasks = new TypeToken<ArrayList<Task>>(){}.getType();
        return gson.fromJson(bodyGson, listOfTasks);
    }

    private List<EpicTask> parseEpicTasks (String bodyGson) {
        Type listOfTasks = new TypeToken<ArrayList<EpicTask>>(){}.getType();
        return gson.fromJson(bodyGson, listOfTasks);
    }

    private List<SubTask> parseSubTasks (String bodyGson) {
        Type listOfTasks = new TypeToken<ArrayList<SubTask>>(){}.getType();
        return gson.fromJson(bodyGson, listOfTasks);
    }

    @Test
    void getTasksShouldReturnEmptyList() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "description1", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        taskManager.addTask(task1);

        Task task2 = new Task("task2", "description2", StatusEnum.NEW,
                LocalDateTime.of(2023, 2, 1, 10, 0),
                Duration.ofMinutes(30));
        taskManager.addTask(task2);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> tasks = parseTasks(response.body());
        assertEquals(2, tasks.size());
        assertEquals(task1, tasks.getFirst());
        assertEquals(task2, tasks.getLast());
    }

    @Test
    void getEpicsShouldReturnEmptyList() throws IOException, InterruptedException {

        EpicTask epicTask1 = new EpicTask("epicTask1", "description1", StatusEnum.NEW);
        taskManager.addEpicTask(epicTask1);

        EpicTask epicTask2 = new EpicTask("epicTask2", "description2", StatusEnum.NEW);
        taskManager.addEpicTask(epicTask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<EpicTask> epicTasks = parseEpicTasks(response.body());
        assertEquals(2, epicTasks.size());
        assertEquals(epicTask1, epicTasks.getFirst());
        assertEquals(epicTask2, epicTasks.getLast());
    }


    @Test
    void getSubtasksShouldReturnEmptyList() throws IOException, InterruptedException {

        SubTask subTask1 = new SubTask("subTask1", "description1", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30), 1);
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("subTask2", "description2", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30), 1);
        taskManager.addSubTask(subTask2);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<SubTask> subTasks = parseSubTasks(response.body());
        assertEquals(2, subTasks.size());
        assertEquals(subTask1, subTasks.getFirst());
        assertEquals(subTask2, subTasks.getLast());
    }

    @Test
    void getHistoryShouldReturnEmptyList() throws IOException, InterruptedException {

        Task task1 = new Task("task1", "description1", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        taskManager.addTask(task1);

        Task task2 = new Task("task2", "description2", StatusEnum.NEW,
                LocalDateTime.of(2023, 2, 1, 10, 0),
                Duration.ofMinutes(30));
        taskManager.addTask(task2);

        taskManager.getTask(1);
        taskManager.getTask(2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> tasks = parseTasks(response.body());
        assertEquals(2, tasks.size());
        assertEquals(task1, tasks.getFirst());
        assertEquals(task2, tasks.getLast());
    }

    @Test
    void getPrioritizedShouldReturnEmptyList() throws IOException, InterruptedException {

        Task task1 = new Task("task1", "description1", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30));
        taskManager.addTask(task1);

        Task task2 = new Task("task2", "description2", StatusEnum.NEW,
                LocalDateTime.of(2023, 2, 1, 10, 0),
                Duration.ofMinutes(30));
        taskManager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        List<Task> tasks = parseTasks(response.body());
        assertEquals(2, tasks.size());
        assertEquals(task1, tasks.getFirst());
        assertEquals(task2, tasks.getLast());
    }
}
