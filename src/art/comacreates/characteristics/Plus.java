package art.comacreates.characteristics;

import art.comacreates.characteristics.Keyed.Entry;

public interface Plus<T> extends Write {
	
	public static interface ByLong<T> extends Plus<T>, Ordered {
		
		public Plus.ByLong<T> plus(long index, T value);
		
	}
	
	//TODO: Should Plus's type be Entry<K, V> or just V?
	public static interface ByKey<K, V> extends Plus<Entry<K, V>>, Keyed<K> {
		
		public Plus.ByKey<K, V> plus(K key, V value);
		
	}
	
	public static interface First<T> extends Plus<T>{
		
		public Plus.First<T> plusFirst(T value);
		
	}
	
	public static interface Last<T> extends Plus<T> {
		
		public Plus.Last<T> plusLast(T value);
		
	}
	
	public static interface Any<T> extends Plus<T> {
		
		public Plus.Any<T> plus(T value);
		
	}

}
