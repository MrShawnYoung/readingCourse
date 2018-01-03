package compound.factory.impl;

import compound.decorator.QuackCounter;
import compound.Quackable;
import compound.factory.AbstractDuckFactory;
import compound.impl.DuckCall;
import compound.impl.MallardDuck;
import compound.impl.RedHeadDuck;
import compound.impl.RubberDuck;

/**
 * 装饰鸭子工厂
 * 
 * @author 杨弢
 * 
 */
public class CountingDuckFactory extends AbstractDuckFactory {

	@Override
	public Quackable createMallardDuck() {
		return new QuackCounter(new MallardDuck());
	}

	@Override
	public Quackable createRedHeadDuck() {
		return new QuackCounter(new RedHeadDuck());
	}

	@Override
	public Quackable createDuckCall() {
		return new QuackCounter(new DuckCall());
	}

	@Override
	public Quackable createRubberDuck() {
		return new QuackCounter(new RubberDuck());
	}
}