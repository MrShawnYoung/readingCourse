package menu;

import java.util.ArrayList;
import java.util.Iterator;
import menu.iterator.Menu;

/**
 * 煎饼类
 * 
 * @author 杨弢
 * 
 */
public class PancakeHouseMenu implements Menu {
	ArrayList<MenuItem> menuItems;

	public PancakeHouseMenu() {
		menuItems = new ArrayList<MenuItem>();
		addItem("K&B's", "eggs", true, 2.99);
		addItem("Regular", "eggs", false, 2.99);
		addItem("Blueberry", "blue", true, 3.49);
		addItem("Waffles", "eggs", true, 3.59);
	}

	public void addItem(String name, String description, boolean vegetarian,
			double price) {
		MenuItem menuItem = new MenuItem(name, description, vegetarian, price);
		menuItems.add(menuItem);
	}

	public Iterator createIterator() {
		return menuItems.iterator();
	}
}