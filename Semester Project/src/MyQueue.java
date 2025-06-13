public class MyQueue {
    private static class Node {
        Passenger data;
        Node next;

        Node(Passenger data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node front;
    private Node rear;

    public MyQueue() {
        this.front = null;
        this.rear = null;
    }

    public void enqueue(Passenger p) {
        Node newNode = new Node(p);
        if (rear == null) {
            front = rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
    }

    public Passenger dequeue() {
        if (isEmpty()) return null;
        Passenger result = front.data;
        front = front.next;
        if (front == null) rear = null;
        return result;
    }

    public Passenger peek() {
        return (isEmpty()) ? null : front.data;
    }

    public boolean isEmpty() {
        return front == null;
    }

    public void printQueue() {
        Node current = front;
        while (current != null) {
            System.out.println(current.data);
            current = current.next;
        }
    }
}