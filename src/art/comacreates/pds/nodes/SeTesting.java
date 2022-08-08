package art.comacreates.pds.nodes;

import static art.comacreates.pds.nodes.SeVecNode.*;

import java.util.Arrays;
import java.util.function.Supplier;

public class SeTesting {
	
	public static final class StateChange {
		
		public static final int PLUS 			= 0x0000_0001;
		public static final int MINUS_LAST 		= 0x0000_0002;
		public static final int MINUS_FIRST 	= 0x0000_0004;
		public static final int SET 			= 0x0000_0008;
		public static final int SLICE 			= 0x0000_0010;
		public static final int CONCAT 			= 0x0000_0020;
		
		public static final int WRITER_CHANGE 	= 0x0001_0000;
		public static final int ROOT_GROW 		= 0x0002_0000;
		public static final int ROOT_SHRINK 	= 0x0004_0000;
		public static final int SHRINK_TO_EMPTY = 0x0008_0000;//Transitioning from a not empty node to EMPTY
		public static final int EMPTY_TO_GROW	= 0x0010_0000;//Transitioning from EMPTY to a not empty node
		public static final int MUTATES_FIRST	= 0x0020_0000;
		public static final int MUTATES_LAST	= 0x0040_0000;
		public static final int MUTATES_INNER	= 0x0080_0000;
		public static final int NO_CHANGE		= 0x0100_0000;
		
		static final int GROWING_STATES 		= ROOT_GROW | EMPTY_TO_GROW;
		static final int SHRINKING_STATES 		= ROOT_SHRINK | SHRINK_TO_EMPTY;
		static final int MUTATING_STATES 		= WRITER_CHANGE | GROWING_STATES | SHRINKING_STATES | MUTATES_FIRST | MUTATES_LAST | MUTATES_INNER;
		
		static final int PLUS_STATES 			= PLUS | WRITER_CHANGE | GROWING_STATES | MUTATES_LAST;
		static final int MINUS_LAST_STATES 		= MINUS_LAST | WRITER_CHANGE | SHRINKING_STATES | MUTATES_LAST | NO_CHANGE;
		static final int MINUS_FIRST_STATES 	= MINUS_FIRST | WRITER_CHANGE | SHRINKING_STATES | MUTATES_FIRST | NO_CHANGE;
		static final int SET_STATES 			= SET | WRITER_CHANGE | MUTATES_INNER | NO_CHANGE;
		static final int SLICE_STATES 			= SLICE | WRITER_CHANGE | SHRINKING_STATES | NO_CHANGE; //TODO: Should this have any MUTATE states?
		static final int CONCAT_STATES 			= CONCAT | WRITER_CHANGE | GROWING_STATES | NO_CHANGE; //TODO: Should this have any MUTATE states?
		
		public static boolean isPlus(int stateChange) {
			return (stateChange & PLUS) != 0;
		}
		
		public static boolean isMinusLast(int stateChange) {
			return (stateChange & MINUS_LAST) != 0;
		}
		
		public static boolean isMinusFirst(int stateChange) {
			return (stateChange & MINUS_FIRST) != 0;
		}
		
		public static boolean isSet(int stateChange) {
			return (stateChange & SET) != 0;
		}
		
		public static boolean isSlice(int stateChange) {
			return (stateChange & SLICE) != 0;
		}
		
		public static boolean isConcat(int stateChange) {
			return (stateChange & CONCAT) != 0;
		}
		
		public static boolean isWriterChange(int stateChange) {
			return (stateChange & WRITER_CHANGE) != 0;
		}
		
		public static boolean isRootGrow(int stateChange) {
			return (stateChange & ROOT_GROW) != 0;
		}
		
		public static boolean isRootShrink(int stateChange) {
			return (stateChange & ROOT_SHRINK) != 0;
		}
		
		public static boolean isShrinkToEmpty(int stateChange) {
			return (stateChange & SHRINK_TO_EMPTY) != 0;
		}
		
		public static boolean isEmptyToGrow(int stateChange) {
			return (stateChange & EMPTY_TO_GROW) != 0;
		}
		
		public static boolean isMutatesFirst(int stateChange) {
			return (stateChange & MUTATES_FIRST) != 0;
		}
		
		public static boolean isMutatesLast(int stateChange) {
			return (stateChange & MUTATES_LAST) != 0;
		}
		
		public static boolean isNoChange(int stateChange) {
			return (stateChange & NO_CHANGE) != 0;
		}
		
		public static boolean isMutating(int stateChange) {
			return (stateChange & MUTATING_STATES) != 0;
		}
		
		
	}
	
	static void print(SeVecNode node) {
		System.out.println(node.toString());
		System.out.println("Children: " + Arrays.toString(node.children));
		System.out.println("Offsets: " + Arrays.toString(node.offsets));
		System.out.println("Tail: " + node.tail);
		System.out.println("Strict: " + node.strict);
		System.out.println("Shift: " + node.shift);
		System.out.println("Size: " + node.size());
		System.out.println("Is empty: " + node.isEmpty());
		System.out.println("Writer: " + (node.writer == null ? "null" : node.writer.getClass().getName() + "@" + Integer.toHexString(node.writer.hashCode())));
	}
	
	public static void testPlus() {
		final int iterations = 64;
		final int groupSize = MAX_CHILDREN;
		final Object writer = new Object();
		final Supplier<Object> writerSupplier = () -> writer;
		SeVecNode previousState = EMPTY;
		SeVecNode nextState = null;
		for (int i = 0; i < iterations; i++) {
			try {
				long expectedSize = previousState.size() + groupSize;
				Object previousWriter = previousState.writer;
				Object nextWriter = writerSupplier.get();
				nextState = previousState.plus(new Object[groupSize], nextWriter);
				validateNodeStructure(nextState);
				if (nextState.size() != expectedSize)
					throw new IllegalStateException("Unexpected nextState node size. Expected: " + expectedSize + ", actual: " + nextState.size());
				previousState = nextState;
			} catch(IllegalStateException e) {
				e.printStackTrace();
				System.out.println("\nFailed structure validation");
				System.out.println("\nPrevious state");
				print(previousState);
				System.out.println("\nFailed state");
				print(nextState);
				return;
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("\nFailed plusFirst operation");
				System.out.println("\nPrevious state");
				print(previousState);
				return;
			}
		}
		System.out.println("Done!");
	}
	
	public static void testMinusLast() {
		final int iterations = 100000;
		final int groupSize = MAX_CHILDREN;
		SeVecNode previousState = EMPTY;
		SeVecNode nextState = null;
		for (int i = 0; i < iterations; i++) {
			previousState = previousState.plus(new Object[groupSize], new Object());
		}
		for (int i = 0; i < iterations; i++) {
			try {
				nextState = previousState.minusLast(new Object());
				validateNodeStructure(nextState);
				if (nextState.size() != previousState.size() - groupSize)
					throw new IllegalStateException("Unexpected nextState node size. Expected: " + (previousState.size() - groupSize) + ", actual: " + nextState.size());
				previousState = nextState;
			} catch(IllegalStateException e) {
				e.printStackTrace();
				System.out.println("\nFailed structure validation");
				System.out.println("\nPrevious state");
				print(previousState);
				System.out.println("\nFailed state");
				print(nextState);
				return;
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("\nFailed plusFirst operation");
				System.out.println("\nPrevious state");
				print(previousState);
				return;
			}
		}
		System.out.println("Done!");
	}
	
	public static void testMinusFirst() {
		final int iterations = 100000;
		final int groupSize = MAX_CHILDREN;
		SeVecNode previousState = EMPTY;
		SeVecNode nextState = null;
		for (int i = 0; i < iterations; i++) {
			previousState = previousState.plus(new Object[groupSize], new Object());
		}
		for (int i = 0; i < iterations; i++) {
			try {
				nextState = previousState.minusFirst(new Object());
				validateNodeStructure(nextState);
				if (nextState.size() != previousState.size() - groupSize)
					throw new IllegalStateException("Unexpected nextState node size. Expected: " + (previousState.size() - groupSize) + ", actual: " + nextState.size());
				previousState = nextState;
			} catch(IllegalStateException e) {
				e.printStackTrace();
				System.out.println("\nFailed structure validation");
				System.out.println("\nPrevious state");
				print(previousState);
				System.out.println("\nFailed state");
				print(nextState);
				return;
			} catch(Exception e) {
				e.printStackTrace();
				System.out.println("\nFailed plusFirst operation");
				System.out.println("\nPrevious state");
				print(previousState);
				return;
			}
		}
		System.out.println("Done!");
	}
	
	static void validateNodeStructure(SeVecNode input) {
		Object[] children = input.children;
		byte shift = input.shift;
		int tail = input.tail;
		if (shift % SHIFT_INC != 0)
			throw new IllegalStateException("Invalid node shift value: " + shift);
		
		if (tail < 0) {
			for (int i = 0; i < children.length; i++) {
				Object child = children[i];
				if (child != null)
					throw new IllegalStateException("Node pointers indicate it is empty, but there is a nonnull element in index " + i + ": " + child.getClass().getCanonicalName());
			}
			//A non-leaf node should never be empty- the root node should never be larger than it needs to be
			if (shift != SHIFT_INC)
				throw new IllegalStateException("Empty node has a non-leaf shift value: " + shift);
		}
		else for (int i = 0; i < children.length; i++) {
			Object child = children[i];
			if (i > tail && child != null)
				throw new IllegalStateException("Nonnull child outside of node pointers. Tail: " + tail + ", index: " + i + ", element type: " + child.getClass().getCanonicalName());
			else if (i <= tail) {
				if (child == null)
					throw new IllegalStateException("Null child inside of node pointers. Tail: " + tail + ", index: " + i);
				else if (child == EMPTY_CHILDREN)
					throw new IllegalStateException("Non-empty node contains EMPTY_CHILDREN array in index " + i);
				if (shift == SHIFT_INC) {
					if (!(child instanceof Object[] group))
						throw new IllegalStateException("Leaf node contains non-group child: " + child.getClass().getCanonicalName());
				}
				else if (!(child instanceof SeVecNode n))
					throw new IllegalStateException("Non-leaf node contains non-node child: " + child.getClass().getCanonicalName());
				else if (n.shift != shift - SHIFT_INC)
					throw new IllegalStateException("Node child in index " + i + " has unexpected shift value. Parent shift: " + shift + ", child shift: " + n.shift);
				else validateNodeStructure(n);
			}
		}
	}

}
