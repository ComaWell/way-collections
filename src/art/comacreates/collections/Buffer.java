package art.comacreates.collections;

public interface Buffer<T> extends Buffers.ReadWrite<T> {
	
	@Override
	public Buffer<T> clone();
	
	@Override
	public Buffer<T> plus(T value);
	
	@Override
	public Buffer<T> minusAll();

}
