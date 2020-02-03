package name.brian_gordon.playground;

import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.List;

public class BiggestSpendersTest {
	@Test
	public void testNoSpenders() {
		var topSpenders = BiggestSpenders.getBigSpenders(List.of(), 5);
		assertTrue(topSpenders.isEmpty());
	}

	@Test
	public void testSomeSpenders() {
		var transactions = List.of(
				new BiggestSpenders.Transaction(0, new BigDecimal("3.50")),
				new BiggestSpenders.Transaction(0, new BigDecimal("3.50")),
				new BiggestSpenders.Transaction(1, new BigDecimal("3.50"))
		);

		var topSpenders = BiggestSpenders.getBigSpenders(transactions, 5);
		assertEquals(2, topSpenders.size());
		assertEquals(0, topSpenders.get(0).longValue());
		assertEquals(1, topSpenders.get(1).longValue());
	}

	@Test
	public void testTiedSpenders() {
		var transactions = List.of(
				new BiggestSpenders.Transaction(1, new BigDecimal("3.50")),
				new BiggestSpenders.Transaction(0, new BigDecimal("3.50"))
		);

		var topSpenders = BiggestSpenders.getBigSpenders(transactions, 1);
		assertEquals(1, topSpenders.size());
		assertEquals(0, topSpenders.get(0).longValue());
	}

	@Test
	public void testManySpenders() {
		var transactions = List.of(
				new BiggestSpenders.Transaction(0, new BigDecimal("3.50")),
				new BiggestSpenders.Transaction(3, new BigDecimal("2.00")),
				new BiggestSpenders.Transaction(0, new BigDecimal("3.50")),
				new BiggestSpenders.Transaction(1, new BigDecimal("3.50")),
				new BiggestSpenders.Transaction(2, new BigDecimal("3.00"))
		);

		var topSpenders = BiggestSpenders.getBigSpenders(transactions, 3);
		assertEquals(3, topSpenders.size());
		assertEquals(0, topSpenders.get(0).longValue());
		assertEquals(1, topSpenders.get(1).longValue());
		assertEquals(2, topSpenders.get(2).longValue());
	}
}
