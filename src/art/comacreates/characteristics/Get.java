package art.comacreates.characteristics;

import java.util.Optional;
import java.util.function.IntFunction;

public interface Get<T> extends Read, Iterable<T> {
	
	public default boolean contains(Object value) {
		if (value == null)
			throw new NullPointerException();
		for (Object obj : this)
			if (obj.equals(value)) return true;
		return false;
	}
	
	public Object[] dump();
	
	public T[] dump(T[] array);
	
	public default T[] dump(IntFunction<T[]> generator) {
		if (generator == null)
			throw new NullPointerException();
		return dump(generator.apply(0));
	}
	
	public static interface ByIndex<T> extends Get<T>, Indexed {
		
		public T get(long index) throws IndexOutOfBoundsException;
		
		public default long minIndex() {
			return 0;
		}
		
		public default long maxIndex() {
			return Long.MAX_VALUE;
		}
		
		public default boolean isValidIndex(long index) {
			return index >= minIndex() && index <= maxIndex();
		}

	}
	
	public static interface ByLong<T> extends Get<T>, Ordered {
		
		public Optional<T> get(long key);
		
		public default T get(long key, T ifAbsent) {
			return get(key).orElse(ifAbsent);
		}
		
		public default T getNullable(long key) {
			return get(key).orElse(null);
		}
		
	}
	
	public static interface ByEnumerated<K, V> extends Get<V>, Keyed<K>, Ordered, Sized {
		
		public V get(K key);
		
		public default V get(K key, V ifAbsent) {
			V val = get(key);
			return val == null ? ifAbsent : val;
		}
		
		public <O extends Get<K> & Distinct & Ordered & Sized> O keys();
		
		public <O extends Get<Entry<K, V>> & Distinct & Ordered & Sized> O entries();
		
	}
	
	public static interface ByKey<K, V> extends Get<V>, Keyed<K> {
		
		public Optional<V> get(K key);
		
		public default V get(K key, V ifAbsent) {
			return get(key).orElse(ifAbsent);
		}
		
		public default V getOrNull(K key) {
			return get(key).orElse(null);
		}
		
		public default boolean containsKey(Object key) {
			if (key == null)
				throw new NullPointerException();
			for (Object obj : keys())
				if (obj.equals(key)) return true;
			return false;
		}
		
		public <O extends Get<K> & Distinct> O keys();
		
		public <O extends Get<Entry<K, V>> & Distinct> O entries();
		
	}
	
	public static interface First<T> extends Get<T>, Ordered {
		
		public T first();

	}
	
	public static interface Last<T> extends Get<T>, Ordered {

		public T last();
		
	}
	
	public static interface Any<T> extends Get<T> {

		public T get();
		
	}

}
