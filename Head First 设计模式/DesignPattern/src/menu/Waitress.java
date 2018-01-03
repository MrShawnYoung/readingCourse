package menu;

import java.util.Iterator;

import menu.tree.MenuComponent;

/**
 * 服务员类
 * 
 * @author 杨弢
 * 
 */
public class Waitress {
	MenuComponent allMenus;

	public Waitress(MenuComponent allMenus) {
		this.allMenus = allMenus;
	}

	public void print() {
		allMenus.print();
	}

	public void printVegetarianMenu() {
		Iterator iterator = allMenus.createIterator();
		System.out.println("vegetarian menu");
		while (iterator.hasNext()) {
			MenuComponent component = (MenuComponent) iterator.next();
			try {
				if (component.isVegetariean()) {
					component.print();
				}
			} catch (UnsupportedOperationException e) {
				e.printStackTrace();
			}
		}
	}
}