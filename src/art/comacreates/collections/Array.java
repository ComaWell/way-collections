package art.comacreates.collections;

import java.util.Iterator;

import art.comacreates.characteristics.Sequential;
import art.comacreates.util.*;

public class Array<T> implements Arrays.ReadWrite<T>, Sequential {
	
	@SuppressWarnings("rawtypes")
	public static final Array EMPTY = new Array(Utils.EMPTY_ARRAY);
	
	final Object[] array;
	
	Array(Object[] array) {
		this.array = array;
	}
	
	public Array(int length) {
		this.array = new Object[length];
	}

	@Override
	public long size() {
		return array.length;
	}

	@Override
	public T get(long index) {
		return (T) array[(int) index];
	}

	@Override
	public Object[] dump() {
		return array.clone();
	}

	@Override
	public T[] dump(T[] array) {
		if (array == null)
			throw new NullPointerException();
		if (array.length < this.array.length)
			array = java.util.Arrays.copyOf(array, this.array.length);
		System.arraycopy(this.array, 0, array, 0, array.length);
		return array;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayIterator<>(array, 0, array.length);
	}

	@Override
	public Array<T> put(long index, T value) {
		array[(int) index] = value;
		return this;
	}
	
	/*
	@Override
	public Array<T> slice(long startInclusive, long endExclusive) {
		if (startInclusive < 0)
			throw new IndexOutOfBoundsException(startInclusive);
		if (endExclusive < startInclusive)
			throw new IllegalArgumentException("start index cannot be higher than end index");
		int length = array.length;
		if (endExclusive > length)
			throw new IndexOutOfBoundsException(endExclusive);
		if (startInclusive == endExclusive)
			return EMPTY;
		if (endExclusive - startInclusive == 1)
			return new Array<>(new Object[] { array[(int) startInclusive] });
		if (startInclusive == 0 && endExclusive == length)
			return this;
		Object[] slice = new Object[(int) (endExclusive - startInclusive)];
		System.arraycopy(array, (int) startInclusive, slice, 0, slice.length);
		return new Array<>(slice);
	}
	
	@Override
	public Array<T> concat(Get.ByLong<? extends T> input) {
		if (input == null)
			throw new NullPointerException();
		Object[] dump = input.dump();
		int firstLength = array.length;
		int secondLength = dump.length;
		Object[] concat = new Object[firstLength + secondLength];
		System.arraycopy(array, 0, concat, 0, firstLength);
		System.arraycopy(dump, 0, concat, firstLength, secondLength);
		return new Array<>(concat);
	}
	
	public Array<T> concat(Array<? extends T> other) {
		if (other == null)
			throw new NullPointerException();
		int firstLength = array.length;
		int secondLength = other.array.length;
		Object[] concat = new Object[firstLength + secondLength];
		System.arraycopy(array, 0, concat, 0, firstLength);
		System.arraycopy(other.array, 0, concat, firstLength, secondLength);
		return new Array<>(concat);
	}
	*/
	
	@Override
	public Array<T> clone() {
		return new Array<>(array.clone());
	}
	
	@Override
	public String toString() {
		return java.util.Arrays.toString(array);
	}
	
	@Override
	public int hashCode() {
		return java.util.Arrays.hashCode(array);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof Array<?> a && java.util.Arrays.equals(a.array, array);
	}
	
	public static <T> Array<T> of(T...values) {
		if (values == null)
			throw new NullPointerException();
		return values.length == 0 ? EMPTY : new Array<>(values.clone());
	}		

}
