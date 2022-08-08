package art.comacreates.characteristics;

public interface ThreadSafe {
	
	public static final ThreadSafe INSTANCE = new ThreadSafe() { };

}
