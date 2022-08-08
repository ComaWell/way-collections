package art.comacreates.pds.nodes;

//"Single-Ended" VecNode that supports tail-ended addition and double-ended removal
//Primarily intended for stacks and queues.
public class SeVecNode {

	public static final byte SHIFT_INC = 5;
	public static final byte MAX_CHILDREN = 1 << SHIFT_INC;
	
	public static final int CHILD_MASK = MAX_CHILDREN - 1;
	
	static final Object[] EMPTY_CHILDREN = new Object[0];
	static final long[] EMPTY_OFFSETS = new long[0];
	
	public static final SeVecNode EMPTY = new SeVecNode(EMPTY_CHILDREN, EMPTY_OFFSETS, -1, SHIFT_INC, new Object(), true);
	
	Object[] children;
	long[] offsets;
	int tail;
	
	final byte shift;
	final Object writer;
	
	boolean strict;
	
	SeVecNode(Object[] children, long[] offsets, int tail, byte shift, Object writer, boolean strict) {
		this.children = children;
		this.offsets = offsets;
		this.tail = tail;
		this.shift = shift;
		this.writer = writer;
		this.strict = strict;
	}
	
	SeVecNode(Object[] children, long[] offsets, int tail, byte shift, Object writer) {
		this.children = children;
		this.offsets = offsets;
		this.tail = tail;
		this.shift = shift;
		this.writer = writer;
		recalcStrict();
	}
	
	SeVecNode(Object[] children, int tail, byte shift, Object writer) {
		this.children = children;
		this.offsets = new long[children.length];
		this.tail = tail;
		this.shift = shift;
		this.writer = writer;
		recalcOffsets(0);
		recalcStrict();
	}
	
	public long size() {
		return isEmpty() ? 0 : offsets[tail];
	}
	
	public boolean isEmpty() {
		return tail < 0;
	}
	
	public boolean isLeaf() {
		return shift == SHIFT_INC;
	}
	
	long offset(int childIndex) {
		return childIndex == 0 ? 0 : offsets[childIndex - 1];
	}
	
	//This isn't used much in the getters because this method accounds for both strict and relaxed nodes,
	//which we don't need in every scenario. (If a given node is strict, then all of its children/descendants
	//must also be strict, therefore we don't need to check each time.)
	int childIndex(long index) {
		int completeIndex = (int) (shift > 60 ? 0 : (index >>> shift) & CHILD_MASK);
		if (strict)
			return completeIndex;
		for (int i = completeIndex; i <= tail; i++)
			if (i < offsets[i])
				return i;
		return -1;
	}
	
	void recalcStrict() {
		this.strict = isEmpty() || size() == (tail + 1) * (1 << shift);
		//this.complete = !isEmpty() && size() == (lastChild + 1) * (1 << shift);
	}
	
	void recalcOffsets(int start) {
		if (shift == SHIFT_INC)
			for (int i = start; i <= tail; i++)
				offsets[i] = offset(i) + ((Object[]) children[i]).length;
		else for (int i = start; i <= tail; i++)
			offsets[i] = offset(i) + ((SeVecNode) children[i]).size();
	}
	
	//--- Getters ---//
	
	public Object[] firstGroup() {
		if (tail < 0)
			return null;
		SeVecNode node = this;
		byte shift = this.shift;
		while (shift > SHIFT_INC) {
			node = (SeVecNode) node.children[0];
			shift -= SHIFT_INC;
		}
		return (Object[]) node.children[0];
	}
	
	public Object first() {
		return firstGroup()[0];
	}
	
	public Object[] lastGroup() {
		if (tail < 0)
			return null;
		SeVecNode node = this;
		byte shift = this.shift;
		while (shift > SHIFT_INC) {
			node = (SeVecNode) node.children[node.tail];
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
		SeVecNode node = this;
		byte shift = this.shift;
		while (shift > SHIFT_INC) {
			node = (SeVecNode) node.children[(int) ((index >>> SHIFT_INC) & CHILD_MASK)];
			shift -= SHIFT_INC;
		}
		return (Object[]) node.children[(int) ((index >>> SHIFT_INC) & CHILD_MASK)];
	}
	
	Object strictGet(long index) {
		return strictGetGroup(index)[(int) index & CHILD_MASK];
	}
	
	Object[] relaxedGetGroup(long index) {
		SeVecNode node = this;
		byte shift = this.shift;
		long shiftedIndex = index;
		nodeTraversal: while (node.shift > SHIFT_INC) {
			if (node.strict)
				return node.strictGetGroup(index);
			for (int i = (int) ((shiftedIndex >>> shift) & CHILD_MASK); i <= node.tail; i++) {
				if (index < node.offsets[i]) {
					node = (SeVecNode) node.children[i];
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
			throw new InternalError("Failed to get childIndex on relaxed Node. index: " + index + ", first offset: " + node.offsets[0] + ", last offset: " + node.offsets[tail]);
		}
		return (Object[]) node.children[node.childIndex(shiftedIndex)];
	}
	
	//We need to have the offset information about the leaf node we're getting the group from in order to retrieve the correct
	//element from the group, which is why this method is an inlined copy of relaxedGetGroup rather than just calling it directly
	//(like strictGet does for strictGetGroup)
	Object relaxedGet(long index) {
		SeVecNode node = this;
		byte shift = this.shift;
		long shiftedIndex = index;
		nodeTraversal: while (node.shift > SHIFT_INC) {
			if (node.strict)
				return node.strictGetGroup(index);
			for (int i = (int) ((shiftedIndex >>> shift) & CHILD_MASK); i <= node.tail; i++) {
				if (index < node.offsets[i]) {
					node = (SeVecNode) node.children[i];
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
			throw new InternalError("Failed to get childIndex on relaxed Node. index: " + index + ", first offset: " + node.offsets[0] + ", last offset: " + node.offsets[tail]);
		}
		int childIndex = node.childIndex(shiftedIndex);
		return ((Object[]) node.children[childIndex])[(int) (shiftedIndex - node.offset(childIndex))];
	}
	
	SeVecNode plusChild(Object[] group, Object writer) {
		int groupLength = group.length;
		int oldLength = children.length;
		int oldTail = tail;
		int newTail = oldTail + 1;
		//If the writer owns this node, we only want to create new children/offset arrays if they're full
		if (this.writer == writer) {
			if (newTail < oldLength) {
				children[newTail] = group;
				offsets[newTail] = offsets[oldTail] + groupLength;
			}
			else {
				int newLength = Math.min(MAX_CHILDREN, oldLength << 1);
				Object[] newChildren = new Object[newLength];
				System.arraycopy(children, 0, newChildren, 0, oldLength);
				newChildren[newTail] = group;
				long[] newOffsets = new long[newLength];
				System.arraycopy(offsets, 0, newOffsets, 0, oldLength);
				newOffsets[newTail] = newOffsets[oldTail] + groupLength;
				this.children = newChildren;
				this.offsets = newOffsets;
			}
			this.tail = newTail;
			//If the group's length is MAX_CHILDREN then it wouldn't alter the node's strictness, and if it's not
			//then the node automatically cannot be strict- there's no need to do a full recalcStrict here
			if (groupLength != MAX_CHILDREN)
				this.strict = false;
			return this;
		}
		else {
			int newLength = newTail >= oldLength ? Math.min(MAX_CHILDREN, oldLength << 1) : oldLength;
			Object[] newChildren = new Object[newLength];
			System.arraycopy(children, 0, newChildren, 0, oldLength);
			newChildren[newTail] = group;
			long[] newOffsets = new long[newLength];
			System.arraycopy(offsets, 0, newOffsets, 0, oldLength);
			newOffsets[newTail] = newOffsets[oldTail] + group.length;
			return new SeVecNode(newChildren, newOffsets, newTail, shift, writer, strict && groupLength == MAX_CHILDREN);
		}
	}
	
	SeVecNode plusChild(SeVecNode child, Object writer) {
		long childSize = child.size();
		int oldLength = children.length;
		int oldTail = tail;
		int newTail = oldTail + 1;
		//If the writer owns this node, we only want to create new children/offset arrays if they're full
		if (this.writer == writer) {
			if (newTail < oldLength) {
				children[newTail] = child;
				offsets[newTail] = offsets[oldTail] + childSize;
			}
			else {
				int newLength = Math.min(MAX_CHILDREN, oldLength << 1);
				Object[] newChildren = new Object[newLength];
				System.arraycopy(children, 0, newChildren, 0, oldLength);
				newChildren[newTail] = child;
				long[] newOffsets = new long[newLength];
				System.arraycopy(offsets, 0, newOffsets, 0, oldLength);
				newOffsets[newTail] = newOffsets[oldTail] + childSize;
				this.children = newChildren;
				this.offsets = newOffsets;
			}
			this.tail = newTail;
			//If the group's length is MAX_CHILDREN then it wouldn't remove the node's strictness, and if it's not
			//then the node automatically cannot be strict- there's no need to do a full recalcStrict here
			if (!child.strict)
				this.strict = false;
			return this;
		}
		else {
			int newLength = newTail >= oldLength ? Math.min(MAX_CHILDREN, oldLength << 1) : oldLength;
			Object[] newChildren = new Object[newLength];
			System.arraycopy(children, 0, newChildren, 0, oldLength);
			newChildren[newTail] = child;
			long[] newOffsets = new long[newLength];
			System.arraycopy(offsets, 0, newOffsets, 0, oldLength);
			newOffsets[newTail] = newOffsets[oldTail] + childSize;
			return new SeVecNode(newChildren, newOffsets, newTail, shift, writer, strict && child.strict);
		}
	}
	
	public SeVecNode plus(Object[] group, Object writer) {
		if (tail < 0)
			return singleGroup(group, writer);
		int groupLength = group.length;
		long oldSize;
		boolean strictGroup = groupLength == MAX_CHILDREN;
		if (shift == SHIFT_INC)
			return tail == CHILD_MASK
				? new SeVecNode(new Object[] { this, singleGroup(group, writer), null, null }, new long[] { (oldSize = size()), oldSize + groupLength, 0, 0 }, 1, (byte) (SHIFT_INC << 1), writer, strict && strictGroup)
				: plusChild(group, writer);
		SeVecNode[] lineage = new SeVecNode[shift / SHIFT_INC];
		lineage[0] = this;
		for (int i = 1; i < lineage.length; i++) {
			SeVecNode parent = lineage[i - 1];
			lineage[i] = (SeVecNode) parent.children[parent.tail];
		}
		int currentIndex = lineage.length - 1;
		SeVecNode current = lineage[currentIndex];
		if (current.tail < CHILD_MASK)
			current = current.plusChild(group, writer);
		else {
			SeVecNode overflow = singleGroup(group, writer);
			byte overflowShift = SHIFT_INC;
			while (--currentIndex >= 0 && (current = lineage[currentIndex]).tail == CHILD_MASK)
				overflow = new SeVecNode(new Object[] { overflow, null, null, null }, new long[] { groupLength, 0, 0, 0 }, 0, (overflowShift += SHIFT_INC), writer, strictGroup);
			//TODO: reevaluate
			if (currentIndex < 0)
				return new SeVecNode(new Object[] { current, overflow, null, null }, new long[] { (oldSize = current.size()), oldSize + groupLength, 0, 0 }, 1, (byte) (overflowShift + SHIFT_INC), writer, current.strict && strictGroup);
			else current = current.plusChild(overflow, writer);
		}
		for (currentIndex -= 1; currentIndex >= 0; currentIndex--) {
			SeVecNode parent = lineage[currentIndex];
			if (parent.writer != writer)
				parent = parent.forWriter(writer);
			parent.children[parent.tail] = current;
			parent.offsets[parent.tail] += groupLength;
			if (!current.strict)
				parent.strict = false;
			current = parent;
		}
		return current;
	}
	
	//--- Minus ---//
	
	SeVecNode minusLastChild(Object writer) {
		int oldTail = tail;
		if (this.writer == writer) {
			children[oldTail] = null;
			offsets[oldTail] = 0;
			tail--;
			if (!strict)
				recalcStrict();//TODO: May be able to derive without recalcStrict
			return this;
		}
		else {
			int newTail = oldTail - 1;
			int length = children.length;
			Object[] newChildren = new Object[length];
			System.arraycopy(children, 0, newChildren, 0, oldTail);
			long[] newOffsets = new long[length];
			System.arraycopy(offsets, 0, newOffsets, 0, oldTail);
			return new SeVecNode(newChildren, newOffsets, newTail, shift, writer);//TODO: May be able to derive strict
		}
	}
	
	SeVecNode minusFirstChild(Object writer) {
		int oldTail = tail;
		long minusLength = shift == SHIFT_INC ? ((Object[]) children[0]).length : ((SeVecNode) children[0]).size();
		if (this.writer == writer) {
			//TODO: I heard somewhere that shifting all the elements this way is faster than System.arraycopy, but I should probably test that
			for (int i = 1; i < oldTail; i++)
				children[i] = children[i + 1];
			children[tail] = null;
			for (int i = 0; i < oldTail; i++)
				offsets[i] -= minusLength;
			offsets[oldTail] = 0;
			tail--;
			recalcStrict();//TODO: May be able to derive without recalcStrict
			return this;
		}
		else {
			int newTail = oldTail - 1;
			int length = children.length;
			Object[] newChildren = new Object[length];
			System.arraycopy(children, 1, newChildren, 0, oldTail);
			long[] newOffsets = new long[length];
			for (int i = 0; i < oldTail; i++)
				newOffsets[i] = offsets[i + 1] - minusLength;
			return new SeVecNode(newChildren, newOffsets, newTail, shift, writer);//TODO: Maybe be able to derive strict
		}
	}
	
	public SeVecNode minusLast(Object writer) {
		if (tail <= 0)
			return EMPTY;
		if (shift == SHIFT_INC)
			return minusLastChild(writer);
		SeVecNode[] lineage = new SeVecNode[shift / SHIFT_INC];
		lineage[0] = this;
		for (int i = 1; i < lineage.length; i++) {
			SeVecNode parent = lineage[i - 1];
			lineage[i] = (SeVecNode) parent.children[parent.tail];
		}
		int currentIndex = lineage.length - 1;
		SeVecNode current = lineage[currentIndex];
		long minusLength = ((Object[]) current.children[current.tail]).length;
		while ((current = lineage[currentIndex]).tail == 0 && --currentIndex >= 0) { }
		//The current node won't be empty after this removal
		if (current.tail > 1)
			current = current.minusLastChild(writer);
		//The root node is only 2 long and the second one is now empty, time to shrink and return the next closest root
		else if (currentIndex <= 0) {
			byte shift = current.shift;
			while ((current = (SeVecNode) current.children[0]).tail == 1 && (shift -= SHIFT_INC) > SHIFT_INC);
			return current;
		}
		//The current node will be empty, but it is not the root, so we just need to remove it from its parent
		else current = lineage[currentIndex].minusLastChild(writer);
		for (currentIndex -= 1; currentIndex >= 0; currentIndex--) {
			SeVecNode parent = lineage[currentIndex];
			if (parent.writer != writer)
				parent = parent.forWriter(writer);
			parent.children[parent.tail] = current;
			parent.offsets[parent.tail] -= minusLength;
			parent.recalcStrict();
			current = parent;
		}
		return current;
	}
	
	public SeVecNode minusFirst(Object writer) {
		if (tail <= 0)
			return EMPTY;
		if (shift == SHIFT_INC)
			return minusFirstChild(writer);
		SeVecNode[] lineage = new SeVecNode[shift / SHIFT_INC];
		lineage[0] = this;
		for (int i = 1; i < lineage.length; i++) {
			SeVecNode parent = lineage[i - 1];
			lineage[i] = (SeVecNode) parent.children[parent.tail];
		}
		int currentIndex = lineage.length - 1;
		SeVecNode current = lineage[currentIndex];
		long minusLength = ((Object[]) current.children[0]).length;
		while ((current = lineage[currentIndex]).tail == 0 && --currentIndex >= 0) { }
		//The current node won't be empty after this removal
		if (current.tail > 1)
			current = current.minusFirstChild(writer);
		//The root node is only 2 long and the second one is now empty, time to shrink and return the next closest root
		else if (currentIndex <= 0) {
			byte shift = current.shift;
			while ((current = (SeVecNode) current.children[1]).tail == 1 && (shift -= SHIFT_INC) > SHIFT_INC);
			return current;
		}
		//The current node will be empty, but it is not the root, so we just need to remove it from its parent
		else current = lineage[currentIndex].minusFirstChild(writer);
		for (currentIndex -= 1; currentIndex >= 0; currentIndex--) {
			SeVecNode parent = lineage[currentIndex];
			if (parent.writer != writer)
				parent = parent.forWriter(writer);
			parent.children[0] = current;
			for (int i = 0; i <= parent.tail; i++)
				parent.offsets[i] -= minusLength;
			parent.recalcStrict();
			current = parent;
		}
		return current;
	}
	
	//--- Concat ---//
	
	/*public SeVecNode plus(Object[] group, Object writer) {
		if (tail < 0)
			return singleGroup(group, writer);
		int groupLength = group.length;
		long oldSize;
		boolean strictGroup = groupLength == MAX_CHILDREN;
		if (shift == SHIFT_INC)
			return tail == CHILD_MASK
				? new SeVecNode(new Object[] { this, singleGroup(group, writer), null, null }, new long[] { (oldSize = size()), oldSize + groupLength, 0, 0 }, 1, (byte) (SHIFT_INC << 1), writer, strict && strictGroup)
				: plusChild(group, writer);
		SeVecNode[] lineage = new SeVecNode[shift / SHIFT_INC];
		lineage[0] = this;
		for (int i = 1; i < lineage.length; i++) {
			SeVecNode parent = lineage[i - 1];
			lineage[i] = (SeVecNode) parent.children[parent.tail];
		}
		int currentIndex = lineage.length - 1;
		SeVecNode current = lineage[currentIndex];
		if (current.tail < CHILD_MASK)
			current = current.plusChild(group, writer);
		else {
			SeVecNode overflow = singleGroup(group, writer);
			byte overflowShift = SHIFT_INC;
			while (--currentIndex >= 0 && (current = lineage[currentIndex]).tail == CHILD_MASK)
				overflow = new SeVecNode(new Object[] { overflow, null, null, null }, new long[] { groupLength, 0, 0, 0 }, 0, (overflowShift += SHIFT_INC), writer, strictGroup);
			//TODO: reevaluate
			if (currentIndex < 0)
				return new SeVecNode(new Object[] { current, overflow, null, null }, new long[] { (oldSize = current.size()), oldSize + groupLength, 0, 0 }, 1, (byte) (overflowShift + SHIFT_INC), writer, current.strict && strictGroup);
			else current = current.plusChild(overflow, writer);
		}
		for (currentIndex -= 1; currentIndex >= 0; currentIndex--) {
			SeVecNode parent = lineage[currentIndex];
			if (parent.writer != writer)
				parent = parent.forWriter(writer);
			parent.children[parent.tail] = current;
			parent.offsets[parent.tail] += groupLength;
			if (!current.strict)
				parent.strict = false;
			current = parent;
		}
		return current;
	}
	 * 
	 */
	
	static SeVecNode[] balanceLeft(SeVecNode left, SeVecNode right, Object writer) {
		int oldLeftLength = left.tail + 1;
		int oldRightLength = right.tail + 1;
		int totalLength = oldLeftLength + oldRightLength;
		int newLeftLength = Math.min(totalLength, MAX_CHILDREN);
		int newRightLength = totalLength - newLeftLength;
		Object[] newLeftChildren = new Object[newLeftLength];
		System.arraycopy(left.children, 0, newLeftChildren, 0, oldLeftLength);
		System.arraycopy(right.children, 0, newLeftChildren, newLeftLength, newLeftLength - oldLeftLength);
		long[] newLeftOffsets = new long[newLeftLength];
		System.arraycopy(left.offsets, 0, newLeftOffsets, 0, oldLeftLength);
		long lastOffset = newLeftOffsets[left.tail];
		for(int i = 0; i < oldRightLength; i++)
			newLeftOffsets[i + oldLeftLength] = lastOffset + right.offsets[i];
		SeVecNode newLeft = new SeVecNode(newLeftChildren, newLeftOffsets, newLeftLength - 1, left.shift, writer);
		//Everything was pushed to the left, no need to make a new right
		if (newRightLength <= 0)
			return new SeVecNode[] { newLeft };
		Object[] newRightChildren = new Object[newRightLength];
		System.arraycopy(right.children, newLeftLength - oldLeftLength, newRightChildren, 0, newRightLength);
		return new SeVecNode[] { newLeft, new SeVecNode(newRightChildren, newRightLength - 1, right.shift, writer) };
	}
	
	static SeVecNode[] balanceRight(SeVecNode left, SeVecNode right, Object writer) {
		int oldRightLength = right.tail + 1;
		int oldLeftLength = left.tail + 1;
		int totalLength = oldRightLength + oldLeftLength;
		int newRightLength = Math.min(totalLength, MAX_CHILDREN);
		int newLeftLength = totalLength - newRightLength;
		Object[] newRightChildren = new Object[newRightLength];
		//TODO
		System.arraycopy(left.children, 0, newRightChildren, 0, oldLeftLength - newLeftLength);
		System.arraycopy(right.children, 0, newLeftChildren, newLeftLength, newLeftLength - oldLeftLength);
		long[] newLeftOffsets = new long[newLeftLength];
		System.arraycopy(left.offsets, 0, newLeftOffsets, 0, oldLeftLength);
		long lastOffset = newLeftOffsets[left.tail];
		for(int i = 0; i < oldRightLength; i++)
			newLeftOffsets[i + oldLeftLength] = lastOffset + right.offsets[i];
		SeVecNode newLeft = new SeVecNode(newLeftChildren, newLeftOffsets, newLeftLength - 1, left.shift, writer);
		//Everything was pushed to the left, no need to make a new right
		if (newRightLength <= 0)
			return new SeVecNode[] { newLeft };
		Object[] newRightChildren = new Object[newRightLength];
		System.arraycopy(right.children, newLeftLength - oldLeftLength, newRightChildren, 0, newRightLength);
		return new SeVecNode[] { newLeft, new SeVecNode(newRightChildren, newRightLength - 1, right.shift, writer) };
	}
	
	public SeVecNode concat(SeVecNode node, Object editor) {
		if (tail < 0)
			return node;
		if (node.tail < 0)
			return this;
		SeVecNode[] leftLineage = new SeVecNode[shift / SHIFT_INC];
		leftLineage[0] = this;
		for (int i = 1; i < leftLineage.length; i++) {
			SeVecNode parent = leftLineage[i - 1];
			leftLineage[i] = (SeVecNode) parent.children[parent.tail];
		}
		SeVecNode[] rightLineage = new SeVecNode[shift / SHIFT_INC];
		rightLineage[0] = node;
		for (int i = 1; i < rightLineage.length; i++) {
			SeVecNode parent = rightLineage[i - 1];
			rightLineage[i] = (SeVecNode) parent.children[parent.tail];
		}
		int leftIndex = leftLineage.length - 1;
		int rightIndex = rightLineage.length - 1;
		SeVecNode currentLeft = leftLineage[leftIndex];
		SeVecNode currentRight = rightLineage[rightIndex];
		while (leftIndex > 0 && rightIndex > 0) {
			SeVecNode[] balanced = balanceLeft(currentLeft, currentRight, writer);
			currentLeft = leftLineage[--leftIndex].forWriter(writer);
			currentRight = rightLineage[--rightIndex];
			currentLeft.children[currentLeft.tail] = balanced[0];
			currentLeft.recalcOffsets(currentLeft.tail);
			if (balanced.length == 2) {
				currentRight = currentRight.forWriter(writer);
				currentRight.children[0] = balanced[1];
				currentRight.recalcOffsets(0);
			}
			else currentRight = currentRight.minusFirstChild(writer);
		}
	}
	
	//--- Slice ---// 
	
	static Object[] slice(Object[] group, int startInclusive, int endExclusive) {
		Object[] newGroup = new Object[endExclusive - startInclusive];
		System.arraycopy(group, startInclusive, newGroup, 0, newGroup.length);
		return newGroup;
	}
	
	//Note: leafs only
	static SeVecNode slice(SeVecNode node, long startInclusive, long endExclusive, Object writer) {
		int startIndex = node.childIndex(startInclusive);
		int endIndex = node.childIndex(endExclusive - 1);
		long firstStart = node.offset(startIndex);
		long firstEnd = node.offset(startIndex + 1);
		Object[] children = node.children;
		Object[] start = slice((Object[]) children[startIndex], (int) (startInclusive - firstStart), (int) (firstEnd - firstStart));
		
		long lastStart = node.offset(endIndex);
		Object[] end = slice((Object[]) children[endIndex], 0, (int) (endExclusive - lastStart));
		
		Object[] newChildren = new Object[endIndex + 1 - startIndex];
		int lastIndex = newChildren.length - 1;
		newChildren[0] = start;
		int copyStart = startIndex + 1;
		System.arraycopy(children, copyStart, newChildren, 1, lastIndex - 1);
		newChildren[lastIndex] = end;
		return new SeVecNode(newChildren, lastIndex, node.shift, writer);
	}
	
	public SeVecNode slice(long startInclusive, long endExclusive, Object writer) {
		if (startInclusive == endExclusive)
			return EMPTY;
		if (startInclusive == 0 && endExclusive == offsets[tail])
			return this;
		if (shift == SHIFT_INC)
			return slice(this, startInclusive, endExclusive, writer);
		int startIndex = childIndex(startInclusive);
		int endIndex = childIndex(endExclusive - 1);
		if (startIndex == endIndex) {
			long offset = offset(startIndex);
			SeVecNode child = (SeVecNode) children[startIndex];
			return child.shift == SHIFT_INC ? slice(child, startInclusive - offset, endExclusive - offset, writer)
					: child.slice(startInclusive, endExclusive, writer);
		}
		long firstStart = offset(startIndex);
		long firstEnd = offset(startIndex + 1);
		SeVecNode start = ((SeVecNode) children[startIndex]).slice(startInclusive - firstStart, firstEnd - firstStart, writer);
		
		long lastStart = offset(endIndex);
		SeVecNode end = ((SeVecNode) children[endIndex]).slice(0, endExclusive - lastStart, writer);
		
		Object[] newChildren = new Object[endIndex + 1 - startIndex];
		int lastIndex = newChildren.length - 1;
		newChildren[0] = start;
		int copyStart = startIndex + 1;
		System.arraycopy(children, copyStart, newChildren, 1, lastIndex - 1);
		newChildren[lastIndex] = end;
		long[] newOffsets = new long[newChildren.length];
		newOffsets[0] = start.size();
		for (int i = copyStart; i < endIndex; i++)
			newOffsets[i] = newOffsets[i - 1] + ((SeVecNode) newChildren[i]).size();//TODO: Remove these node casts
		return new SeVecNode(newChildren, newOffsets, lastIndex, shift, writer);
	}
	
	//--- Cloning ---//
	
	
	//Shallowly clones the children and offsets of this node under the given writer
	public SeVecNode forWriter(Object writer) {
		return new SeVecNode(children.clone(), offsets.clone(), tail, shift, writer, strict);
	}
	
	//--- Static Stuff ---//
	
	static SeVecNode singleGroup(Object[] group, Object writer) {
		int groupLength = group.length;
		return new SeVecNode(new Object[] { group, null, null, null }, new long[] { groupLength, 0, 0, 0 }, 0, SHIFT_INC, writer, groupLength == MAX_CHILDREN);
	}
	
}
