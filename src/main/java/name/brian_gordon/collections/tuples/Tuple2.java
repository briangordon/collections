package name.brian_gordon.collections.tuples;

import java.util.Objects;

public class Tuple2<T1, T2> {
	private final T1 field1;
	private final T2 field2;

	private Tuple2(T1 first, T2 value) {
		this.field1 = first;
		this.field2 = value;
	}

	public T1 get1() {
		return field1;
	}

	public T2 get2() {
		return field2;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Tuple2)) {
			return false;
		}
		Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;
		return Objects.equals(field1, tuple2.field1) &&
				Objects.equals(field2, tuple2.field2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(field1, field2);
	}

	public static <T1, T2> Tuple2<T1, T2> of(T1 field1, T2 field2) {
		return new Tuple2<>(field1, field2);
	}
}
