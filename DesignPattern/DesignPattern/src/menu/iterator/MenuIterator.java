package menu.iterator;

import menu.MenuItem;

import java.util.Iterator;

/**
 * 餐厅迭代器
 * 
 * @author 杨弢
 * 
 */
public class MenuIterator implements Iterator {
	MenuItem[] items;
	int position = 0;

	public MenuIterator(MenuItem[] items) {
		this.items = items;
	}

	@Override
	public boolean hasNext() {
		if (position >= items.length || items[position] == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Object next() {
		MenuItem item = items[position];
		position += 1;
		return item;
	}

	@Override
	public void remove() {
		try {
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}