package art.comacreates.characteristics;

public interface Slice extends Read {
	
	public static interface ByIndex<T> extends Slice, Get.ByIndex<T> {
		
		public Slice.ByIndex<T> slice(long startInclusive, long endExclusive);
		
	}
	
	public static interface ByEnumerated<K, V> extends Slice, Get.ByEnumerated<K, V> {
		
		public Slice.ByEnumerated<K, V> slice(K startInclusive, K endExclusive);
		
	}

}
