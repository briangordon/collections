package name.brian_gordon.playground;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class BiggestSpenders {
	/**
	 * Get the n customers which spent the most total cash across all their transactions.
	 *
	 * @return Up to n customer IDs, in descending order by spending amount. If there are ties, the customers with a
	 *         lower customer id will come first. In the case of ties, there may be customers who aren't included in
	 *         the big spenders list, but who actually spent the same amount as the customer in the bottom position of
	 *         the big spenders list. If there are fewer than n distinct customer IDs in the given transactions, this
	 *         will return all distinct customer IDs.
	 */
	public static List<Long> getBigSpenders(List<Transaction> transactions, int n) {
		// Calculate the total spending for each distinct customer.
		Map<Long, BigDecimal> spendingByUser = transactions.stream().collect(
				Collectors.groupingBy(
						Transaction::getCustomerId,
						Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)
				)
		);

		// Get the top n spenders using a heap. We're sorting by spending ascending, then by user ID descending - the
		// inverse of what we really want - because PriorityQueue keeps the *smallest* element at the head.
		Comparator<Map.Entry<Long, BigDecimal>> userSpendingComparator =
				Map.Entry.<Long, BigDecimal>comparingByValue().thenComparing(
						Map.Entry.<Long, BigDecimal>comparingByKey().reversed()
				);
		PriorityQueue<Map.Entry<Long, BigDecimal>> heap = new PriorityQueue<>(n, userSpendingComparator);
		spendingByUser.entrySet().forEach(entry -> {
			if (heap.size() < n) {
				heap.offer(entry);
			} else if (userSpendingComparator.compare(entry, heap.peek()) > 0) {
				heap.poll();
				heap.offer(entry);
			}
		});

		// Remove all elements from the heap and reverse the order.
		List<Long> bigSpenders = new ArrayList<>(heap.size());
		while (!heap.isEmpty()) {
			bigSpenders.add(heap.poll().getKey());
		}
		Collections.reverse(bigSpenders);
		return bigSpenders;
	}

	public static class Transaction {
		public final long customerId;
		public final BigDecimal amount;

		public Transaction(long customerId, BigDecimal amount) {
			this.customerId = customerId;
			this.amount = amount;
		}

		public long getCustomerId() {
			return customerId;
		}

		public BigDecimal getAmount() {
			return amount;
		}

		@Override
		public int hashCode() {
			return Objects.hash(customerId, amount);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}

			if (!(o instanceof Transaction)) {
				return false;
			}

			Transaction other = (Transaction)o;
			return customerId == other.customerId && Objects.equals(amount, other.amount);
		}
	}
}
