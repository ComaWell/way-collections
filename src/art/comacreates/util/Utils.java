package art.comacreates.util;

import java.util.*;

public class Utils {
	
	public static final Object[] EMPTY_ARRAY = new Object[0];
	
	public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
	
	public static final char[] EMPTY_CHAR_ARRAY = new char[0];
	
	public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	
	public static final short[] EMPTY_SHORT_ARRAY = new short[0];
	
	public static final int[] EMPTY_INT_ARRAY = new int[0];
	
	public static final long[] EMPTY_LONG_ARRAY = new long[0];
	
	public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
	
	public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
	
	public static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;//TODO: 8 was pulled out of my ass
	
	public static String iterableToString(Iterable<?> iterable) {
		if (iterable == null)
			throw new NullPointerException();
		StringJoiner sj = new StringJoiner(", ", "[", "]");
		for (Object val : iterable)
			sj.add(Objects.toString(val));
		return sj.toString();
	}
	
	public static int iterableHashCode(Iterable<?> iterable) {
		if (iterable == null)
			throw new NullPointerException();
		int hash = 1;
		for (Object val : iterable)
			hash = 31 * hash + (val == null ? 0 : val.hashCode());
		return hash;
	}

}
