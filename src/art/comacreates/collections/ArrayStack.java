package art.comacreates.collections;

import java.util.*;

import art.comacreates.characteristics.*;
import art.comacreates.characteristics.compound.*;
import art.comacreates.util.ArrayIterator;

public class ArrayStack<T> implements Stack<T>, Mutable, Fixed, Sequential {
	
	final Object[] stack;
	
	int pointer;
	
	ArrayStack(Object[] stack, int pointer) {
		this.stack = stack;
		this.pointer = pointer;
	}
	
	public ArrayStack(int size) {
		this.stack = new Object[size];
		this.pointer = -1;
	}
	
	@Override
	public long count() {
		return pointer + 1;
	}
	
	@Override
	public boolean isEmpty() {
		return pointer < 0;
	}
	
	@Override
	public long size() {
		return stack.length;
	}

	@Override
	public T last() {
		if (pointer < 0)
			throw new NoSuchElementException();
		return (T) stack[pointer];
	}

	@Override
	public Object[] dump() {
		int length = pointer + 1;
		Object[] dump = new Object[length];
		System.arraycopy(stack, 0, dump, 0, length);
		return dump;
	}

	@Override
	public T[] dump(T[] array) {
		if (array == null)
			throw new NullPointerException();
		int length = pointer + 1;
		if (array.length < length)
			array = java.util.Arrays.copyOf(array, length);
		System.arraycopy(stack, 0, array, 0, length);
		return array;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayIterator.Reversed<>(stack, pointer, -1);
	}

	@Override
	public ArrayStack<T> clone() {
		return new ArrayStack<>(stack, pointer);
	}
	
	@Override
	public ArrayStack<T> plusLast(T value) {
		if (value == null)
			throw new NullPointerException();
		if (pointer + 1 == stack.length)
			throw new IllegalStateException("Stack is full");
		stack[++pointer] = value;
		return this;
	}

	@Override
	public ArrayStack<T> minusAll() {
		while (pointer >= 0)
			stack[pointer--] = null;
		return this;
	}

	@Override
	public ArrayStack<T> minusLast() {
		if (pointer < 0)
			throw new IllegalStateException("Stack is empty");
		stack[pointer--] = null;
		return this;
	}

	@Override
	public Pop.Result<T, ? extends ArrayStack<T>> popLast() {
		if (pointer < 0)
			throw new IllegalStateException("Stack is empty");
		T popped = (T) stack[pointer];
		stack[pointer--] = null;
		return new Pop.Result<>(popped, this);
	}

}
