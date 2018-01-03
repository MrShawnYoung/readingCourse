package compound;

import compound.observer.QuackObservable;

/**
 * 行为接口
 * 
 * @author 杨弢
 * 
 */
public interface Quackable extends QuackObservable {
	public void quack();
}