package compound.adapter;

import compound.Quackable;
import compound.impl.Goose;
import compound.observer.Observer;

/**
 * 鹅适配器
 * 
 * @author 杨弢
 * 
 */
public class GooseAdapter implements Quackable {
	Goose goose;

	public GooseAdapter(Goose goose) {
		this.goose = goose;
	}

	@Override
	public void quack() {
		goose.honk();
	}

	@Override
	public void registerObserver(Observer observer) {

	}

	@Override
	public void notifyObservers() {

	}
}