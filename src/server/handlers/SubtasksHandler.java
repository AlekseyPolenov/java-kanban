package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager) {
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
                        List<models.SubTask> subtasks = taskManager.getSubTasks();
                        sendSuccess(exchange, gson.toJson(subtasks));
                    } else if (pathParts.length == 3) {
                        long id = Long.parseLong(pathParts[2]);
                        models.SubTask subtask = taskManager.getSubTask(id);
                        if (subtask != null) {
                            sendSuccess(exchange, gson.toJson(subtask));
                        } else {
                            sendNotFound(exchange);
                        }
                    } else if (pathParts.length == 4 && pathParts[2].equals("epic")) {
                        long epicId = Long.parseLong(pathParts[3]);
                        List<models.SubTask> epicSubtasks = taskManager.getSubTasks(epicId);
                        sendSuccess(exchange, gson.toJson(epicSubtasks));
                    }
                    break;
                case "POST":
                    String requestBody = readRequestBody(exchange);
                    models.SubTask newSubtask = gson.fromJson(requestBody, models.SubTask.class);
                    try {
                        taskManager.addSubTask(newSubtask);
                        sendCreated(exchange, gson.toJson(newSubtask));
                    } catch (IllegalStateException e) {
                        sendNotAcceptable(exchange, e.getMessage());
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 2) {
                        taskManager.deleteSubTask();
                        sendSuccess(exchange, "{\"message\":\"All subtasks deleted\"}");
                    } else if (pathParts.length == 3) {
                        long id = Long.parseLong(pathParts[2]);
                        taskManager.removeSubTask(id);
                        sendSuccess(exchange, "{\"message\":\"Subtask deleted\"}");
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
