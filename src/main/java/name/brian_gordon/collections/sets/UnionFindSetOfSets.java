package name.brian_gordon.collections.sets;

import name.brian_gordon.collections.tuples.Tuple2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A data structure that tracks multiple sets and can efficiently join and search them.
 *
 * There is a forest of trees, and each tree represents a disjoint set of items. To union two sets we attach the root
 * of one tree to the root of the other tree. To determine whether two items belong to the same set, we check whether
 * they share the same root node.
 *
 * This class depends on the stability of passed-in objects' equality and hash codes, so be careful not to mutate any
 * inserted objects. It would be safer, in fact, to only use this with immutable classes. This class is not thread-safe.
 */
public class UnionFindSetOfSets<T> implements SetOfSets<T> {
	private static class UnionFindNode<U> {
		public U data;
		public Optional<UnionFindNode<U>> parent = Optional.empty();
		public int descendantsCount = 1;

		public UnionFindNode(U data) {
			UnionFindNode.this.data = data;
		}
	}

	private Map<T, UnionFindNode<T>> nodeLookup = new HashMap<>();

	@Override
	public void add(T item) {
		if (item == null) {
			throw new IllegalArgumentException("Can't insert null.");
		}

		if (nodeLookup.containsKey(item)) {
			throw new IllegalStateException("This collection already contains " + item);
		}

		nodeLookup.put(item, new UnionFindNode<>(item));
	}

	@Override
	public void union(T item1, T item2) {
		var roots = getRoots(item1, item2);
		UnionFindNode<T> item1Root = roots.get1();
		UnionFindNode<T> item2Root = roots.get2();

		// If the two items are already in the same set, there's nothing to do.
		if (item1Root == item2Root) {
			return;
		}

		// Attach the root of the smaller tree to the root of the larger tree. This prevents repeated unions between
		// single nodes from accumulating into a long, expensive-to-traverse chain.

		if (item1Root.descendantsCount >= item2Root.descendantsCount) {
			item2Root.parent = Optional.of(item1Root);
			item1Root.descendantsCount += item2Root.descendantsCount;
		} else {
			item1Root.parent = Optional.of(item2Root);
			item2Root.descendantsCount += item1Root.descendantsCount;
		}
	}

	@Override
	public boolean isCommonSet(T item1, T item2) {
		var roots = getRoots(item1, item2);

		return roots.get1() == roots.get2();
	}

	/**
	 * Factory method which takes items and places each one in its own independent set.
	 */
	public static <U> UnionFindSetOfSets<U> of(Iterable<U> items) {
		var ret = new UnionFindSetOfSets<U>();

		for (var item : items) {
			ret.add(item);
		}

		return ret;
	}

	private Tuple2<UnionFindNode<T>, UnionFindNode<T>> getRoots(T item1, T item2) {
		UnionFindNode<T> item1Node = nodeLookup.get(item1);
		UnionFindNode<T> item2Node = nodeLookup.get(item2);

		if (item1Node == null && item2Node == null) {
			throw new IllegalStateException(item1 + " and " + item2 + " don't exist in the collection.");
		}
		if (item1Node == null) {
			throw new IllegalStateException(item1 + " doesn't exist in the collection.");
		}
		if (item2Node == null) {
			throw new IllegalStateException(item2 + " doesn't exist in the collection.");
		}

		UnionFindNode<T> item1Root = item1Node;
		while (item1Root.parent.isPresent()) {
			item1Root = item1Root.parent.get();
		}

		UnionFindNode<T> item2Root = item2Node;
		while (item2Root.parent.isPresent()) {
			item2Root = item2Root.parent.get();
		}

		// Perform path compression. This will invalidate the descendantsCount for all intermediate nodes, but it
		// doesn't matter because once a node is no longer a root we never care about its descendantsCount again.

		if (item1Node != item1Root) {
			item1Node.parent = Optional.of(item1Root);
		}

		if (item2Node != item2Root) {
			item2Node.parent = Optional.of(item2Root);
		}

		return Tuple2.of(item1Root, item2Root);
	}
}