package art.comacreates.characteristics;

public interface Counted extends CountedEstimate {
	
	public long count();
	
	@Override
	public default long estimateCount() {
		return count();
	}
	
	public default boolean isEmpty() {
		return count() == 0;
	}

}
