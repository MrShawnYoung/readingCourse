package compound.factory;

import compound.Quackable;

/**
 * 抽象鸭子工厂
 * 
 * @author 杨弢
 * 
 */
public abstract class AbstractDuckFactory {
	public abstract Quackable createMallardDuck();

	public abstract Quackable createRedHeadDuck();

	public abstract Quackable createDuckCall();

	public abstract Quackable createRubberDuck();
}