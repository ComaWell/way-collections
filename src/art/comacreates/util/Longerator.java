package art.comacreates.util;

import java.util.*;

public class Longerator implements PrimitiveIterator.OfLong {
	
	private final long end;
	
	private long current;
	
	public Longerator(long start, long length) {
		this.end = start + length;
		this.current = start;
	}

	@Override
	public boolean hasNext() {
		return current <= end;
	}
	
	@Override
	public long nextLong() {
		if (!hasNext())
			throw new NoSuchElementException();
		return current++;
	}

}
