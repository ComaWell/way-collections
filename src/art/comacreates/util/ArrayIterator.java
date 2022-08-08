package art.comacreates.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T> {
	
	private final Object[] array;
	private final int end;
	
	private int pointer;
	
	public ArrayIterator(Object[] array, int startInclusive, int endExclusive) {
		this.array = array;
		this.end = endExclusive;
		this.pointer = startInclusive;
	}

	@Override
	public boolean hasNext() {
		return pointer < end;
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException();
		return (T) array[pointer++];
	}
	
	public static class Reversed<T> implements Iterator<T> {
		
		private final Object[] array;
		private final int end;
		
		private int pointer;
		
		public Reversed(Object[] array, int startInclusive, int endExclusive) {
			this.array = array;
			this.end = endExclusive;
			this.pointer = startInclusive;
		}

		@Override
		public boolean hasNext() {
			return pointer > end;
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return (T) array[pointer--];
		}
		
	}

}
