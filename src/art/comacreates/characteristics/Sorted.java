package art.comacreates.characteristics;

import java.util.Comparator;

public interface Sorted<T> extends Ordered {
	
	public Comparator<T> comparator();
	
}
