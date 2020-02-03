package name.brian_gordon.collections.queues;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

/**
 * Single-threaded tests against various implementations of the Queue interface.
 *
 * @author Brian Gordon
 */
@RunWith(Parameterized.class)
public class QueueTest {
    private QueueFactory<Integer> queueFactory;

    public QueueTest(QueueFactory<Integer> queueFactory) {
        this.queueFactory = queueFactory;
    }

    @Test
    public void testEmpty() {
        Queue<Integer> queue = queueFactory.makeQueue();
        Assert.assertNull(queue.remove());
    }

    @Test
    public void testFixed1() {
        Queue<Integer> queue = queueFactory.makeQueue();
        queue.add(5);
        queue.add(4);
        Assert.assertEquals(5, (Number)queue.remove());
        Assert.assertEquals(4, (Number)queue.remove());
        Assert.assertNull(queue.remove());
    }

    @Test
    public void testFixed2() {
        Queue<Integer> queue = queueFactory.makeQueue();
        queue.add(5);
        queue.add(4);
        Assert.assertEquals(5, (Number)queue.remove());
        Assert.assertEquals(4, (Number)queue.remove());
        queue.add(3);
        queue.add(2);
        Assert.assertEquals(3, (Number)queue.remove());
        Assert.assertEquals(2, (Number)queue.remove());
        Assert.assertNull(queue.remove());
    }

    @Test
    public void testFixed3() {
        Queue<Integer> queue = queueFactory.makeQueue();
        queue.add(5);
        Assert.assertEquals(5, (Number)queue.remove());
        queue.add(4);
        queue.add(3);
        queue.add(2);
        Assert.assertEquals(4, (Number)queue.remove());
        queue.add(1);
        Assert.assertEquals(3, (Number)queue.remove());
        Assert.assertEquals(2, (Number)queue.remove());
        Assert.assertEquals(1, (Number)queue.remove());
        Assert.assertNull(queue.remove());
    }

    @Test
    public void testFixed4() {
        Queue<Integer> queue = queueFactory.makeQueue();
        queue.add(5);
        queue.add(4);
        queue.add(3);
        Assert.assertEquals(5, (Number)queue.remove());
        queue.add(2);
        queue.add(1);
        Assert.assertEquals(4, (Number)queue.remove());
        Assert.assertEquals(3, (Number)queue.remove());
        Assert.assertEquals(2, (Number)queue.remove());
        Assert.assertEquals(1, (Number)queue.remove());
        Assert.assertNull(queue.remove());
    }

    /**
     * Apply randomized add/remove operations to the queues being tested and a known correct queue implementation,
     * simultaneously. Then compare their outputs.
     */
    @Test
    public void testRandom() {
        Random random = new Random();

        Queue<Integer> myQueue = queueFactory.makeQueue();
        java.util.Queue<Integer> correctQueue = new ArrayDeque<>();
        for(int i=0; i<1000; i++) {
            if(random.nextBoolean()) {
                myQueue.add(i);
                correctQueue.add(i);
            } else {
                Assert.assertEquals(myQueue.remove(), correctQueue.poll());
            }
        }
    }

    // We need to parameterize the JUnit test on factories rather than on actual instances, because the Parameterized
    // runner reuses the same instance for every test!
    @Parameterized.Parameters
    public static List<QueueFactory<Integer>[]> factories() {
        return List.of(
                new QueueFactory[] {() -> new LinkedQueue<Integer>()},
                new QueueFactory[] {() -> new ArrayQueue<Integer>()},
                new QueueFactory[] {() -> new ArrayQueue<Integer>(3)},
                new QueueFactory[] {() -> new ArrayQueue<Integer>(5)},
                new QueueFactory[] {() -> new SynchronizedArrayQueue<Integer>(3)},
                new QueueFactory[] {() -> new SynchronizedArrayQueue<Integer>(10)}
        );
    }
}