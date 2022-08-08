package art.comacreates.collections;

import art.comacreates.characteristics.*;
import art.comacreates.characteristics.compound.Flush;

public final class Buffers {
	
	public static interface Structure extends Mutable, Counted, Ordered, Nonnull { }
	
	public static interface Read<T> extends Structure, Clone<T> {
		
		@Override
		public Read<T> clone();
		
	}
	
	public static interface Write<T> extends Structure, Plus.Any<T>, Minus<T> {
		
		@Override
		public Write<T> plus(T value);
		
		@Override
		public Write<T> minusAll();
		
	}
	
	public static interface ReadWrite<T> extends Read<T>, Write<T>, Flush<T> {
		
		@Override
		public Read<T> clone();
		
		@Override
		public ReadWrite<T> plus(T value);
		
		@Override
		public ReadWrite<T> minusAll();
		
	}

}
