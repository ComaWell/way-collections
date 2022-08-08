package art.comacreates.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class NonnullArrayIterator<T> implements Iterator<T> {
	
	private final Object[] array;
	private final int length;
	
	private Object next = null;
	private int pointer;
	
	public NonnullArrayIterator(Object[] array, int start, int length) {
		this.array = array;
		this.length = length;
		this.pointer = start;
		findNext();
	}
	
	private void findNext() {
		do {
			next = array[pointer++];
		} while (next == null && pointer < length);
	}
	
	@Override
	public boolean hasNext() {
		return next != null;
	}
	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException();
		Object val = next;
		findNext();
		return (T) val;
	}

}
