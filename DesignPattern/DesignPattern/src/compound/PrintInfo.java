package compound;

import compound.adapter.GooseAdapter;
import compound.composite.Flock;
import compound.decorator.QuackCounter;
import compound.factory.AbstractDuckFactory;
import compound.factory.impl.CountingDuckFactory;
import compound.impl.Goose;
import compound.observer.impl.Quackologist;

public class PrintInfo {

	public static void main(String[] args) {
		PrintInfo pi = new PrintInfo();
		/* 装饰工厂 */
		AbstractDuckFactory abstractDuckFactory = new CountingDuckFactory();

		pi.print(abstractDuckFactory);
	}

	public void print(AbstractDuckFactory abstractDuckFactory) {
		/* 工厂创建鸭子(有装饰) */
		Quackable redHeadDuck = abstractDuckFactory.createRedHeadDuck();
		Quackable duckCall = abstractDuckFactory.createDuckCall();
		Quackable rubberDuck = abstractDuckFactory.createRubberDuck();
		Quackable gooseDuck = new GooseAdapter(new Goose());

		System.out.println("Flocks...");
		/* 鸭子集合 */
		Flock flock = new Flock();
		flock.add(redHeadDuck);
		flock.add(duckCall);
		flock.add(rubberDuck);
		flock.add(gooseDuck);

		/* 绿头鸭集合 */
		Flock flockMall = new Flock();
		Quackable mallardDuck1 = abstractDuckFactory.createMallardDuck();
		Quackable mallardDuck2 = abstractDuckFactory.createMallardDuck();
		Quackable mallardDuck3 = abstractDuckFactory.createMallardDuck();
		Quackable mallardDuck4 = abstractDuckFactory.createMallardDuck();
		flockMall.add(mallardDuck1);
		flockMall.add(mallardDuck2);
		flockMall.add(mallardDuck3);
		flockMall.add(mallardDuck4);

		flock.add(flockMall);

		/* 全部鸭子 */
		// System.out.println("\nFlock Simulation");
		System.out.println("\nDuck Simulator:With Observer");
		Quackologist quackologist = new Quackologist();
		flock.registerObserver(quackologist);
		print(flock);

		/* 绿头鸭集合 */
		// System.out.println("\nMallard Flock Simulation");
		// print(flockMall);

		/* 鸭子数量 */
		System.out.println("The ducks Quacked:" + QuackCounter.getQuacks()
				+ " times");
	}

	public void print(Quackable duck) {
		duck.quack();
	}
}