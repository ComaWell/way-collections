package art.comacreates.characteristics;

import art.comacreates.characteristics.Keyed.Entry;

public interface Minus<T> extends Write {
	
	public Minus<T> minusAll();
	
	public static interface ByLong<T> extends Minus<T>, Ordered {
		
		@Override
		public Minus.ByLong<T> minusAll();
		
		public Minus.ByLong<T> minus(long key);
		
		public Minus.ByLong<T> minus(long key, T value);
		
	}
	
	public static interface ByKey<K, V> extends Minus<Entry<K, V>>, Keyed<K> {
		
		@Override
		public Minus.ByKey<K, V> minusAll();
		
		public Minus.ByKey<K, V> minus(Object key);
		
		public Minus.ByKey<K, V> minus(Object key, V value);
		
	}

	public static interface ByValue<T> extends Minus<T> {
		
		@Override
		public Minus.ByValue<T> minusAll();
		
		public Minus.ByValue<T> minus(Object value);
		
	}
	
	public static interface First<T> extends Minus<T>, Ordered {

		@Override
		public Minus.First<T> minusAll();
		
		public Minus.First<T> minusFirst();
		
	}
	
	public static interface Last<T> extends Minus<T>, Ordered {
		
		@Override
		public Minus.Last<T> minusAll();
		
		public Minus.Last<T> minusLast();
		
	}

}
