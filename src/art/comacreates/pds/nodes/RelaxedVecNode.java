package art.comacreates.pds.nodes;

import java.util.Arrays;
import java.util.Objects;

//"Relaxed" in this instance means it uses a relaxed radix structure, meaning each group
//can be of any size less than or equal to MAX_CHILDREN, and any node is allowed to be incomplete.
public final class RelaxedVecNode {
	
	public static final byte SHIFT_INC = 5;
	public static final byte MAX_CHILDREN = 1 << SHIFT_INC;
	//The remaining number of empty indices a children array can have before growing (if growth is possible)
	private static final int GROW_THRESHOLD = 4;
	
	public static final int CHILD_MASK = MAX_CHILDREN - 1;
	
	private static final long[] EMPTY_OFFSETS = new long[0];
	private static final Object[] EMPTY_CHILDREN = new Object[0];
	
	public static final RelaxedVecNode EMPTY = new RelaxedVecNode(EMPTY_CHILDREN, EMPTY_OFFSETS, -1, -1, SHIFT_INC, new Object());
	
	Object[] children;
	long[] offsets;
	int head;
	int tail;
	
	boolean strict;
	
	/* You may notice that this field is typically only retrieved for the root node,
	 * and then decremented by SHIFT_INC during traversal rather than being retrieved
	 * for other nodes. This is intentional; the semantics of this node are designed
	 * such that a given node's children should only ever be nodes that are 1 SHIFT_INC
	 * lower than them (or all groups in the case of leaf nodes), so outside of debugging,
	 * there isn't a need to read this field for any others.
	 */
	final byte shift;
	final Object writer;
	
	RelaxedVecNode(Object[] children, long[] offsets, int head, int tail, byte shift, Object writer) {
		this.children = children;
		this.offsets = offsets;
		this.head = head;
		this.tail = tail;
		this.shift = shift;
		this.writer = writer;
		recalcStrict();
	}
	
	RelaxedVecNode(Object[] children, int head, int tail, byte shift, Object writer) {
		this.children = children;
		this.offsets = new long[children.length];
		this.head = head;
		this.tail = tail;
		this.shift = shift;
		this.writer = writer;
		recalcOffsets(false);
		recalcStrict();
	}
	
	public long size() {
		return isEmpty() ? 0 : offsets[tail];
	}
	
	public boolean isEmpty() {
		return head < 0;
	}
	
	public boolean isLeaf() {
		return shift == SHIFT_INC;
	}
	
	long offset(int childIndex) {
		return childIndex == head ? 0 : offsets[childIndex - 1];
	}
	
	//This isn't used much in the getters because this method accounds for both strict and relaxed nodes,
	//which we don't need in every scenario. (If a given node is strict, then all of its children/descendants
	//must also be strict, therefore we don't need to check each time.)
	int childIndex(long index) {
		int completeIndex = head + (int) (shift > 60 ? head : (index >>> shift) & CHILD_MASK);
		if (strict)
			return completeIndex;
		for (int i = completeIndex; i <= tail; i++)
			if (i < offsets[i])
				return i;
		return -1;
	}
	
	void recalcStrict() {
		this.strict = isEmpty() || size() == (tail - head + 1) * (1 << shift);
		//this.complete = !isEmpty() && size() == (lastChild + 1) * (1 << shift);
	}
	
	void recalcOffsets(boolean grown) {
		if (grown) {
			offsets = new long[children.length];
			System.out.println("Grown!");
		}
		if (shift == SHIFT_INC)
			for (int i = head; i <= tail; i++) {
				//print(this);
				offsets[i] = offset(i) + ((Object[]) children[i]).length;
				//System.out.println("i: " + i);
				//print(this);
			}
		else for (int i = head; i <= tail; i++) {
			offsets[i] = offset(i) + ((RelaxedVecNode) children[i]).size();
			//System.out.println("i: " + i);
			//print(this);
		}
	}
	
	/* TODO
	void rightShiftOffsets(int shiftAmount, long incremenentAmount) {
		for (int i = lastChild; i > 0; i--)
			offsets[i] = offsets[i - 1] + amount;
		offsets[0] = amount;
	}
	*/
	
	//--- Getters ---//
	
	public Object[] firstGroup() {
		if (head < 0)
			return null;
		RelaxedVecNode node = this;
		byte shift = this.shift;
		while (shift > SHIFT_INC) {
			node = (RelaxedVecNode) node.children[node.head];
			shift -= SHIFT_INC;
		}
		return (Object[]) node.children[node.head];
	}
	
	public Object first() {
		return firstGroup()[0];
	}
	
	public Object[] lastGroup() {
		if (tail < 0)
			return null;
		RelaxedVecNode node = this;
		byte shift = this.shift;
		while (shift > SHIFT_INC) {
			node = (RelaxedVecNode) node.children[node.tail];
			shift -= SHIFT_INC;
		}
		return (Object[]) node.children[node.tail];
	}
	
	public Object last() {
		Object[] group = lastGroup();
		return group[group.length - 1];
	}
	
	public Object[] getGroup(long index) {
		return strict ? strictGetGroup(index) : relaxedGetGroup(index);
	}
	
	public Object get(long index) {
		return strict ? strictGet(index) : relaxedGet(index);
	}
	
	Object[] strictGetGroup(long index) {
		RelaxedVecNode node = this;
		byte shift = this.shift;
		while (shift > SHIFT_INC) {
			node = (RelaxedVecNode) node.children[node.head + (int) ((index >>> SHIFT_INC) & CHILD_MASK)];
			shift -= SHIFT_INC;
		}
		return (Object[]) node.children[ node.head + (int) ((index >>> SHIFT_INC) & CHILD_MASK)];
	}
	
	Object strictGet(long index) {
		return strictGetGroup(index)[(int) index & CHILD_MASK];
	}
	
	Object[] relaxedGetGroup(long index) {
		RelaxedVecNode node = this;
		byte shift = this.shift;
		long shiftedIndex = index;
		nodeTraversal: while (node.shift > SHIFT_INC) {
			if (node.strict)
				return node.strictGetGroup(index);
			for (int i = node.head + (int) ((shiftedIndex >>> shift) & CHILD_MASK); i <= node.tail; i++) {
				if (index < node.offsets[i]) {
					node = (RelaxedVecNode) node.children[i];
					shiftedIndex -= node.offset(i);
					shift -= SHIFT_INC;
					continue nodeTraversal;
				}
			}
			//This check could be done at the start, but for now is done here so as not to waste time checking the index when
			//the data structure using this node presumably already did
			if (index < 0 || index > size())
				throw new IndexOutOfBoundsException(index);
			//If the for loop completes without manually continuing nodeTraversal, that means for whatever reason, the current Node doesn't
			//contain the index in question, therefore something is wrong with the logic. This should only get thrown if there's a bug
			throw new InternalError("Failed to get childIndex on relaxed Node. index: " + index + ", first offset: " + node.offsets[head] + ", last offset: " + node.offsets[tail]);
		}
		return (Object[]) node.children[node.childIndex(shiftedIndex)];
	}
	
	//We need to have the offset information about the leaf node we're getting the group from in order to retrieve the correct
	//element from the group, which is why this method is an inlined copy of relaxedGetGroup rather than just calling it directly
	//(like strictGet does for strictGetGroup)
	Object relaxedGet(long index) {
		RelaxedVecNode node = this;
		byte shift = this.shift;
		long shiftedIndex = index;
		nodeTraversal: while (node.shift > SHIFT_INC) {
			if (node.strict)
				return node.strictGetGroup(index);
			for (int i = node.head + (int) ((shiftedIndex >>> shift) & CHILD_MASK); i <= node.tail; i++) {
				if (index < node.offsets[i]) {
					node = (RelaxedVecNode) node.children[i];
					shiftedIndex -= node.offset(i);
					shift -= SHIFT_INC;
					continue nodeTraversal;
				}
			}
			//This check could be done at the start, but for now is done here so as not to waste time checking the index when
			//the data structure using this node presumably already did
			if (index < 0 || index > size())
				throw new IndexOutOfBoundsException(index);
			//If the for loop completes without manually continuing nodeTraversal, that means for whatever reason, the current Node doesn't
			//contain the index in question, therefore something is wrong with the logic. This should only get thrown if there's a bug
			throw new InternalError("Failed to get childIndex on relaxed Node. index: " + index + ", first offset: " + node.offsets[head] + ", last offset: " + node.offsets[tail]);
		}
		int childIndex = node.childIndex(shiftedIndex);
		return ((Object[]) node.children[childIndex])[(int) (shiftedIndex - node.offset(childIndex))];
	}
	
	//--- Setters ---//
	
	public RelaxedVecNode set(long index, Object value, Object writer) {
		//TODO
		return null;
	}
	
	//--- Plus ---//
	
	/* Nodes generally attempt to keep their children packed in the middle, so that there's
	 * room on both ends of their children arrays for elements to be added. However, the point
	 * of doing things this way is to prevent array cloning and (more importantly) rebalancing
	 * as much as possible. So it would defeat the purpose to always force elements to be in
	 * the center- therefore these methods attempt to only balance elements when it's immediately
	 * needed (i.e., there is no room left in the direction we need), while also ensuring that
	 * at least most of the array is being used effectively in scenarios where one side of the
	 * node is being heavily favored over the other.
	 */
	
	//This method has a lot of repetition and branching logic to avoid having to check
	//the same conditions multiple times. It may be worth revisiting to try to reduce the repetition
	RelaxedVecNode plusFirstChild(Object child, Object writer) {
		int oldLength = children.length;
		int newLength;
		Object[] newChildren;
		int newHead;
		int newTail;
		if (this.writer == writer) {
			//No more room, needs to be rebalanced
			if (head == 0) {
				//If the array is mostly full but isn't MAX_CHILDREN in length yet, it's time to grow
				if (oldLength < MAX_CHILDREN && oldLength - tail <= GROW_THRESHOLD) {
					newLength = Math.min(MAX_CHILDREN, oldLength << 1);
					newChildren = new Object[newLength];
				}
				else {
					newLength = oldLength;
					newChildren = children;
				}
				//Place the head in an index that leaves ~half of the empty space in the new array before it
				newHead = (newLength - tail) >> 1;
				newTail = (newHead + tail - head) + 1;
			}
			else {
				newLength = oldLength;
				newChildren = children;
				newHead = head - 1;
				newTail = tail;
			}
			System.arraycopy(children, head, newChildren, newHead + 1, (tail - head) + 1);
			newChildren[newHead] = child;
			children = newChildren;
			head = newHead;
			tail = newTail;
			recalcOffsets(newLength != oldLength);
			recalcStrict();
			return this;
		}
		//No more room, needs to be rebalanced
		if (head == 0) {
			//If the array is mostly full but isn't MAX_CHILDREN in length yet, it's time to grow
			if (oldLength < MAX_CHILDREN && oldLength - tail <= GROW_THRESHOLD)
				newLength = Math.min(MAX_CHILDREN, oldLength << 1);
			else newLength = oldLength;
			//Place the head in an index that leaves ~half of the empty space in the new array before it
			newHead = (newLength - tail) >> 1;
			newTail = (newHead + tail - head) + 1;
		}
		else {
			newLength = oldLength;
			newHead = head - 1;
			newTail = tail;
		}
		newChildren = new Object[newLength];
		System.arraycopy(children, head, newChildren, newHead + 1, (tail - head) + 1);
		newChildren[newHead] = child;
		return new RelaxedVecNode(newChildren, newHead, newTail, shift, writer);
	}
	
	public RelaxedVecNode plusFirst(Object[] group, Object writer) {
		if (head < 0)
			return singleGroup(group, writer);
		if (shift == SHIFT_INC)
			return head == 0 && tail == CHILD_MASK
				? new RelaxedVecNode(new Object[] { null, singleGroup(group, writer), this, null }, new long[] { 0, group.length, group.length + size(), 0 }, 1, 2, (byte) (SHIFT_INC << 1), writer)
				: plusFirstChild(group, writer);
		RelaxedVecNode[] lineage = new RelaxedVecNode[shift / SHIFT_INC];
		lineage[0] = this;
		for (int i = 1; i < lineage.length; i++) {
			RelaxedVecNode parent = lineage[i - 1];
			lineage[i] = (RelaxedVecNode) parent.children[parent.head];
		}
		int currentIndex = lineage.length - 1;
		RelaxedVecNode current = lineage[currentIndex--];
		//If there is room in the leaf of this lineage: push the group into the leaf
		if (current.head > 0 || current.tail < CHILD_MASK) {
			current = current.plusFirstChild(group, writer);
		}
		//If there is not room: create a new leaf, scale it until we find a parent that is not
		//full, and then merge them together (or create a new root if the current root is full)
		else {
			RelaxedVecNode overflow = singleGroup(group, writer);
			while (currentIndex >= 0) {
				current = lineage[currentIndex];
				if (current.head == 0 && current.tail == CHILD_MASK) {
					overflow = new RelaxedVecNode(new Object[] { null, null, overflow, null }, new long[] { 0, 0, overflow.size(), 0 }, 2, 2, (byte) (overflow.shift + SHIFT_INC), writer);
					if (currentIndex == 0)
						return new RelaxedVecNode(new Object[] { null, overflow, current, null }, new long[] { 0, overflow.size(), overflow.size() + current.size(), 0, }, 1, 2, (byte) (current.shift + SHIFT_INC), writer);
				}
				else {
					current = current.plusFirstChild(overflow, writer);
					break;
				}
				currentIndex--;
			}
		}
		
		for (int i = currentIndex; i >= 0; i--) {
			RelaxedVecNode parent = lineage[i];
			if (parent.writer != writer)
				parent = parent.forWriter(writer);
			parent.children[parent.head] = current;
			parent.recalcOffsets(false);
			parent.recalcStrict();
			current = parent;
		}
		return current;
	}
	
	//--- Minus ---//
	
	//--- Cloning ---//

	//Shallowly clones the children and offsets of this node under the given writer
	public RelaxedVecNode forWriter(Object writer) {
		return new RelaxedVecNode(children.clone(), offsets.clone(), head, tail, shift, writer);
	}
	
	//Deeply clones the children of this node, including cloning child nodes and groups,
	//under the given writer. This results in the returned node (at the time of being returned)
	//being entirely and solely owned by the given writer
	public RelaxedVecNode deepForWriter(Object writer) {
		//TODO
		return null;
	}
	
	//--- Static Stuff ---//
	
	static RelaxedVecNode singleGroup(Object[] group, Object writer) {
		return new RelaxedVecNode(new Object[] { null, group, null, null }, new long[] { 0, group.length, 0, 0 }, 1, 1, SHIFT_INC, writer);
	}

}
