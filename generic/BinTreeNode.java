package generic;

/**
 * class modeling a node associating a value with an index
 * @author martin
 * @param <T> type stored in node
 */
public class BinTreeNode<T> 
{
	public enum Side {LEFT, RIGHT}
	
	/**
	 * constructs a root node
	 * @param val element to contain in node
	 */
	public BinTreeNode (T val)
	{
		mVal = val;
	}
	
	/**
	 * constructs a leaf node
	 * @param elem value to contain
	 * @param parent parent of this node
	 * the parent node needs to be updated manually
	 */
	public BinTreeNode (T val, BinTreeNode<T> parent)
	{
		mParent = parent;
		mVal = val;
	}
	
	/**
	 * @return left child of this node
	 */
	public BinTreeNode<T> getChild (Side s) 
	{ 
		if (s == Side.LEFT)
			return mLeft; 
		else
			return mRight;
	}
	
	/**
	 * @return return the parent of this node
	 */
	public BinTreeNode<T> getParent() { return mParent; }
	
	/**
	 * @return sibling of this node
	 * Precondition: this node is not the tree's root
	 */
	public BinTreeNode<T> getSibling()
	{
		if (getSide() == Side.LEFT)
			return getParent().getChild (Side.RIGHT);
		else
			return getParent().getChild (Side.LEFT);
	}
	
	/**
	 * @return element stored
	 */
	public T getElement() { return mVal; }
	
	/**
	 * @return which side this node is on
	 * Precondition: this node is not the tree's root
	 */
	public Side getSide()
	{
		if (getParent().isChild (this, Side.LEFT))
			return Side.LEFT;
		else
			return Side.RIGHT;
	}
	
	/**
	 * @return height of this node, 1 if node is leaf node
	 */
	public int getHeight()
	{
		if (isLeaf())
			return 1;
		return Math.max (getChild (Side.LEFT).getHeight(), getChild (Side.RIGHT).getHeight()) + 1;
	}
	
	/**
	 * @param check node to verify
	 * @param s side of node
	 * @return true if check references the same object as the left child stored
	 */
	public boolean isChild (BinTreeNode<T> check, Side s) 
	{ 
		if (s == Side.LEFT)
			return (check == mLeft); 
		else
			return (check == mRight);
	}
	
	/**
	 * @return true if node has no parent
	 */
	public boolean isRoot() { return (mParent == null); }
	
	/**
	 * @return true if node has two children and a parent
	 */
	public boolean isInternal()
	{
		return (mLeft != null && mRight != null && mParent != null);
	}
	
	/**
	 * @return true if node has no children
	 */
	public boolean isLeaf() { return (mLeft == null && mRight == null); }
	
	/**
	 * @param child node to set as child
	 * @param s side of child
	 * the references of the child node will be updated
	 * child == null is permissible
	 */
	public void setChild (BinTreeNode<T> child, Side s)
	{
		if (child != null)
		{
			if (!child.isRoot())
			{
				if (child.mParent.mLeft == child)
					child.mParent.mLeft = null;
				else
					child.mParent.mRight = null;
			}
			child.mParent = this;
		}
		if (s == Side.LEFT)
			mLeft = child;
		else
			mRight = child;
	}
	
	/**
	 * @param elem new element of node
	 */
	public void setElement (T newElem)
	{
		mVal = newElem;
	}
	
	private BinTreeNode<T> mParent, mLeft, mRight;
	private T mVal;
}
