package menu.tree;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 菜单类
 * 
 * @author 杨弢
 * 
 */
public class Menu extends MenuComponent {
	ArrayList<MenuComponent> menuComponent = new ArrayList<MenuComponent>();
	String name;
	String description;

	public Menu(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public void add(MenuComponent component) {
		menuComponent.add(component);
	}

	@Override
	public void remove(MenuComponent component) {
		menuComponent.remove(component);
	}

	@Override
	public MenuComponent getChild(int i) {
		return (MenuComponent) menuComponent.get(i);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void print() {
		System.out.print("\n" + getName());
		System.out.println(", " + getDescription());
		System.out.println("----------");
		Iterator<MenuComponent> iterator = menuComponent.iterator();
		while (iterator.hasNext()) {
			MenuComponent menuComponent = (MenuComponent) iterator.next();
			menuComponent.print();
		}
	}

	public Iterator createIterator() {
		return new CompositeIterator(menuComponent.iterator());
	}
}