package compound.observer.impl;

import compound.observer.Observer;
import compound.observer.QuackObservable;

/**
 * 通知信息类
 * 
 * @author 杨弢
 * 
 */
public class Quackologist implements Observer {

	@Override
	public void update(QuackObservable duck) {
		System.out.println("Quackologist " + duck + " just quacked.");
	}
}