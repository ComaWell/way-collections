package art.comacreates.characteristics.compound;

import art.comacreates.characteristics.Counted;
import art.comacreates.characteristics.Sized;

public interface Fixed extends Sized, Counted {

	public default boolean isFull() {
		return count() == size();
	}
	
}
