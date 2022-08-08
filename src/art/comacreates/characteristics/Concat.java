package art.comacreates.characteristics;

public interface Concat<T> extends Read {
	
	public Concat<T> concat(Concat<? extends T> input);

}
