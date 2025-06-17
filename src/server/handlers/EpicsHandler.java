package server.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import models.EpicTask;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager) {
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
                        List<models.EpicTask> epics = taskManager.getEpicTasks();
                        sendSuccess(exchange, gson.toJson(epics));
                    } else if (pathParts.length == 3) {
                        long id = Long.parseLong(pathParts[2]);
                        models.EpicTask epic = taskManager.getEpicTask(id);
                        if (epic != null) {
                            sendSuccess(exchange, gson.toJson(epic));
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "POST":
                    String requestBody = readRequestBody(exchange);
                    models.EpicTask newEpic = gson.fromJson(requestBody, EpicTask.class);
                    taskManager.addEpicTask(newEpic);
                    sendCreated(exchange, gson.toJson(newEpic));
                    break;
                case "DELETE":
                    if (pathParts.length == 2) {
                        taskManager.deleteEpicTask();
                        sendSuccess(exchange, "{\"message\":\"All epics deleted\"}");
                    } else if (pathParts.length == 3) {
                        long id = Long.parseLong(pathParts[2]);
                        taskManager.removeEpicTask(id);
                        sendSuccess(exchange, "{\"message\":\"Epic deleted\"}");
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