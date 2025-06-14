import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path file;

    public FileBackedTaskManager(Path file) {
        this.file = file;
        try {
            if (file.toFile().exists()) {
                this.loadFromFile(file);
            } else {
                throw new ManagerLoadException("Файл не существует: " + file, null);
            }
        } catch (Exception e) {
            throw new ManagerLoadException("Ошибка при загрузке из файла", e);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        saveToFile();
    }

    @Override
    public void addEpicTask(EpicTask epic) {
        super.addEpicTask(epic);
        saveToFile();

    }

    @Override
    public void addSubTask(SubTask subtask) {
        super.addSubTask(subtask);
        saveToFile();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        saveToFile();
    }

    @Override
    public void updateEpicTask(EpicTask epic) {
        super.updateEpicTask(epic);
        saveToFile();
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        super.updateSubTask(subtask);
        saveToFile();
    }

    @Override
    public void removeTask(long id) {
        super.removeTask(id);
        saveToFile();
    }

    @Override
    public void removeEpicTask(long id) {
        super.removeEpicTask(id);
        saveToFile();
    }

    @Override
    public void removeSubTask(long id) {
        super.removeSubTask(id);
        saveToFile();
    }

    @Override
    public void deleteTask() {
        super.deleteTask();
        saveToFile();
    }

    @Override
    public void deleteEpicTask() {
        super.deleteEpicTask();
        saveToFile();
    }

    @Override
    public void deleteSubTask() {
        super.deleteSubTask();
        saveToFile();
    }

    protected void saveToFile() {
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {

            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getTasks()) {
                writer.write(taskToCsvString(task));
                writer.newLine();
            }

            for (EpicTask epic : getEpicTasks()) {
                writer.write(taskToCsvString(epic));
                writer.newLine();
            }

            for (SubTask subtask : getSubTasks()) {
                writer.write(taskToCsvString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    protected String taskToCsvString(Task task) {
        String[] fields = new String[] {
                String.valueOf(task.getId()),
                getType(task),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                task instanceof SubTask ? String.valueOf(((SubTask) task).getEpicId()) : "",
                task.getStartTime() != null ? task.getStartTime().toString() : "",
                task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : ""
        };
        return String.join(",", fields);
    }

    protected String getType(Task task) {
        if (task instanceof EpicTask) {
            return "EpicTask";
        } else if (task instanceof SubTask) {
            return "SubTask";
        } else {
            return "Task";
        }
    }

    private void loadFromFile(Path file) {

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {

            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                Task task = fromCsvString(line);
                if (task != null) {
                    if (task instanceof EpicTask) {
                        taskEpicMap.put(task.getId(), (EpicTask) task);
                    } else if (task instanceof SubTask) {
                        SubTask subTask = (SubTask) task;
                        subTaskMap.put(task.getId(), subTask);
                        taskEpicMap.get(subTask.getEpicId())
                                .getSubTasks()
                                .add(subTask);
                    } else {
                        taskMap.put(task.getId(), task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при загрузке из файла", e);
        }
    }

    protected Task fromCsvString(String value) {
        String[] fields = value.split(",");

        long id = Long.parseLong(fields[0]);
        String type = fields[1];
        String name = fields[2];
        StatusEnum status = StatusEnum.valueOf(fields[3]);
        String description = fields[4];

        LocalDateTime startTime = fields.length > 6 && !fields[6].isEmpty()
                ? LocalDateTime.parse(fields[6]) : null;
        Duration duration = fields.length > 7 && !fields[7].isEmpty()
                ? Duration.ofMinutes(Long.parseLong(fields[7])) : null;

        switch (type) {
            case "Task":
                Task task = new Task(name, description, status, startTime, duration);
                task.setId(id);
                return task;
            case "EpicTask":
                EpicTask epicTask = new EpicTask(name, description, status);
                epicTask.setId(id);
                return epicTask;
            case "SubTask":
                if (fields.length > 5) {
                    long parentEpicId = Long.parseLong(fields[5]);
                    SubTask subTask = new SubTask(name, description, status, startTime, duration, parentEpicId);
                    subTask.setId(id);
                    return subTask;
                }
                return null;
            default:
                return null;
        }
    }
}
