package name.brian_gordon.collections.queues.factories;

import name.brian_gordon.collections.queues.Queue;
import name.brian_gordon.collections.queues.SynchronizedArrayQueue;

public class SynchronizedArrayQueueFactory<T> implements QueueFactory<T> {
    private int startingSize;

    public SynchronizedArrayQueueFactory(int startingSize) {
        SynchronizedArrayQueueFactory.this.startingSize = startingSize;
    }

    @Override
    public Queue<T> makeQueue() {
        return new SynchronizedArrayQueue<>(startingSize);
    }
}