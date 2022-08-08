package art.comacreates.collections;

import art.comacreates.characteristics.*;

public class Vectors {
	
	public static interface Structure extends Immutable, Counted, Indexed, Nonnull { }
	
	public static interface Read<T> extends Structure, Get.First<T>, Get.Last<T>, Slice.ByIndex<T>, Concat<T> {
		
		@Override
		public Read<T> slice(long startInclusive, long endExclusive);
		
		@Override
		public Read<T> concat(Concat<? extends T> input);
		
	}
	
	public static interface Write<T> extends Structure, Plus.Last<T>, Minus.Last<T>, Minus.First<T>, Put.ByLong<T> {
		
		@Override
		public Write<T> plusLast(T value);
		
		@Override
		public Write<T> minusAll();
		
		@Override
		public Write<T> minusLast();
		
		@Override
		public Write<T> minusFirst();
		
		@Override
		public Write<T> put(long index, T value);
		
	}
	
	public static interface ReadWrite<T> extends Read<T>, Write<T> {
		
		@Override
		public ReadWrite<T> slice(long startInclusive, long endExclusive);
		
		@Override
		public ReadWrite<T> concat(Concat<? extends T> input);
		
		@Override
		public ReadWrite<T> plusLast(T value);
		
		@Override
		public ReadWrite<T> minusAll();
		
		@Override
		public ReadWrite<T> minusLast();
		
		@Override
		public ReadWrite<T> minusFirst();
		
		@Override
		public ReadWrite<T> put(long index, T value);
		
	}

}
