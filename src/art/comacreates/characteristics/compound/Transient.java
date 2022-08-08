package art.comacreates.characteristics.compound;

import java.util.function.*;

import art.comacreates.characteristics.*;

public interface Transient extends Immutable, Cloneable {
	
	public Transient clone();
	
	Transient clone(boolean mutable);
	
	public boolean isMutable();
	
	default Transient toMutable() {
		return isMutable() ? this : clone();
	}
	
	default Transient toImmutable() {
		return isMutable() ? clone() : this;
	}
	
	public default Transient mutate(UnaryOperator<Transient> mutator) {
		return mutator.apply(toMutable()).toImmutable();
	}
	
	

}
