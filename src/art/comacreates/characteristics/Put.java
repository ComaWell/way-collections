package art.comacreates.characteristics;

import art.comacreates.characteristics.Keyed.Entry;

public interface Put<T> extends Write {
	
	public static interface ByLong<T> extends Put<T>, Ordered {
		
		public Put.ByLong<T> put(long index, T value);
		
	}
	
	public static interface ByKey<K, V> extends Put<Entry<K, V>>, Keyed<K> {
		
		public Put.ByKey<K, V> put(K key, V value);
		
	}

}
