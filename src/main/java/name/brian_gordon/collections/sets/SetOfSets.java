package name.brian_gordon.collections.sets;

public interface SetOfSets<T> {
	/**
	 * Add a new set containing a single item.
	 *
	 * @throws IllegalStateException If the item already exists in the collection.
	 */
	public void add(T item);

	/**
	 * Join the set containing item1 and the set containing item2, so that they become a single set.
	 *
	 * @throws IllegalStateException If one or both of the given items don't exist in the collection.
	 */
	public void union(T item1, T item2);

	/**
	 * Check whether the set containing item1 is the same as the set containing item2.
	 *
	 * @throws IllegalStateException If one or both of the given items don't exist in the collection.
	 */
	public boolean isCommonSet(T item1, T item2);
}
