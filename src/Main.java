import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            int command = scanner.nextInt();

            switch (command) {
                case 1:
                    System.out.println("Задачи");
                    List<Task> tasks = inMemoryTaskManager.getTasks();
                    System.out.println(tasks);
                    System.out.println("1 - Удалить все задачи \n2 - Получить по индификатору " +
                                       "\n3 - Удалить по индификатору \n4 - Создать задачу" +
                                        "\n5 - Редактировать");

                    int commandTasks = scanner.nextInt();

                    if (commandTasks == 1) {

                        System.out.println("Удаление...");
                        inMemoryTaskManager.deleteTask();

                    } else if (commandTasks == 2) {
                        System.out.println("Введите ID");
                        int idTask = scanner.nextInt();
                        System.out.println(inMemoryTaskManager.getTask(idTask));

                    } else if (commandTasks == 3) {

                        System.out.println("Введите ID");
                        long deleteTask = scanner.nextInt();

                        System.out.println("Объект удален");
                        inMemoryTaskManager.removeTask(deleteTask);

                    } else if (commandTasks == 4) {

                        scanner.nextLine();
                        System.out.println("Введите название задачи");
                        String name = scanner.nextLine();

                        System.out.println("Введите описание задачи");
                        String description = scanner.nextLine();

                        System.out.println("Введите статус NEW, IN_PROGRESS, DONE");
                        StatusEnum statusEnum = StatusEnum.valueOf(scanner.next());

                        System.out.println("Введите дату начала (yyyy-MM-dd HH:mm)");
                        scanner.nextLine();
                        String dateInput = scanner.nextLine();
                        LocalDateTime startTime = dateInput.isEmpty() ? null :
                                LocalDateTime.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                        System.out.println("Введите продолжительность в минутах");
                        String durationInput = scanner.nextLine();
                        Duration duration = durationInput.isEmpty() ? null :
                                Duration.ofMinutes(Long.parseLong(durationInput));

                        Task newTask = new Task(name, description, statusEnum, startTime, duration);
                        inMemoryTaskManager.addTask(newTask);

                    } else if (commandTasks == 5) {

                        scanner.nextLine();
                        System.out.println("Введите ID");
                        long idTask = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Введите название задачи");
                        String name = scanner.nextLine();

                        System.out.println("Введите описание задачи");
                        String description = scanner.nextLine();

                        System.out.println("Введите статус задачи NEW, IN_PROGRESS, DONE");
                        StatusEnum status = StatusEnum.valueOf(scanner.next());

                        System.out.println("Введите дату начала (yyyy-MM-dd HH:mm)");
                        scanner.nextLine();
                        String dateInput = scanner.nextLine();
                        LocalDateTime startTime = dateInput.isEmpty() ? null :
                                LocalDateTime.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                        System.out.println("Введите продолжительность в минутах");
                        String durationInput = scanner.nextLine();
                        Duration duration = durationInput.isEmpty() ? null :
                                Duration.ofMinutes(Long.parseLong(durationInput));

                        Task task = new Task(name, description, status, startTime, duration);
                        task.setId(idTask);

                        inMemoryTaskManager.updateTask(task);
                    }
                    break;

                case 2:
                    System.out.println("Эпики");
                    List<EpicTask> epicTasks = inMemoryTaskManager.getEpicTasks();
                    System.out.println(epicTasks);
                    System.out.println("1 - Удалить все задачи \n2 - Получить по индификатору " +
                            "\n3 - Удалить по индификатору \n4 - Создать задачу" +
                            "\n5 - Редактировать \n6 - Вывести все подзадачи");

                    int commandEpicTask = scanner.nextInt();

                    if (commandEpicTask == 1) {

                        System.out.println("Удаление...");
                        inMemoryTaskManager.deleteEpicTask();

                    } else if (commandEpicTask == 2) {

                        System.out.println("Введите ID");
                        int idEpicTask = scanner.nextInt();
                        System.out.println(inMemoryTaskManager.getEpicTask(idEpicTask));

                    } else if (commandEpicTask == 3) {

                        System.out.println("Введите ID");
                        long deleteEpicTask = scanner.nextInt();

                        System.out.println("Объект удален");
                        inMemoryTaskManager.removeEpicTask(deleteEpicTask);
                    } else if (commandEpicTask == 4) {

                        scanner.nextLine();
                        System.out.println("Введите название эпика");
                        String name = scanner.nextLine();

                        System.out.println("Введите описание эпика");
                        String description = scanner.nextLine();

                        EpicTask newEpicTask = new EpicTask(name, description, StatusEnum.NEW);
                        inMemoryTaskManager.addEpicTask(newEpicTask);

                    } else if (commandEpicTask == 5) {

                        scanner.nextLine();
                        System.out.println("Введите ID");
                        long idTask = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Введите название епика");
                        String name = scanner.nextLine();

                        System.out.println("Введите описание епика");
                        String description = scanner.nextLine();

                        System.out.println("Введите статус епика NEW, IN_PROGRESS, DONE");
                        StatusEnum status = StatusEnum.valueOf(scanner.next());

                        EpicTask task = new EpicTask(name, description, status);
                        task.setId(idTask);

                        inMemoryTaskManager.updateEpicTask(task);

                    } else if (commandEpicTask == 6) {
                        System.out.println("Введите ID");
                        long idEpicTask = scanner.nextInt();
                        System.out.println(inMemoryTaskManager.getSubTasks(idEpicTask));
                    }
                    break;
                case 3:
                    System.out.println("Подзадачи");
                    List<SubTask> subTasks = inMemoryTaskManager.getSubTasks();
                    System.out.println(subTasks);
                    System.out.println("1 - Удалить все подзадачи \n2 - Получить по индификатору " +
                            "\n3 - Удалить по индификатору \n4 - Создать подзадачу" +
                            "\n5 - Редактировать");

                    int commandSubTask = scanner.nextInt();
                        if (commandSubTask == 1) {

                            System.out.println("Удаление...");
                            inMemoryTaskManager.deleteSubTask();

                        } else if (commandSubTask == 2) {

                            System.out.println("Введите ID");
                            int idSubTask = scanner.nextInt();
                            System.out.println(inMemoryTaskManager.getSubTask(idSubTask));

                        } else if (commandSubTask == 3) {

                            System.out.println("Введите ID");
                            long deleteSubTask = scanner.nextInt();
                            System.out.println("Объект удален");
                            inMemoryTaskManager.removeSubTask(deleteSubTask);

                        } else if (commandSubTask == 4) {

                            scanner.nextLine();
                            System.out.println("Список задач эпиков");
                            System.out.println(inMemoryTaskManager.getEpicTasks());
                            System.out.println("Напишите ID Эпика к которому хотите добивать подзадачу");
                            long idEpicTask = scanner.nextInt();
                            EpicTask epicTask = inMemoryTaskManager.getEpicTask(idEpicTask);

                            scanner.nextLine();

                            System.out.println("Введите название подзадачи");
                            String name = scanner.nextLine();

                            System.out.println("Введите описание подзадачи");
                            String description = scanner.nextLine();

                            System.out.println("Введите статус NEW, IN_PROGRESS, DONE");
                            StatusEnum status = StatusEnum.valueOf(scanner.next());

                            System.out.println("Введите дату начала (yyyy-MM-dd HH:mm)");
                            scanner.nextLine();
                            String dateInput = scanner.nextLine();
                            LocalDateTime startTime = dateInput.isEmpty() ? null :
                                    LocalDateTime.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                            System.out.println("Введите продолжительность в минутах");
                            String durationInput = scanner.nextLine();
                            Duration duration = durationInput.isEmpty() ? null :
                                    Duration.ofMinutes(Long.parseLong(durationInput));

                            SubTask subTask = new SubTask(name, description, status, startTime, duration, epicTask.getId());
                            inMemoryTaskManager.addSubTask(subTask);

                        } else if (commandSubTask == 5) {


                            scanner.nextLine();
                            System.out.println("Введите ID");
                            long idTask = scanner.nextInt();
                            scanner.nextLine();

                            System.out.println("Введите название сабтаски");
                            String name = scanner.nextLine();

                            System.out.println("Введите описание сабтаски");
                            String description = scanner.nextLine();

                            System.out.println("Введите статус сабтаски NEW, IN_PROGRESS, DONE");
                            StatusEnum status = StatusEnum.valueOf(scanner.next());

                            System.out.println("Введите дату начала (yyyy-MM-dd HH:mm)");
                            scanner.nextLine();
                            String dateInput = scanner.nextLine();
                            LocalDateTime startTime = dateInput.isEmpty() ? null :
                                    LocalDateTime.parse(dateInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                            System.out.println("Введите продолжительность в минутах");
                            String durationInput = scanner.nextLine();
                            Duration duration = durationInput.isEmpty() ? null :
                                    Duration.ofMinutes(Long.parseLong(durationInput));

                            SubTask subTask = new SubTask(name, description, status, startTime, duration,0);
                            subTask.setId(idTask);

                            inMemoryTaskManager.updateSubTask(subTask);
                        }
                        break;
                case 4:
                    List<Task> history = inMemoryTaskManager.getTasksHistory();
                    System.out.println("История просмотренных задач");
                    System.out.println(history);
                    break;
                case 0:
                    return;
            }
        }

    }

    static void printMenu() {
        System.out.println("1 - Работа с задачи");
        System.out.println("2 - Работа с эпиками");
        System.out.println("3 - Работа с подзадачами");
        System.out.println("4 - Вывести историю просмотра");
        System.out.println("0 - Выход");
    }
}
