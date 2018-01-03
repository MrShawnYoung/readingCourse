package compound.factory.impl;

import compound.Quackable;
import compound.factory.AbstractDuckFactory;
import compound.impl.DuckCall;
import compound.impl.MallardDuck;
import compound.impl.RedHeadDuck;
import compound.impl.RubberDuck;

/**
 * 无装饰鸭子工厂
 * 
 * @author 杨弢
 * 
 */
public class DuckFactory extends AbstractDuckFactory {

	@Override
	public Quackable createMallardDuck() {
		return new MallardDuck();
	}

	@Override
	public Quackable createRedHeadDuck() {
		return new RedHeadDuck();
	}

	@Override
	public Quackable createDuckCall() {
		return new DuckCall();
	}

	@Override
	public Quackable createRubberDuck() {
		return new RubberDuck();
	}
}