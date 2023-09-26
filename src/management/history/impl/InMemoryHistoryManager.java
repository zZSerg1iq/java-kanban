package management.history.impl;

import enity.Task;
import management.history.HistoryManager;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final LinkedList<Task> history;

    public InMemoryHistoryManager() {
        history = new LinkedList<>();
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        history.push(task);
        if (history.size() > 10) {
            history.removeFirst();
        }
    }
}
