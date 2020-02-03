package name.brian_gordon.collections.queues;

import name.brian_gordon.collections.queues.Queue;

@FunctionalInterface
public interface QueueFactory<T> {
    public Queue<T> makeQueue();
}