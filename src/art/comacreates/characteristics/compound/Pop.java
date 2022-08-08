package art.comacreates.characteristics.compound;

import art.comacreates.characteristics.*;

public interface Pop<T> extends Read, Write {
	
	@SuppressWarnings("rawtypes")
	public static record Result<T, O extends Pop>(T popped, O output) { }
	
	public static interface ByLong<T> extends Pop<T>, Get.ByLong<T>, Minus.ByLong<T> {
		
		public Result<T, ? extends Pop.ByLong<T>> pop(long key);
		
	}
	
	public static interface ByKey<K, V> extends Pop<V>, Get.ByKey<K, V>, Minus.ByKey<K, V> {
		
		public Result<V, ? extends Pop.ByKey<K, V>> pop(K key);
		
	}
	
	public static interface First<T> extends Pop<T>, Get.First<T>, Minus.First<T> {
		
		public Result<T, ? extends Pop.First<T>> popFirst();
		
	}
	
	public static interface Last<T> extends Pop<T>, Get.Last<T>, Minus.Last<T> {
		
		public Result<T, ? extends Pop.Last<T>> popLast();
		
	}

}
