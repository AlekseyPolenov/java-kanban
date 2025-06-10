import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path file;

    public FileBackedTaskManager() {
        this.file = Paths.get("backup.csv");
        if (file.toFile().exists()) {
            this.loadFromFile(file);
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

    private void saveToFile() {
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

    private String taskToCsvString(Task task) {
        String[] fields = new String[] {
                String.valueOf(task.getId()),
                getType(task),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                task instanceof SubTask ? String.valueOf(((SubTask) task).getEpicId()) : ""
        };
        return String.join(",", fields);
    }

    private String getType(Task task) {
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
                        addEpicTask((EpicTask) task);
                    } else if (task instanceof SubTask) {
                        addSubTask((SubTask) task);
                    } else {
                        addTask(task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при загрузке из файла", e);
        }
    }

    private Task fromCsvString(String value) {
        String[] fields = value.split(",");

        long id = Long.parseLong(fields[0]);
        String type = fields[1];
        String name = fields[2];
        StatusEnum status = StatusEnum.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case "Task":
                Task task = new Task(name, description, status);
                task.setId(id);
                return task;
            case "EpicTask":
                EpicTask epicTask = new EpicTask(name, description, status);
                epicTask.setId(id);
                return epicTask;
            case "SubTask":
                long parentEpicId = Long.parseLong(fields.length > 5 ? fields[5] : "");
                return new SubTask(name, description, status, parentEpicId);
            default:
                return null;
        }
    }
}
