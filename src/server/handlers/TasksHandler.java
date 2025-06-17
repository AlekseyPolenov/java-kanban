package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import models.Task;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        List<Task> tasks = taskManager.getTasks();
                        sendSuccess(exchange, gson.toJson(tasks));
                    } else if (pathParts.length == 3) {
                        long id = Long.parseLong(pathParts[2]);
                        Task task = taskManager.getTask(id);
                        if (task != null) {
                            sendSuccess(exchange, gson.toJson(task));
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "POST":
                    String requestBody = readRequestBody(exchange);
                    Task newTask = gson.fromJson(requestBody, Task.class);
                    try {
                        taskManager.addTask(newTask);
                        sendCreated(exchange, gson.toJson(newTask));
                    } catch (IllegalStateException e) {
                        sendNotAcceptable(exchange, e.getMessage());
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 2) {
                        taskManager.deleteTask();
                        sendSuccess(exchange, "{\"message\":\"All tasks deleted\"}");
                    } else if (pathParts.length == 3) {
                        long id = Long.parseLong(pathParts[2]);
                        taskManager.removeTask(id);
                        sendSuccess(exchange, "{\"message\":\"Task deleted\"}");
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            System.err.println("Error: ID must be a number.");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Error: URL must contain an ID.");
        } catch (Exception e) {
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
