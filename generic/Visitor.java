package generic;

/**
 * interface to be implemented for tree traversals
 * in avl tree class
 * @author martin
 * @param <T>
 */
public interface Visitor<T>
{
	public void visit (T elem);
}
