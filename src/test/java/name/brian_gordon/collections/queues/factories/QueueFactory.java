package name.brian_gordon.collections.queues.factories;

import name.brian_gordon.collections.queues.Queue;

public interface QueueFactory<T> {
    public Queue<T> makeQueue();
}