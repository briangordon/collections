package name.brian_gordon.collections.queues;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Random;

/**
 * Tests specific to SynchronizedArrayQueue.
 *
 * @author Brian Gordon
 */
public class SynchronizedArrayQueueTest {
    /**
     * Compare the queue state at every step to that of java.util.ArrayDeque.
     */
    @Test
    public void testRandom() {
        Random random = new Random(1);

        Queue<Integer> myQueue = new SynchronizedArrayQueue<>(5);
        java.util.Queue<Integer> correctQueue = new ArrayDeque<>();
        for(int i=0; i<1000; i++) {
            if(random.nextBoolean()) {
                myQueue.add(i);
                correctQueue.add(i);
            } else {
                myQueue.remove();
                correctQueue.poll();
            }

            Assert.assertEquals(myQueue.toString(), correctQueue.toString());
        }
    }
}
