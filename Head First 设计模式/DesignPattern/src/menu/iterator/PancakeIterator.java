package menu.iterator;

import java.util.ArrayList;
import java.util.Hashtable;

import menu.MenuItem;

/**
 * 煎饼迭代器
 * 
 * @author 杨弢
 * 
 */
public class PancakeIterator implements Iterator {
	ArrayList items;
	int index = 0;

	@Override
	public boolean hasNext() {
		if (index >= items.size() || items.get(index) == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Object next() {
		MenuItem item = (MenuItem) items.get(index);
		index += 1;
		return item;
	}
}