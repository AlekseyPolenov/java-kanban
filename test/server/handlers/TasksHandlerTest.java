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
                LocalDateTime.of(2023, 1, 1, 10, 0), Duration.ofMinutes(30));

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

//    @Test
//    void handle_GetTaskById_ShouldReturnTask() throws IOException {
//        // Arrange
//        when(exchange.getRequestMethod()).thenReturn("GET");
//        when(exchange.getRequestURI().getPath()).thenReturn("/tasks/1");
//
//        Task task = new Task("Test", "Description", StatusEnum.NEW,
//                LocalDateTime.now(), Duration.ofMinutes(30));
//        when(taskManager.getTask(1L)).thenReturn(task);
//
//        OutputStream outputStream = mock(OutputStream.class);
//        when(exchange.getResponseBody()).thenReturn(outputStream);
//
//        // Act
//        tasksHandler.handle(exchange);
//
//        // Assert
//        verify(taskManager).getTask(1L);
//        verify(exchange).sendResponseHeaders(200, anyInt());
//    }
//
//    @Test
//    void handle_GetTaskById_NotFound_ShouldReturn404() throws IOException {
//        // Arrange
//        when(exchange.getRequestMethod()).thenReturn("GET");
//        when(exchange.getRequestURI().getPath()).thenReturn("/tasks/1");
//        when(taskManager.getTask(1L)).thenReturn(null);
//
//        OutputStream outputStream = mock(OutputStream.class);
//        when(exchange.getResponseBody()).thenReturn(outputStream);
//
//        // Act
//        tasksHandler.handle(exchange);
//
//        // Assert
//        verify(exchange).sendResponseHeaders(404, anyInt());
//    }
//
//    @Test
//    void handle_PostTask_ShouldAddTask() throws IOException {
//        // Arrange
//        when(exchange.getRequestMethod()).thenReturn("POST");
//        when(exchange.getRequestURI().getPath()).thenReturn("/tasks");
//
//        Task task = new Task("Test", "Description", StatusEnum.NEW,
//                LocalDateTime.now(), Duration.ofMinutes(30));
//        String requestBody = new Gson().toJson(task);
//        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(requestBody.getBytes()));
//
//        OutputStream outputStream = mock(OutputStream.class);
//        when(exchange.getResponseBody()).thenReturn(outputStream);
//
//        // Act
//        tasksHandler.handle(exchange);
//
//        // Assert
//        verify(taskManager).addTask(any(Task.class));
//        verify(exchange).sendResponseHeaders(201, anyInt());
//    }
//
//    @Test
//    void handle_DeleteAllTasks_ShouldDeleteAllTasks() throws IOException {
//        // Arrange
//        when(exchange.getRequestMethod()).thenReturn("DELETE");
//        when(exchange.getRequestURI().getPath()).thenReturn("/tasks");
//
//        OutputStream outputStream = mock(OutputStream.class);
//        when(exchange.getResponseBody()).thenReturn(outputStream);
//
//        // Act
//        tasksHandler.handle(exchange);
//
//        // Assert
//        verify(taskManager).deleteTask();
//        verify(exchange).sendResponseHeaders(200, anyInt());
//    }
//
//    @Test
//    void handle_DeleteTaskById_ShouldDeleteTask() throws IOException {
//        // Arrange
//        when(exchange.getRequestMethod()).thenReturn("DELETE");
//        when(exchange.getRequestURI().getPath()).thenReturn("/tasks/1");
//
//        OutputStream outputStream = mock(OutputStream.class);
//        when(exchange.getResponseBody()).thenReturn(outputStream);
//
//        // Act
//        tasksHandler.handle(exchange);
//
//        // Assert
//        verify(taskManager).removeTask(1L);
//        verify(exchange).sendResponseHeaders(200, anyInt());
//    }
}
