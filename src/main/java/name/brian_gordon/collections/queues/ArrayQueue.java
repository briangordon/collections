package name.brian_gordon.collections.queues;

/**
 * A simple queue implementation based on an array. Basically the same idea as SynchronizedArrayQueue, written 5 1/2
 * years later. It's not thread-safe, and it doesn't attempt to do anything fancy with System.arraycopy().
 *
 * @author Brian Gordon
 */
public class ArrayQueue<T> implements Queue<T> {
	private static final int DFEFAULT_INITIAL_CAPACITY = 10;

	// We can't use T[] - see Effective Java, 3rd ed., item 29.
	private Object[] ary;
	private int capacity;
	private int tailIndex = 0;
	private int headIndex = 0;

	public ArrayQueue() {
		this(ArrayQueue.DFEFAULT_INITIAL_CAPACITY);
	}

	public ArrayQueue(int initialCapacity) {
		ary = new Object[initialCapacity];
		capacity = initialCapacity;
	}

	@Override
	public void add(T data) {
		// If we're about to write into occupied space, expand and unwrap the array.
		if ((headIndex + 1) % capacity == tailIndex) {
			int newCapacity = capacity * 2;
			Object[] newAry = new Object[newCapacity];

			int newIndex = 0;
			for (int i = tailIndex; i != headIndex; i = (i + 1) % capacity) {
				newAry[newIndex++] = ary[i];
			}

			ary = newAry;
			capacity = newCapacity;
			tailIndex = 0;
			headIndex = newIndex;
		}

		ary[headIndex] = data;
		headIndex = (headIndex + 1) % capacity;
	}

	@Override
	@SuppressWarnings("unchecked")
	public T remove() {
		if (headIndex == tailIndex) {
			return null;
		}

		T ret = (T)ary[tailIndex];
		tailIndex = (tailIndex + 1) % capacity;

		return ret;
	}
}
