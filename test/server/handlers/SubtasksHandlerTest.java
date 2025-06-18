package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.Managers;
import managers.TaskManager;
import models.EpicTask;
import models.StatusEnum;
import models.SubTask;
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

import static org.junit.jupiter.api.Assertions.*;

public class SubtasksHandlerTest {

    private HttpTaskServer taskServer;
    private HttpClient httpClient;
    private TaskManager taskManager;
    private Gson gson;
    private final String baseUrl = "http://localhost:8080";
    private EpicTask testEpic;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getInMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        httpClient = HttpClient.newHttpClient();
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();

        testEpic = new EpicTask("Test Epic", "Description", StatusEnum.NEW);
        taskManager.addEpicTask(testEpic);

        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void handle_GetAllSubtasks_ShouldReturnSubtasksList() throws IOException, InterruptedException {

        SubTask subTask1 = new SubTask("SubTask 1", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30),
                testEpic.getId());

        SubTask subTask2 = new SubTask("SubTask 2", "Description", StatusEnum.IN_PROGRESS,
                LocalDateTime.of(2023, 1, 1, 11, 0),
                Duration.ofMinutes(45),
                testEpic.getId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type listType = new TypeToken<ArrayList<SubTask>>(){}.getType();
        List<SubTask> subtasks = gson.fromJson(response.body(), listType);

        assertEquals(2, subtasks.size());
        assertTrue(subtasks.stream().anyMatch(st -> st.getId() == subTask1.getId()));
        assertTrue(subtasks.stream().anyMatch(st -> st.getId() == subTask2.getId()));
    }

    @Test
    void handle_GetSubtaskById_ShouldReturnSubtask() throws IOException, InterruptedException {

        SubTask subTask = new SubTask("Test SubTask", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30),
                testEpic.getId());

        taskManager.addSubTask(subTask);
        long subTaskId = subTask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks/" + subTaskId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        SubTask returnedSubTask = gson.fromJson(response.body(), SubTask.class);
        assertEquals(subTask, returnedSubTask);
        assertEquals(testEpic.getId(), returnedSubTask.getEpicId());
    }

    @Test
    void handle_GetSubtaskByEpicId_ShouldReturnEpicSubtasks() throws IOException, InterruptedException {

        SubTask subTask1 = new SubTask("SubTask 1", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30),
                testEpic.getId());
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "Description", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 11, 0),
                Duration.ofMinutes(45),
                testEpic.getId());
        taskManager.addSubTask(subTask2);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks/epic/" + testEpic.getId()))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(200, response.statusCode());

        Type listType = new TypeToken<ArrayList<SubTask>>(){}.getType();
        List<SubTask> subtasks = gson.fromJson(response.body(), listType);

        assertEquals(2, subtasks.size());
        assertTrue(subtasks.stream().allMatch(st -> st.getEpicId() == testEpic.getId()));
    }

    @Test
    void handle_PostSubtask_ShouldAddSubtask() throws IOException, InterruptedException {

        SubTask subTask = new SubTask("New SubTask", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30),
                testEpic.getId());
        String subTaskJson = gson.toJson(subTask);


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


        assertEquals(201, response.statusCode());

        SubTask createdSubTask = gson.fromJson(response.body(), SubTask.class);
        subTask.setId(createdSubTask.getId());

        assertEquals(subTask, createdSubTask);
        assertEquals(createdSubTask, taskManager.getSubTask(createdSubTask.getId()));

        EpicTask updatedEpic = taskManager.getEpicTask(testEpic.getId());
        assertNotNull(updatedEpic.getStartTime());
        assertNotNull(updatedEpic.getDuration());
    }

    @Test
    void handle_PostSubtask_ShouldReturnError_WhenEpicNotExists() throws IOException, InterruptedException {

        SubTask subTask = new SubTask("subTask", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30),
                testEpic.getId());
        taskManager.addSubTask(subTask);
        String subTaskJson = gson.toJson(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks/1"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode());
    }

    @Test
    void handle_DeleteAllSubtasks_ShouldDeleteAllSubtasks() throws IOException, InterruptedException {

        SubTask subTask1 = new SubTask("SubTask 1", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30),
                testEpic.getId());

        SubTask subTask2 = new SubTask("SubTask 2", "Description", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 11, 0),
                Duration.ofMinutes(45),
                testEpic.getId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("{\"message\":\"All subtasks deleted\"}", response.body());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getSubTasks().size());
    }

    @Test
    void handle_DeleteSubtaskById_ShouldDeleteSubtask() throws IOException, InterruptedException {

        SubTask subTask1 = new SubTask("SubTask 1", "Description", StatusEnum.NEW,
                LocalDateTime.of(2023, 1, 1, 10, 0),
                Duration.ofMinutes(30),
                testEpic.getId());

        SubTask subTask2 = new SubTask("SubTask 2", "Description", StatusEnum.DONE,
                LocalDateTime.of(2023, 1, 1, 11, 0),
                Duration.ofMinutes(45),
                testEpic.getId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/subtasks/2"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("{\"message\":\"Subtask deleted\"}", response.body());
        assertEquals(200, response.statusCode());
        assertEquals(1, taskManager.getSubTasks().size());
        assertEquals(subTask2, taskManager.getSubTasks().getFirst());

        EpicTask updatedEpic = taskManager.getEpicTask(testEpic.getId());
        assertEquals(subTask2.getStartTime(), updatedEpic.getStartTime());
    }
}
