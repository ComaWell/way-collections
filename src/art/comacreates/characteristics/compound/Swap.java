package art.comacreates.characteristics.compound;

import art.comacreates.characteristics.*;
import art.comacreates.characteristics.Keyed.Entry;

public interface Swap<T> extends Read, Put<T> {
	
	@SuppressWarnings("rawtypes")
	public static record Result<T, O extends Swap>(T swapped, O output) { }
	
	public static interface ByLong<T> extends Swap<T>, Get.ByIndex<T>, Put.ByLong<T> {
		
		public Swap.Result<T, ? extends Swap.ByLong<T>> swap(long index, T value);
		
		@Override
		public Swap.ByLong<T> put(long index, T value);
		
	}
	
	public static interface ByKey<K, V> extends Swap<Entry<K, V>>, Get.ByKey<K, V>, Put.ByKey<K, V> {
		
		public Swap.Result<V, ? extends Swap.ByKey<K, V>> swap(K key, V value);
		
		@Override
		public Swap.ByKey<K, V> put(K key, V value);
		
	}

}
