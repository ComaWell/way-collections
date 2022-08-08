package art.comacreates.collections;

import art.comacreates.characteristics.*;
import art.comacreates.characteristics.compound.Pop;

public final class Stacks {
	
	public static interface Structure extends Ordered, Nullable, Counted { }
	
	public static interface Read<T> extends Structure, Get.Last<T>, Clone<T> {
		
		@Override
		public Read<T> clone();
		
	}
	
	public static interface Write<T> extends Structure, Plus.Last<T>, Minus.Last<T> {
		
		@Override
		public Write<T> plusLast(T value);
		
		@Override
		public Write<T> minusAll();
		
		@Override
		public Write<T> minusLast();
		
	}
	
	public static interface ReadWrite<T> extends Read<T>, Write<T>, Pop.Last<T> {
		
		@Override
		public ReadWrite<T> clone();
		
		@Override
		public ReadWrite<T> plusLast(T value);
		
		@Override
		public ReadWrite<T> minusAll();
		
		@Override
		public ReadWrite<T> minusLast();
		
		public Pop.Result<T, ? extends ReadWrite<T>> popLast();
		
	}

}
