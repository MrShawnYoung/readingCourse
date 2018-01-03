package menu.tree;

import java.util.Iterator;

/**
 * 菜单项类
 * 
 * @author 杨弢
 * 
 */
public class MenuItem extends MenuComponent {
	String name;
	String description;
	boolean vegetarian;
	double price;

	public MenuItem(String name, String description, boolean vegetarian,
			double price) {
		this.name = name;
		this.description = description;
		this.vegetarian = vegetarian;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public boolean isVegetarian() {
		return vegetarian;
	}

	public double getPrice() {
		return price;
	}

	public void print() {
		System.out.print(" " + getName());
		if (isVegetarian()) {
			System.out.println("(v)");
		}
		System.out.println(", " + getPrice());
		System.out.println(" --" + getDescription());
	}

	public Iterator createIterator() {
		return new NullIterator();
	}
}