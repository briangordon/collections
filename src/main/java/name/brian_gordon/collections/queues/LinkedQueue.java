package name.brian_gordon.collections.queues;

/**
 * A simple doubly-linked queue for single-threaded applications.
 *
 * @author Brian Gordon
 */
public class LinkedQueue<T> implements Queue<T> {
    private Node<T> head;
    private Node<T> tail;

    /**
     * Add a new element to the head of the queue.
     */
    @Override
    public void add(T data) {
        if(head == null) {
            head = new Node<>(data, null, null);
            tail = head;
        } else {
            Node<T> oldHead = head;
            head = new Node<>(data, null, oldHead);
            oldHead.prev = head;
        }
    }

    /**
     * Retrieves and removes the tail of this queue.
     *
     * @return The element at the tail of this queue, or null if this queue is empty
     */
    @Override
    public T remove() {
        if(tail == null) {
            return null;
        }

        T ret = tail.data;

        if(head == tail) {
            // Special case when there's only one element.
            head = tail = null;
        } else {
            tail = tail.prev;
        }

        return ret;
    }

    private static class Node<T> {
        public T data;
        public Node<T> prev;
        public Node<T> next;

        public Node(T data, Node<T> prev, Node<T> next) {
            Node.this.data = data;
            Node.this.prev = prev;
            Node.this.next = next;
        }
    }
}
