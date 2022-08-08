package art.comacreates.characteristics;

public interface Clone<T> extends Get<T>, Cloneable {
	
	public Clone<T> clone();

}
