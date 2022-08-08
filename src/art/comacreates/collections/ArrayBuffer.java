package art.comacreates.collections;

import java.util.*;

import art.comacreates.characteristics.*;
import art.comacreates.util.*;

public class ArrayBuffer<T> implements Buffer<T>, Unsized, Sequential {
	
	Object[] buffer;
	int pointer = 0;
	
	final int initSize;
	
	public ArrayBuffer(ArrayBuffer<T> buffer) {
		if (buffer == null)
			throw new NullPointerException();
		this.buffer = buffer.buffer.clone();
		this.pointer = buffer.pointer;
		this.initSize = buffer.initSize;
	}
	
	public ArrayBuffer(int initSize) throws IllegalArgumentException {
		if (initSize < 1)
			throw new IllegalArgumentException("initSize must be positive");
		this.buffer = Utils.EMPTY_ARRAY;
		this.initSize = initSize;
	}
	
	public ArrayBuffer() {
		this(8);
	}
	
	private void grow(Object toAdd, int minSize) {
		int oldLength = buffer.length;
		int newLength;
		int addIndex;
		if (oldLength == 0) {
			newLength = initSize;
			addIndex = 0;
		}
		else {
			newLength = oldLength * 2;
			while (newLength < minSize && newLength > 0)
				newLength *= 2;
			//integer overflow
			if (newLength <= 0 || newLength > Utils.MAX_ARRAY_LENGTH) {
				if (oldLength == Utils.MAX_ARRAY_LENGTH)
					throw new IllegalStateException("Exceeded max buffer size");
				else newLength = Utils.MAX_ARRAY_LENGTH;
			}
			addIndex = oldLength;
		}
		Object[] newBuffer = new Object[newLength];
		System.arraycopy(buffer, 0, newBuffer, 0, oldLength);
		newBuffer[addIndex] = toAdd;
		buffer = newBuffer;
	}
	
	@Override
	public long count() {
		return pointer;
	}
	
	@Override
	public ArrayBuffer<T> minusAll() {
		this.buffer = new Object[initSize];
		this.pointer = 0;
		return this;
	}

	@Override
	public ArrayBuffer<T> plus(T value) throws IllegalStateException {
		if (value == null)
			throw new NullPointerException();
		int index = pointer++;
		if (index >= buffer.length)
			grow(value, index + 1);
		else buffer[index] = value;
		return this;
	}

	@Override
	public Object[] dump() {
		Object[] dump = new Object[pointer];
		System.arraycopy(buffer, 0, dump, 0, dump.length);
		return dump;
	}

	@Override
	public T[] dump(T[] array) {
		if (array == null)
			throw new NullPointerException();
		int size = pointer;
		if (array.length < size)
			array = java.util.Arrays.copyOf(array, size);
		System.arraycopy(buffer, 0, array, 0, size);
		return array;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayIterator<>(buffer, 0, pointer);
	}
	
	@Override
	public ArrayBuffer<T> clone() {
		return new ArrayBuffer<>(this);
	}
	
	@Override
	public String toString() {
		return java.util.Arrays.toString(buffer);
	}
	
	@Override
	public int hashCode() {
		return 19 * Integer.hashCode(pointer) * java.util.Arrays.hashCode(buffer);
	}
	
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof ArrayBuffer<?> b)
			return pointer == b.pointer && java.util.Arrays.equals(buffer, b.buffer);
		if (!(obj instanceof Buffer<?> b))
			return false;
		if (pointer != b.count())
			return false;
		int index = 0;
		for (Object o : b)
			if (!Objects.equals(buffer[index++], o))
				return false;
		return true;
	}

}
