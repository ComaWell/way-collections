package art.comacreates.characteristics;

public interface Keyed<K> extends Distinct {
	
	public static record Entry<K, V>(K key, V value) { }
	
	public static final Indexed INSTANCE = new Indexed() { };

}
