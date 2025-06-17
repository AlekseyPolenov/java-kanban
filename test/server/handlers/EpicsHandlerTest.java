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

class EpicsHandlerTest {

    private HttpTaskServer taskServer;
    private HttpClient httpClient;
    private TaskManager taskManager;
    private Gson gson;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() throws IOException {
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
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void handle_GetAllEpics_ShouldReturnEpicsList() throws IOException, InterruptedException {

        EpicTask epic1 = new EpicTask("Epic 1", "Description", StatusEnum.NEW);
        EpicTask epic2 = new EpicTask("Epic 2", "Description", StatusEnum.NEW);
        taskManager.addEpicTask(epic1);
        taskManager.addEpicTask(epic2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type listType = new TypeToken<ArrayList<EpicTask>>(){}.getType();
        List<EpicTask> epics = gson.fromJson(response.body(), listType);

        assertEquals(2, epics.size());
        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));
    }

    @Test
    void handle_GetEpicById_ShouldReturnEpic() throws IOException, InterruptedException {

        EpicTask epic = new EpicTask("Test Epic", "Description", StatusEnum.NEW);
        taskManager.addEpicTask(epic);
        long epicId = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        EpicTask returnedEpic = gson.fromJson(response.body(), EpicTask.class);
        assertEquals(epic, returnedEpic);
    }

    @Test
    void handle_GetEpicById_NotFound_ShouldReturn404() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics/999"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    void handle_PostEpic_ShouldAddEpic() throws IOException, InterruptedException {

        EpicTask epic = new EpicTask("New Epic", "Description", StatusEnum.NEW);
        String epicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        EpicTask createdEpic = gson.fromJson(response.body(), EpicTask.class);
        epic.setId(createdEpic.getId());

        assertEquals(epic, createdEpic);
        assertEquals(createdEpic, taskManager.getEpicTask(createdEpic.getId()));
    }

    @Test
    void handle_PostEpic_WithSubtasks_ShouldUpdateEpicStatus() throws IOException, InterruptedException {

        EpicTask epic = new EpicTask("Epic with Subtasks", "Description", StatusEnum.NEW);
        taskManager.addEpicTask(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description", StatusEnum.NEW,
                LocalDateTime.now(),
                Duration.ofMinutes(30),
                epic.getId());

        SubTask subTask2 = new SubTask("SubTask 2", "Description", StatusEnum.DONE,
                LocalDateTime.now().plusHours(1),
                Duration.ofMinutes(30),
                epic.getId());

        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        String epicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics/" + epic.getId()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        EpicTask updatedEpic = gson.fromJson(response.body(), EpicTask.class);
        assertEquals(StatusEnum.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void handle_DeleteEpicById_ShouldDeleteEpic() throws IOException, InterruptedException {

        EpicTask epic1 = new EpicTask("Epic 1", "Description", StatusEnum.NEW);
        taskManager.addEpicTask(epic1);
        epic1.setId(1);

        EpicTask epic2 = new EpicTask("Epic 2", "Description", StatusEnum.NEW);
        taskManager.addEpicTask(epic2);
        epic2.setId(2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics/1"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("{\"message\":\"Epic deleted\"}", response.body());
        assertEquals(200, response.statusCode());
        assertEquals(1, taskManager.getEpicTasks().size());
        assertEquals(epic2, taskManager.getEpicTasks().getFirst());
    }

    @Test
    void handle_DeleteEpicAllEicTask_ShouldDeleteEpic() throws IOException, InterruptedException {

        EpicTask epic1 = new EpicTask("Epic 1", "Description", StatusEnum.NEW);
        taskManager.addEpicTask(epic1);
        epic1.setId(1);

        EpicTask epic2 = new EpicTask("Epic 2", "Description", StatusEnum.NEW);
        taskManager.addEpicTask(epic2);
        epic2.setId(2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/epics"))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals("{\"message\":\"All epics deleted\"}", response.body());
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.getEpicTasks().size());
    }
}
