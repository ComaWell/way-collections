package art.comacreates.characteristics;

public interface Synchronized extends ThreadSafe {
	
	public static final Synchronized INSTANCE = new Synchronized() { };

}
