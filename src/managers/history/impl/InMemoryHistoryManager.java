package managers.history.impl;

import enity.Task;
import managers.history.HistoryManager;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final int MAX_SIZE = 10;

    static class CustomLinkedList implements Iterable<Task> {

        Node head;
        Node tail;
        int size;

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
                    Task task = currentNode.getTask();
                    currentNode = currentNode.getNext();
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
                head = currentHead.getNext();
                if (head != null) {
                    head.setPrev(null);
                }
            }
        }

        public void linkLast(Node newNode) {
            newNode.setNext(null);

            if (tail == null) {
                head = newNode;
                tail = newNode;
                newNode.setPrev(newNode);
                newNode.setNext(newNode);
            } else {
                newNode.setPrev(tail);
                tail.setNext(newNode);
                tail = newNode;
            }
            size++;
        }

        public List<Task> getTasks() {
            List<Task> taskList = new ArrayList<>();
            Node current = head;

            while (current != null) {
                taskList.add(current.getTask());

                if (current != current.getNext()) {
                    current = current.getNext();
                } else break;

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
                if (headNode.getNext() != null) {
                    head = head.getNext();
                    head.setPrev(null);
                } else {
                    head = null;
                }
            } else if (tail != null && Objects.equals(tailNode, node)) {
                tail = tail.getPrev();
                tail.setNext(null);
            } else {
                node.getPrev().setNext(node.getNext());
                node.getNext().setPrev(node.getPrev());
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

        if (node != null && nodeMap.containsKey(task.getTaskId())) {
            remove(task.getTaskId());
            history.linkLast(node);
            nodeMap.put(task.getTaskId(), node);
        } else {
            node = new Node(task);
            history.linkLast(node);
            nodeMap.put(task.getTaskId(), node);

            if (history.size() > MAX_SIZE) {
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
