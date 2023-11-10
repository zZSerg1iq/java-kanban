package managers.history.impl;

import enity.Task;

import java.util.Objects;

class Node {
    Task task;
    Node next = null;
    Node prev = null;

    public Node(Task task) {
        this.task = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(task, node.task);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task);
    }

    @Override
    public String toString() {
        return "Node{" +
                "task=" + task +
                ", next=" + (next != null) +
                ", prev=" + (prev != null) +
                '}';
    }
}
