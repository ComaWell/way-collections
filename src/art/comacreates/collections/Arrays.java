package art.comacreates.collections;

import art.comacreates.characteristics.*;

public final class Arrays {
	
	public static interface Structure extends Mutable, Sized, Indexed, Nullable { }
	
	public static interface Read<T> extends Structure, Get.ByIndex<T>, Clone<T> {
		
		@Override
		public Read<T> clone();
		
	}
	
	public static interface Write<T> extends Structure, Put.ByLong<T> {
		
		@Override
		public Write<T> put(long index, T value);
		
	}
	
	public static interface ReadWrite<T> extends Read<T>, Write<T> {
		
		@Override
		public ReadWrite<T> clone();
		
		@Override
		public ReadWrite<T> put(long index, T value);
		
	}

}
