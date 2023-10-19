package management.history.impl;

import enity.Task;
import management.history.HistoryManager;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    static class CustomLinkedList implements Iterable<Task> {

        private Node head;
        private Node tail;
        private int size;


        @Override
        public Iterator<Task> iterator() {
            return new Iterator<Task>() {
                private Node currentNode = head;

                @Override
                public boolean hasNext() {
                    return currentNode != null;
                }

                @Override
                public Task next() {
                    if (currentNode == null) {
                        throw new NoSuchElementException();
                    }
                    Task task = currentNode.task;
                    currentNode = currentNode.next;
                    return task;
                }
            };
        }

        public int size() {
            return size;
        }

        public void removeFirst() {
            if (head != null) {
                Node currentHead = head;
                head = currentHead.next;
                if (head != null) {
                    head.prev = null;
                }
            }
        }

        public void linkLast(Node newNode) {
            newNode.next = null;

            if (tail == null) {
                head = newNode;
                tail = newNode;
            } else {
                newNode.prev = tail;
                tail.next = newNode;
                tail = newNode;
            }
            size++;
        }

        public List<Task> getTasks() {
            List<Task> taskList = new ArrayList<>();
            Node current = head;
            while (current != null) {
                taskList.add(current.task);
                current = current.next;
            }
            return taskList;
        }

        public void removeNode(Node node) {
            if (node == null) {
                return;
            }

            Node headNode = head;
            Node tailNode = tail;

            if (Objects.equals(headNode, node)) {
                head = head.next;
            } else if (Objects.equals(tailNode, node)) {
                tail = tail.prev;
            } else {
                Node current = headNode.next;

                while (!Objects.equals(node, current)) {
                    current = current.next;
                }

                if (current.next != null) {
                    current.next.prev = current.prev;
                }
                if (current.prev != null) {
                    current.prev.next = current.next;
                }
            }

            size--;
        }
    }


    private final CustomLinkedList history;

    private final Map<Integer, Node> nodeMap;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList();
        nodeMap = new HashMap<>();
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public void add(Task task) {
        Node node = nodeMap.getOrDefault(task.getTaskId(), new Node(task));

        if (nodeMap.containsKey(task.getTaskId())) {
            remove(task.getTaskId());
            history.linkLast(node);
            nodeMap.put(task.getTaskId(), node);
        } else {
            history.linkLast(node);
            nodeMap.put(task.getTaskId(), node);

            if (history.size() > 10) {
                history.removeFirst();
            }
        }
    }

    @Override
    public void remove(int id) {
        if (nodeMap.containsKey(id)) {
            history.removeNode(nodeMap.get(id));
            nodeMap.remove(id);
        }
    }


    public static void main(String[] args) {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();

        for (int i = 1; i < 20; i++) {
            Task task = new Task("task_" + i, "desc");
            task.setTaskId(i);
            historyManager.add(task);
        }

        historyManager.getHistory().forEach(System.out::println);
        System.out.println("-------------------------");

        Task task15 = new Task("task_" + 15, "desc");
        task15.setTaskId(15);
        Task task19 = new Task("task_" + 19, "desc");
        task19.setTaskId(19);
        Task task13 = new Task("task_" + 13, "desc");
        task13.setTaskId(13);
        historyManager.add(task19);
        historyManager.add(task15);
        historyManager.add(task13);


        for (Task t: historyManager.history) {
            System.out.println(t);
        }


    }
}
