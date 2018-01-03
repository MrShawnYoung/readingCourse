package compound.composite;

import java.util.ArrayList;
import java.util.Iterator;
import compound.Quackable;
import compound.observer.Observer;

/**
 * 鸟集合
 * 
 * @author 杨弢
 * 
 */
public class Flock implements Quackable {
	ArrayList ducks = new ArrayList();

	public void add(Quackable duck) {
		ducks.add(duck);
	}

	@Override
	public void quack() {
		Iterator iterator = ducks.iterator();
		while (iterator.hasNext()) {
			Quackable duck = (Quackable) iterator.next();
			duck.quack();
		}
	}

	@Override
	public void registerObserver(Observer observer) {
		Iterator iterator = ducks.iterator();
		while (iterator.hasNext()) {
			Quackable duck = (Quackable) iterator.next();
			duck.registerObserver(observer);
		}
	}

	@Override
	public void notifyObservers() {
		
	}
}