package menu.tree;

import java.util.Iterator;

/**
 * 空迭代器类
 * 
 * @author 杨弢
 * 
 */
public class NullIterator implements Iterator {

	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public Object next() {
		return null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}