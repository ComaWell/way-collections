package art.comacreates.collections;

import art.comacreates.characteristics.compound.Pop;

public interface Stack<T> extends Stacks.ReadWrite<T> {
	
	@Override
	public Stack<T> clone();
	
	@Override
	public Stack<T> plusLast(T value);
	
	@Override
	public Stack<T> minusAll();
	
	@Override
	public Stack<T> minusLast();
	
	public Pop.Result<T, ? extends Stack<T>> popLast();

}
