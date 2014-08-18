package name.brian_gordon.collections.queues.factories;

import name.brian_gordon.collections.queues.LinkedQueue;
import name.brian_gordon.collections.queues.Queue;

public class LinkedQueueFactory<T> implements QueueFactory<T> {
    @Override
    public Queue<T> makeQueue() {
        return new LinkedQueue<>();
    }
}
