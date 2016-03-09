package generic;

import generic.BinTreeNode.Side;


public class AvlTree<T extends Comparable<T>> 
{
	/**
	 * exception thrown if node requested does not exist
	 * @author martin
	 */
	public static class NotInTreeException extends IllegalArgumentException
	{
		public NotInTreeException() {}
		
		public NotInTreeException (String mess) { super (mess); }
	}
	
	/**
	 * class used for non-existing nodes
	 * @author martin
	 */
	public class NullNode extends BinTreeNode<T>
	{
		public NullNode() 
		{
			super (null);
		}
		
		public boolean isNull (BinTreeNode<T> check)
		{
			return (check.getElement() == null && check.isLeaf() && check.isRoot());
		}
	}
	
	/**
	 * wrapper for binary tree nodes
	 * used to compare nodes depending on their value
	 * @author martin
	 */
	public class NodeOrderWrapper implements Comparable<NodeOrderWrapper>
	{
		/**
		 * @param n node to use as reference for comparison
		 */
		public NodeOrderWrapper (BinTreeNode<T> n)
		{
			mNode = n;
		}
		
		/**
		 * @param comp node wrapper to compare reference with
		 * @return result of comparison of element of reference node to element of
		 * comp
		 */
		public int compareTo (NodeOrderWrapper comp)
		{
			return mNode.getElement().compareTo (comp.mNode.getElement());
		}
		
		private BinTreeNode<T> mNode;
	}
	
	/**
	 * default constructor
	 */
	public AvlTree()
	{
		
	}
	
	/**
	 * @param val a given value in the tree
	 * @return height of first node of value val found
	 */
	public int getHeight (T val)
	{
		BinTreeNode<T> node = getNode (val);
		if (new NullNode().isNull (node))
			throw new NotInTreeException ("value requested is not in tree and cannot be replaced");
		return node.getHeight();
	}
	
	/**
	 * @return number of nodes stored
	 */
	public int getSize() { return mSize; }
	
	/**
	 * @param addVal value to add to tree
	 */
	public void add (T addVal)
	{
		if (mRoot == null)
			mRoot = new BinTreeNode<>(addVal);
		else
		{
			BinTreeNode<T> leaf = getClosestLeaf (addVal);
			//add new node to tree
			if (addVal.compareTo (leaf.getElement()) < 0)
				leaf.setChild (new BinTreeNode<> (addVal, leaf), Side.LEFT);
			else
				leaf.setChild (new BinTreeNode<> (addVal, leaf), Side.RIGHT);
			//check for restructuring
			BinTreeNode<T> child = null, grandchild = null;
			boolean restored = false;
			while (!leaf.isRoot() && !restored)
			{
				grandchild = child;
				child = leaf;
				leaf = leaf.getParent();
				
				if (grandchild != null)
				{
					int heightL, heightR;
					heightL = leaf.getChild (Side.LEFT).getHeight();
					heightR = leaf.getChild (Side.RIGHT).getHeight();
					//restructure
					if (Math.abs (heightL - heightR) > 1)
					{
						restructure (leaf, child, grandchild);
						restored = true;
					}
				}				
			}
		}
		++mSize;
	}
	
	/**
	 * @param remVal value of node to remove
	 */
	public void remove (T remVal)
	{
		//search node
		BinTreeNode<T> node = getNode (remVal);
		if (new NullNode().isNull (node))
			throw new NotInTreeException ("value requested is not in tree and cannot be replaced");
		//binary tree removal
		if (node.isRoot())
			mRoot = null;
		else if (!node.isInternal())
		{
			BinTreeNode<T> replace = null;
			if (node.getChild (Side.LEFT) != null)
				replace = node.getChild (Side.LEFT);
			else if (node.getChild (Side.RIGHT) != null)
				replace = node.getChild (Side.RIGHT);
			
			node.getParent().setChild (replace, node.getSide());
		}
		else
		{
			BinTreeNode<T> leaf = getClosestLeaf (remVal);
			node.getParent().getChild (node.getSide()).setElement (leaf.getElement());
		}
		//trinode restructuring
		BinTreeNode<T> child = null; 
		while (!node.isRoot())
		{
			child = node;
			node = node.getParent();
			int heightL, heightR;
			heightL = node.getChild (Side.LEFT).getHeight();
			heightR = node.getChild (Side.RIGHT).getHeight();
			//if unbalanced
			if (Math.abs (heightL - heightR) > 1)
			{
				BinTreeNode<T> lvl2, lvl3;
				Side sideChosen;
				if (node.isChild (child, Side.LEFT))
				{
					lvl2 = node.getChild (Side.RIGHT);
					sideChosen = Side.RIGHT;
				}
				else
				{
					lvl2 = node.getChild (Side.LEFT);
					sideChosen = Side.LEFT;
				}
				
				int heightL2, heightR2;
				heightL2 = lvl2.getChild (Side.LEFT).getHeight();
				heightR2 = lvl2.getChild (Side.RIGHT).getHeight();
				if (heightL2 > heightR2)
					lvl3 = lvl2.getChild (Side.LEFT);
				else if (heightL2 < heightR2)
					lvl3 = lvl2.getChild (Side.RIGHT);
				else
					lvl3 = lvl2.getChild (sideChosen);
				restructure (node, lvl2, lvl3);
			}
		}
		--mSize;
	}
	
	
	/**
	 * @param oldVal value stored in tree to replace
	 * @param newVal value to replace oldVal with
	 * the first occurrence of oldVal will be replaced
	 */
	public void replace (T oldVal, T newVal)
	{
		BinTreeNode<T> node = getNode (oldVal);
		if (new NullNode().isNull (node))
			throw new NotInTreeException ("value requested is not in tree and cannot be replaced");
		node.setElement (newVal);
	}
	
	
	/**
	 * @param elem value of node to retrieve
	 * @return node having elem as value or null node if none is found
	 */
	private BinTreeNode<T> getNode (T elem)
	{
		BinTreeNode<T> currNode = mRoot;
		while (currNode != null)
		{
			int indicator = currNode.getElement().compareTo (elem);
			if (indicator == 0)
				return currNode;
			else if (indicator < 0)
				currNode.getChild (Side.LEFT);
			else
				currNode.getChild (Side.RIGHT);
		}
		return new NullNode();
	}
	
	/**
	 * @param elem target value of leaf node
	 * @return leaf node with closest value possible to elem
	 */
	private BinTreeNode<T> getClosestLeaf (T val)
	{
		BinTreeNode<T> curr = mRoot;
		while (!curr.isLeaf())
		{
			if (val.compareTo (curr.getElement()) < 0)
				curr = curr.getChild (Side.LEFT);
			else
				curr = curr.getChild (Side.RIGHT);
		}
		return curr;
	}
	
	/**
	 * 
	 * @param x bottom up first unbalanced node
	 * @param y higher child of x
	 * @param z higher child of y
	 */
	private void restructure (BinTreeNode<T> x, BinTreeNode<T> y, BinTreeNode<T> z)
	{
		//map x, y, z to inorder sequence
		QuickSort<NodeOrderWrapper> orderInput = new QuickSort<>();
		//store inorder of subtrees of x, y, z != x, y, z
		QuickSort <NodeOrderWrapper> orderSubtrees = new QuickSort<>();
		
		orderInput.add (new NodeOrderWrapper (x));
		orderInput.add (new NodeOrderWrapper (y));
		orderInput.add (new NodeOrderWrapper (z));
		
		if (x.isChild (y, Side.LEFT))
			orderSubtrees.add (new NodeOrderWrapper (x.getChild (Side.RIGHT)));
		else
			orderSubtrees.add (new NodeOrderWrapper (x.getChild (Side.LEFT)));
		if (y.isChild (z, Side.LEFT))
			orderSubtrees.add (new NodeOrderWrapper (y.getChild (Side.RIGHT)));
		else
			orderSubtrees.add (new NodeOrderWrapper (y.getChild (Side.LEFT)));
		orderSubtrees.add (new NodeOrderWrapper (z.getChild (Side.LEFT)));
		orderSubtrees.add (new NodeOrderWrapper (z.getChild (Side.RIGHT)));
		
		orderInput.sort (0, orderInput.size() - 1);
		orderSubtrees.sort (0, orderSubtrees.size() - 1);
		//restructure parameters
		Side xSide = x.getParent().isChild (x, Side.LEFT) ? Side.LEFT : Side.RIGHT;
		x.getParent().setChild (orderInput.get (1).mNode, xSide);
		orderInput.get (1).mNode.setChild (orderInput.get (0).mNode, Side.LEFT);
		orderInput.get (1).mNode.setChild (orderInput.get (2).mNode, Side.RIGHT);
		//restructure subtrees
		orderInput.get (0).mNode.setChild (orderSubtrees.get (0).mNode, Side.LEFT);
		orderInput.get (0).mNode.setChild (orderSubtrees.get (1).mNode, Side.RIGHT);
		orderInput.get (2).mNode.setChild (orderSubtrees.get (2).mNode, Side.LEFT);
		orderInput.get (2).mNode.setChild (orderSubtrees.get (3).mNode, Side.RIGHT);
	}
	
	/**
	 * @param n1 a given node
	 * @param n2 another given node
	 * swaps values of n1, n2
	 */
	private void swapValues (BinTreeNode<T> n1, BinTreeNode<T> n2)
	{
		T temp = n1.getElement();
		n1.setElement (n2.getElement());
		n2.setElement (temp);
	}
	
	private BinTreeNode<T> mRoot;
	private int mSize;
}
