package name.brian_gordon.collections.queues;

/**
 * A thread-safe array-backed queue which uses coarse-grained locking.
 *
 * @author Brian Gordon
 */
public class SynchronizedArrayQueue<T> implements Queue<T> {
    private Object[] ary;

    // The number of items the backing array can hold before needing to grow.
    private int capacity;

    // The number of items currently in the queue.
    private int size = 0;

    // The index where new items will be inserted.
    private int headIdx = 0;

    // The index pointing to the next element to be removed.
    private int tailIdx = 0;

    public SynchronizedArrayQueue() {
        this(10);
    }

    public SynchronizedArrayQueue(int startingCapacity) {
        if(startingCapacity <= 0) {
            throw new IllegalArgumentException("Starting capacity must be at least 1.");
        }

        ary = new Object[startingCapacity];
        capacity = startingCapacity;
    }

    /**
     * Add a new element to the head of the queue.
     */
    @Override
    public synchronized void add(T data) {
        ensure(size+1);

        ary[headIdx] = data;

        headIdx = (headIdx - 1) % capacity;
        if(headIdx < 0) {
            headIdx += capacity;
        }

        size++;
    }

    /**
     * Retrieves and removes the tail of this queue.
     *
     * @return The element at the tail of this queue, or null if this queue is empty
     */
    @Override
    public synchronized T remove() {
        if(size == 0) {
            return null;
        }

        T ret = (T)ary[tailIdx];
        tailIdx = (tailIdx - 1) % capacity;
        if(tailIdx < 0) {
            tailIdx += capacity;
        }
        size--;

        return ret;
    }

    /**
     * Gets a user-readable report about the current state of the queue.
     */
    @Override
    public synchronized String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append('[');

        for(int i=size-1; i>=0; i--) {
            int actualIndex = (headIdx + 1 + i) % capacity;
            ret.append(ary[actualIndex]);
            if(i > 0) {
                ret.append(", ");
            }
        }

        ret.append(']');

        return ret.toString();
    }

    /**
     * @see java.util.ArrayList#MAX_ARRAY_SIZE
      */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Ensure that the backing array can hold the given number of items.
     */
    private void ensure(int minSize) {
        if(minSize < 0) {
            // Overflow will happen if we're on a VM which allows us to allocate arrays of size Integer.MAX_INT and
            // the user tries to continue adding elements past that point.
            throw new IllegalStateException("This queue is full.");
        }

        if(capacity >= minSize) {
            return;
        }

        // Reserve twice as much memory as we actually need. This reduces the frequency of resize operations.
        int newCapacity = minSize * 2;

        // Check to see if newCapacity overshot the MAX_ARRAY_SIZE or overflowed Integer.MAX_VALUE.
        if(newCapacity > MAX_ARRAY_SIZE || newCapacity < 0) {
            // We *may* be able to allocate an array larger than MAX_ARRAY_SIZE, but it's not exactly recommended.
            // Therefore, we only grow past that point if the user specifically requests it. This is the approach
            // taken by java.util.ArrayList when growing.
            if(minSize > MAX_ARRAY_SIZE) {
                // Grow directly to Integer.MAX_VALUE in order to prevent continuous allocations in the interval
                // between MAX_ARRAY_SIZE and Integer.MAX_VALUE.
                newCapacity = Integer.MAX_VALUE;
            } else {
                newCapacity = MAX_ARRAY_SIZE;
            }
        }

        // Allocate the array. This may fail with java.lang.OutOfMemoryError if the user specifically tried to ensure
        // a size greater than MAX_ARRAY_SIZE and we're running on a VM which doesn't allow it.
        Object[] newAry = new Object[newCapacity];

        //      HT
        // [ 14 12 17 16 15 ]

        // Unroll the old circular array so that we can add empty space at the end.
        if(headIdx < tailIdx) {
            // The queue items are in one contiguous chunk. Just copy the chunk to the new array.
            System.arraycopy(ary, headIdx + 1 , newAry, 0, tailIdx - headIdx);
        } else if(headIdx == capacity - 1) {
            // The queue items are really in one contiguous chunk, because the head pointer is at the end of the array.
            System.arraycopy(ary, 0, newAry, 0, tailIdx + 1);
        } else {
            // The queue items wrap around the end of the circular array, so we have to copy two separate pieces.
            System.arraycopy(ary, headIdx + 1, newAry, 0, capacity - headIdx - 1);
            System.arraycopy(ary, 0, newAry, capacity - headIdx - 1, tailIdx + 1);
        }

        headIdx = newCapacity - 1;
        tailIdx = size - 1;
        ary = newAry;
        capacity = newCapacity;
    }
}
