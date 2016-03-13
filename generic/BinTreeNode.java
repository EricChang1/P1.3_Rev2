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
	
	
	public String toString()
	{
		String ret =  new String();
		if (!isRoot())
			ret += "par " + getParent().getElement() + " ";
		ret += "val = " + getElement() + " children: ";
		if (hasChild (Side.LEFT))
			ret += getChild (Side.LEFT).getElement() + " "; 
		else
			ret +=  "null ";
		if (hasChild (Side.RIGHT))
			ret += getChild (Side.RIGHT).getElement() + " ";
		else
			ret += "null";
		return ret;
	}
	
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
		int leftH = hasChild (Side.LEFT) ? getChild (Side.LEFT).getHeight() : 0;
		int rightH = hasChild (Side.RIGHT) ? getChild (Side.RIGHT).getHeight() : 0;
		return Math.max (leftH, rightH) + 1;
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
	 * @param s a given side
	 * @return true if node has child on s
	 */
	public boolean hasChild (Side s)
	{
		if (s == Side.LEFT)
			return mLeft != null;
		else
			return mRight != null;
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
	 * detaches this node from its parent
	 * thus making this node the root of a tree
	 */
	public void makeRoot()
	{
		assert (!isRoot());
		getParent().setChild (null, getSide());
		mParent = null;
	}
	
	/**
	 * @param child node to set as child
	 * @param s side of child
	 * the references of the child node will be updated
	 * child == null is permissible
	 */
	public void setChild (BinTreeNode<T> child, Side s)
	{
		if (child != (s == Side.LEFT ? mLeft : mRight))
		{
			//modify child's parent field
			if (child != null)
			{
				if (!child.isRoot())
					child.getParent().setChild (null, child.getSide());
				child.mParent = this;
			}
			//remove old child & set new child
			if (s == Side.LEFT)
			{
				if (mLeft != null)
					mLeft.mParent = null;
				mLeft = child;
			}
			else
			{
				if (mRight != null)
					mRight.mParent = null;
				mRight = child;
			}
		}
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
