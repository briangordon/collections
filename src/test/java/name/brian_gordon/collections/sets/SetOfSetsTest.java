package name.brian_gordon.collections.sets;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.List;

public class SetOfSetsTest {
	@Test
	public void testSingletons() {
		var strings = List.of("hello", "there");
		var sos = UnionFindSetOfSets.of(strings);
		assertFalse(sos.isCommonSet("hello", "there"));
	}

	@Test
	public void testUnion() {
		var strings = List.of("a", "b");
		var sos = UnionFindSetOfSets.of(strings);
		sos.union("a", "b");
		assertTrue(sos.isCommonSet("a", "b"));
	}

	@Test
	public void testAdd() {
		var strings = List.of("a", "b");
		var sos = UnionFindSetOfSets.of(strings);
		sos.add("c");
		assertFalse(sos.isCommonSet("a", "c"));
		sos.union("a", "c");
		assertTrue(sos.isCommonSet("a", "c"));
		assertFalse(sos.isCommonSet("a", "b"));
		sos.union("b","c");
		assertTrue(sos.isCommonSet("b", "c"));
		assertTrue(sos.isCommonSet("a", "b"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInsertNull() {
		var sos = new UnionFindSetOfSets<String>();
		sos.add(null);
	}

	@Test(expected = IllegalStateException.class)
	public void testMissingKey() {
		var strings = List.of("hello", "there");
		var sos = UnionFindSetOfSets.of(strings);
		sos.isCommonSet("general", "kenobi");
	}
}
