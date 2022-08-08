package art.comacreates.characteristics;

public interface Immutable extends ThreadSafe {
	
	public static final Immutable INSTANCE = new Immutable() { };

}
