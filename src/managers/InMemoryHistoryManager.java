package managers;

import models.Task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task;
        Node next;
        Node prev;

        Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    private final Map<Long, Node> taskNodesHistory;
    private Node firstTaskNode;
    private Node lastTaskNode;

    public InMemoryHistoryManager() {
        taskNodesHistory = new LinkedHashMap<>();
    }

    @Override
    public List<Task> getTasksHistory() {
        List<Task> result = new ArrayList<>();
        Node current = firstTaskNode;
        while (current != null) {
            result.add(current.task);
            current = current.next;
        }
        return result;
    }

    @Override
    public void addTaskHistory(Task task) {
        if (task == null) return;

        long taskId = task.getId();
        removeTaskHistory(taskId); // Удаляем если уже есть

        linkLast(task);
        taskNodesHistory.put(taskId, lastTaskNode);
    }

    protected void linkLast(Task task) {
        final Node l = lastTaskNode;
        final Node newNode = new Node(l, task, null);
        lastTaskNode = newNode;
        if (l == null) {
            firstTaskNode = newNode;
        } else {
            l.next = newNode;
        }
    }

    private void unlink(Node node) {
        final Node next = node.next;
        final Node prev = node.prev;

        if (prev == null) {
            firstTaskNode = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            lastTaskNode = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        node.task = null;
    }

    @Override
    public void removeTaskHistory(Long id) {
        Node node = taskNodesHistory.get(id);
        if (node != null) {
            unlink(node);
            taskNodesHistory.remove(id);
        }
    }
}
