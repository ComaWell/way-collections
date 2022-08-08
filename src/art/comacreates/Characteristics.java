package art.comacreates;

public final class Characteristics {
	
	//--- GET ---//
	//00_00_00_00_00_00_00_XX
	//					   ^^
	
	public static final long GET						= 0x00_00_00_00_00_00_00_01L;
	public static final long GET_BY_INDEX				= 0x00_00_00_00_00_00_00_02L;
	public static final long GET_BY_LONG				= 0x00_00_00_00_00_00_00_04L;
	public static final long GET_BY_ENUMERATED			= 0x00_00_00_00_00_00_00_08L;
	public static final long GET_KEYED					= 0x00_00_00_00_00_00_00_10L;
	public static final long GET_FIRST					= 0x00_00_00_00_00_00_00_20L;
	public static final long GET_LAST					= 0x00_00_00_00_00_00_00_40L;
	public static final long GET_ANY 					= 0x00_00_00_00_00_00_00_80L;
	
	//--- PLUS ---//
	//00_00_00_00_00_00_XX_00
	//					^^
	
	public static final long PLUS						= 0x00_00_00_00_00_00_01_00L;
	public static final long PLUS_BY_LONG				= 0x00_00_00_00_00_00_02_00L;
	public static final long PLUS_BY_KEY				= 0x00_00_00_00_00_00_04_00L;
	public static final long PLUS_FIRST					= 0x00_00_00_00_00_00_08_00L;
	public static final long PLUS_LAST					= 0x00_00_00_00_00_00_10_00L;
	public static final long PLUS_ANY					= 0x00_00_00_00_00_00_20_00L;
	
	//--- MINUS ---//
	//00_00_00_00_00_XX_00_00
	//				 ^^
	
	public static final long MINUS						= 0x00_00_00_00_00_01_00_00L;
	public static final long MINUS_BY_LONG				= 0x00_00_00_00_00_02_00_00L;
	public static final long MINUS_BY_KEY				= 0x00_00_00_00_00_04_00_00L;
	public static final long MINUS_BY_VALUE				= 0x00_00_00_00_00_08_00_00L;
	public static final long MINUS_FIRST				= 0x00_00_00_00_00_10_00_00L;
	public static final long MINUS_LAST					= 0x00_00_00_00_00_20_00_00L;
	
	//--- PUT ---//
	//00_00_00_00_XX_00_00_00
	//			  ^^
	
	public static final long PUT						= 0x00_00_00_00_01_00_00_00L;
	public static final long PUT_BY_LONG				= 0x00_00_00_00_02_00_00_00L;
	public static final long PUT_BY_KEY					= 0x00_00_00_00_04_00_00_00L;
	
	//--- Mutability ---//
	//00_00_00_0X_00_00_00_00
	//		    ^
	
	public static final long MUTABLE					= 0x00_00_00_01_00_00_00_00L;
	public static final long IMMUTABLE					= 0x00_00_00_02_00_00_00_00L;
	public static final long TRANSIENT					= 0x00_00_00_04_00_00_00_00L;
	
	//--- Thread Safety ---//
	//00_00_X0_00_00_00_00_00
	//		^
	
	//Note that parallel and synchronized are mutually exclusive, despite often being interchangable. SYNCHRONIZED implies
	//that the data structure is thread-safe but still generally behaves as a single-threaded structure, whereas PARALLEL
	//implies that the structure is specifically designed to be utilized by multiple threads. One way to think of it is:
	//HashTable would fit under SYNCHRONIZED, and ConcurrentHashMap would fit under PARALLEL, but both are thread-safe.
	public static final long SEQUENTIAL					= 0x00_00_00_10_00_00_00_00L;
	public static final long SYNCHRONIZED				= 0x00_00_00_20_00_00_00_00L;
	public static final long PARALLEL					= 0x00_00_00_40_00_00_00_00L;
	public static final long THREAD_SAFE				= 0x00_00_00_80_00_00_00_00L;
	
	//--- Size ---//
	//00_XX_00_00_00_00_00_00
	//	 ^^
	
	//COUNTABLE indicates that the structure tracks the number of elements it contains.
	public static final long COUNTED					= 0x00_01_00_00_00_00_00_00L;
	//COUNTABLE_ESTIMATE indicates that the structure tracks the number of elements it contains, but it may not be exact due
	//likely to high concurrency.
	public static final long COUNTED_ESTIMATE			= 0x00_02_00_00_00_00_00_00L;
	//SIZED indicates that the structure has a fixed size. Note that the size of a structure does not necessarily reflect how
	//many elements it has allocated room for; rather it only expresses the maximum number of elements it can hold.
	public static final long SIZED						= 0x00_04_00_00_00_00_00_00L;
	public static final long UNSIZED					= 0x00_08_00_00_00_00_00_00L;
	
	//--- Structure ---//
	//XX_00_00_00_00_00_00_00
	//^^

	public static final long INDEXED					= 0x01_00_00_00_00_00_00_00L;
	public static final long KEYED						= 0x02_00_00_00_00_00_00_00L;
	public static final long HASHED						= 0x04_00_00_00_00_00_00_00L;
	public static final long SORTED						= 0x08_00_00_00_00_00_00_00L;
	public static final long ORDERED					= 0x10_00_00_00_00_00_00_00L;
	public static final long DISTINCT					= 0x20_00_00_00_00_00_00_00L;
	public static final long NULLABLE					= 0x40_00_00_00_00_00_00_00L;
	
	
	//--- Implied Characteristics ---//
	
	/* The following defines a set of associations between characteristics, such that some characteristics
	 * can be implied by the presence of others. For example, COUNTED_ESTIMATE can be implied from COUNTED,
	 * because COUNTED inherently meets the specifications of COUNTED_ESTIMATE.
	 */
	
	public static final long IMPLIED_GET_ANY			= GET_ANY | INDEXED | KEYED;
	public static final long IMPLIED_GET_FIRST			= GET_FIRST | IMPLIED_GET_ANY;
	public static final long IMPLIED_GET_LAST			= GET_LAST | IMPLIED_GET_ANY;
	public static final long IMPLIED_GET				= GET | GET_UNSPECIFIED | IMPLIED_GET_FIRST | IMPLIED_GET_LAST;
	
	public static final long IMPLIED_PLUS_ANY			= PLUS_ANY | INDEXED | KEYED;
	public static final long IMPLIED_PLUS_FIRST			= PLUS_FIRST | IMPLIED_PLUS_ANY;
	public static final long IMPLIED_PLUS_LAST			= PLUS_LAST | IMPLIED_PLUS_ANY;
	public static final long IMPLIED_PLUS				= PLUS | PLUS_UNSPECIFIED | IMPLIED_PLUS_FIRST | IMPLIED_PLUS_LAST;
	
	public static final long IMPLIED_SET_ANY			= SET_ANY | INDEXED | KEYED;
	public static final long IMPLIED_SET_FIRST			= SET_FIRST | IMPLIED_SET_ANY;
	public static final long IMPLIED_SET_LAST			= SET_LAST | IMPLIED_SET_ANY;
	public static final long IMPLIED_SET				= SET  | IMPLIED_SET_FIRST | IMPLIED_SET_LAST;
	
	public static final long IMPLIED_MINUS_ANY			= MINUS_ANY | INDEXED | KEYED;
	public static final long IMPLIED_MINUS_FIRST		= MINUS_FIRST | IMPLIED_MINUS_ANY;
	public static final long IMPLIED_MINUS_LAST			= MINUS_LAST | IMPLIED_MINUS_ANY;
	public static final long IMPLIED_MINUS				= MINUS | MINUS_UNSPECIFIED | IMPLIED_MINUS_FIRST | IMPLIED_MINUS_LAST;
	
	public static final long IMPLIED_PERSISTENT			= PERSISTENT | TRANSIENT;
	public static final long IMPLIED_IMMUTABLE			= IMMUTABLE | IMPLIED_PERSISTENT;
	
	public static final long IMPLIED_COUNTABLE_ESTIMATE	= COUNTABLE_ESTIMATE | COUNTABLE;
	
	public static final long IMPLIED_INDEXED			= INDEXED | GET_INDEXED | SET_INDEXED;
	public static final long IMPLIED_KEYED				= KEYED | HASH_BASED;
	public static final long IMPLIED_ORDERED			= ORDERED | SORTED | INDEXED | GET_FIRST | GET_LAST | PLUS_FIRST | PLUS_LAST | MINUS_FIRST | MINUS_LAST;
	
	//--- Compound Characteristics ---//
	
	/* Like implied characteristics, these can be implied by the presence of other characteristics. The key
	 * difference with these, however, is that they are only implied if ALL of their specified characteristics
	 * are present, not just one. Additionally, these characteristics may also have an additional interface
	 * corresponding to them that may need manual implementation.
	 */
	
	public static final long BLOCKING					= SIZED | SYNCHRONIZED;
	
	public static final long FIXED						= SIZED | COUNTED;
	
	//--- Mutual Exclusions ---//
	
	/* These are not actual characteristics- rather they are a list of invalid characteristic combinations.
	 * For example, a data structure cannot be both SYNCHRONIZED and PARALLEL.
	 */
	
	static final long SYNCHRONIZED_PARALLEL 			= SYNCHRONIZED | PARALLEL;
	
	//--- Common Data Structure Characteristics ---//
	


}
