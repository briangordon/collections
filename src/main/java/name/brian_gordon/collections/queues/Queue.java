package name.brian_gordon.collections.queues;

/**
 * @author Brian Gordon
 */
public interface Queue<T> {
    /**
     * Add a new element to the head of the queue.
     */
    void add(T data);

    /**
     * Retrieves and removes the tail of this queue.
     *
     * @return The element at the tail of this queue, or null if this queue is empty
     */
    T remove();
}
