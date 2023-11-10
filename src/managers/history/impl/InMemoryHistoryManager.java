package managers.history.impl;

import enity.Task;
import managers.history.HistoryManager;

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
                head.prev = null;
            } else if (tail != null && Objects.equals(tailNode, node)) {
                tail = tail.prev;
                tail.next = null;
            } else {
                node.prev.next = node.next;
                node.next.prev = node.prev;
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
        Node node = nodeMap.get(task.getTaskId());

        if (nodeMap.containsKey(task.getTaskId()) && node != null) {
            remove(task.getTaskId());
            history.linkLast(node);
            nodeMap.put(task.getTaskId(), node);
        } else {
            history.linkLast(new Node(task));
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


}
