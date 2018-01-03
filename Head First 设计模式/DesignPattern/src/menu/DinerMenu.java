package menu;

import java.util.Iterator;
import menu.iterator.Menu;
import menu.iterator.MenuIterator;

/**
 * 餐厅类
 * 
 * @author 杨弢
 * 
 */
public class DinerMenu implements Menu {
	static final int MAX_ITEMS = 6;
	int numberOfItems = 0;
	MenuItem[] menuItems;

	public DinerMenu() {
		menuItems = new MenuItem[MAX_ITEMS];
		addItem("BLT", "eggs", true, 2.99);
		addItem("Bacion", "eggs", false, 2.99);
		addItem("Soup", "blue", true, 3.09);
		addItem("hotdog", "eggs", true, 3.49);
	}

	public void addItem(String name, String description, boolean vegetarian,
			double price) {
		MenuItem menuItem = new MenuItem(name, description, vegetarian, price);
		if (numberOfItems >= MAX_ITEMS) {
			System.err.println("Sorry");
		} else {
			menuItems[numberOfItems] = menuItem;
			numberOfItems += 1;
		}
	}

	public Iterator createIterator() {
		return new MenuIterator(menuItems);
	}
}