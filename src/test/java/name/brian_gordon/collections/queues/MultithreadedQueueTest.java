package name.brian_gordon.collections.queues;

import name.brian_gordon.collections.queues.factories.LinkedQueueFactory;
import name.brian_gordon.collections.queues.factories.QueueFactory;
import name.brian_gordon.collections.queues.factories.SynchronizedArrayQueueFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Multithreaded tests against various thread-safe implementations of the Queue interface.
 *
 * @author Brian Gordon
 */
@RunWith(Parameterized.class)
public class MultithreadedQueueTest {
    private static final int ACTIONS_PER_ACTOR = 1000;
    private static final int NUMBER_OF_ACTORS = 5;

    private QueueFactory<Integer> queueFactory;
    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public MultithreadedQueueTest(QueueFactory<Integer> queueFactory) {
        this.queueFactory = queueFactory;
    }

    @Test
    public void testMultithreaded() throws InterruptedException {
        Queue<Integer> queue = queueFactory.makeQueue();

        // Start up enqueuing actors.
        for(int i=0; i<NUMBER_OF_ACTORS; i++) {
            EnqueuingActor actor = new EnqueuingActor(queue, i);
            executor.execute(actor);
        }

        // Start up dequeuing actors.
        List<DequeuingActor> dequeuingActors = new ArrayList<>();
        for(int i=0; i<NUMBER_OF_ACTORS; i++) {
            DequeuingActor actor = new DequeuingActor(queue);
            dequeuingActors.add(actor);
            executor.execute(actor);
        }

        // Wait for the actors to finish.
        executor.shutdown();
        if(!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            Assert.fail("Test took too much time.");
        }

        // Verify output.
        List<List<Integer>> outs = new ArrayList<>();
        for(DequeuingActor actor : dequeuingActors) {
            outs.add(actor.getLocal());
        }
        verify(outs);
    }

    /**
     * Verifies that the observed outputs of the dequeuing actors are possible given the behavior of the enqueuing actors.
     *
     * @param outs The histories of the dequeuing actors.
     */
    public void verify(List<List<Integer>> outs) {
        for(List<Integer> out : outs) {
            for(int offset=0; offset<NUMBER_OF_ACTORS; offset++) {
                int last = -1;
                for(Integer cur : out) {
                    if(cur % NUMBER_OF_ACTORS == offset) {
                        Assert.assertTrue(cur > last);
                        last = cur;
                    }
                }
            }
        }
    }

    /**
     * An actor which enqueues items.
     */
    private static class EnqueuingActor implements Runnable {
        Queue<Integer> queue;
        int offset;

        /**
         * @param offset We will use modulus to determine whether two items were enqeued by the same actor.
         *               All items enqueued by this actor will be congruent to offset (modulo NUMBER_OF_ACTORS).
         */
        public EnqueuingActor(Queue<Integer> queue, int offset) {
            EnqueuingActor.this.queue = queue;
            EnqueuingActor.this.offset = offset;
        }

        @Override
        public void run() {
            int cur = offset;
            for(int i=0; i<ACTIONS_PER_ACTOR; i++) {
                queue.add(cur);
                cur += NUMBER_OF_ACTORS;
            }
        }
    }

    /**
     * An actor which dequeues items.
     */
    private static class DequeuingActor implements Runnable {
        Queue<Integer> queue;
        List<Integer> local = new ArrayList<>();

        public DequeuingActor(Queue<Integer> queue) {
            DequeuingActor.this.queue = queue;
        }

        @Override
        public void run() {
            for(int i=0; i<ACTIONS_PER_ACTOR; i++) {
                Integer result;
                while((result = queue.remove()) == null) {}
                local.add(result);
            }
        }

        /**
         * Get the integers that this actor has dequeued, in order.
         */
        public List<Integer> getLocal() {
            return local;
        }
    }

    /**
     * Use the queue as a mutex. A thread acquires the lock when it successfully dequeues a number. It releases
     * the lock by enqueuing a number. We can tell whether the mutex works by using it to protect an unsynchronized
     * counter variable and checking whether writes to it are lost.
     */
    @Test
    public void testMultithreaded2() throws InterruptedException {
        Queue<Integer> queue = queueFactory.makeQueue();
        AtomicInteger atomicCounter = new AtomicInteger(0);
        AtomicInteger unsafeCounter = new AtomicInteger(0);

        // Start up locking actors.
        List<LockingActor> lockingActors = new ArrayList<>();
        for(int i=0; i<NUMBER_OF_ACTORS; i++) {
            LockingActor actor = new LockingActor(queue, atomicCounter, unsafeCounter);
            lockingActors.add(actor);
            executor.execute(actor);
        }
        queue.add(0);

        // Wait for all of the locking actors to finish.
        executor.shutdown();
        if(!executor.awaitTermination(1, TimeUnit.MINUTES)) {
            Assert.fail("Test took too much time.");
        }

        // Check the various counts to make sure they all match.
        Integer queueCount = queue.remove();
        Assert.assertNotNull(queueCount);
        Assert.assertEquals(atomicCounter.get(), unsafeCounter.get());
        Assert.assertEquals(atomicCounter.get(), (int)queueCount);
    }

    /**
     * An actor which attempts to use the queue as a lock in order to safely increment a counter variable.
     */
    private static class LockingActor implements Runnable {
        Queue<Integer> queue;

        // A counter which will be incremented atomically using CAS.
        AtomicInteger atomicCounter;

        // A counter which will be incremented in an unsafe way, relying on the queue for mutex.
        AtomicInteger unsafeCounter;

        public LockingActor(Queue<Integer> queue, AtomicInteger atomicCounter, AtomicInteger unsafeCounter) {
            LockingActor.this.queue = queue;
            LockingActor.this.atomicCounter = atomicCounter;
            LockingActor.this.unsafeCounter = unsafeCounter;
        }

        @Override
        public void run() {
            for(int i=0; i<ACTIONS_PER_ACTOR; i++) {
                // Acquire the lock by removing an item from the queue.
                Integer value = null;
                while ((value = queue.remove()) == null) {
                    if (Thread.interrupted()) {
                        return;
                    }
                }

                atomicCounter.incrementAndGet();

                // Read the value of counter
                int cached = unsafeCounter.get();

                // Try to make another thread take over execution. If the queue implementation is not correct, then
                // hopefully another thread will read the value of counter while we sleep, messing up the final count.
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    return;
                } finally {
                    // Set the value of counter
                    unsafeCounter.set(cached + 1);

                    // Release the lock by inserting an item back into the queue.
                    queue.add(value + 1);
                }
            }
        }
    }

    // We need to parameterize the JUnit test on factories rather than on actual instances, because the Parameterized
    // runner reuses the same instance for every test!

    @Parameterized.Parameters
    public static Collection<Object[]> factories() {
        return Arrays.asList(
                new Object[]{new SynchronizedArrayQueueFactory<Integer>(3)},
                new Object[]{new SynchronizedArrayQueueFactory<Integer>(10)}
        );
    }
}
