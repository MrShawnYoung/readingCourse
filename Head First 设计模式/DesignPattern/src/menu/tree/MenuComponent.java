package menu.tree;

import java.util.Iterator;

/**
 * 菜单树抽象类
 * 
 * @author 杨弢
 * 
 */
public abstract class MenuComponent {
	public void add(MenuComponent component) {
		throw new UnsupportedOperationException();
	}

	public void remove(MenuComponent component) {
		throw new UnsupportedOperationException();
	}

	public MenuComponent getChild(int i) {
		throw new UnsupportedOperationException();
	}

	public String getName() {
		throw new UnsupportedOperationException();
	}

	public String getDescription() {
		throw new UnsupportedOperationException();
	}

	public double getPrice() {
		throw new UnsupportedOperationException();
	}

	public boolean isVegetariean() {
		throw new UnsupportedOperationException();
	}

	public void print() {
		throw new UnsupportedOperationException();
	}

	public Iterator createIterator() {
		return null;
	}
}