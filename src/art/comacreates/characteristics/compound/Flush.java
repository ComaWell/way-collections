package art.comacreates.characteristics.compound;

import java.util.function.IntFunction;

import art.comacreates.characteristics.*;

public interface Flush<T> extends Mutable, Get<T>, Minus<T> {

	@Override
	public Flush<T> minusAll();
	
	public default Object[] flush() {
		Object[] dump = dump();
		minusAll();
		return dump;
	}
	
	public default T[] flush(T[] array) {
		T[] dump = dump(array);
		minusAll();
		return dump;
	}
	
	public default T[] flush(IntFunction<T[]> generator) {
		return flush(generator.apply(0));
	}
	
}
