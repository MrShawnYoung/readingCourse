package chapter14;

import java.util.function.Consumer;

/**
 * 破坏式更新和函数式更新的比较
 * 
 * @author Loops
 *
 */
public class PersistentTrainJourney {

	public static class TrainJourney {
		public int price;
		public TrainJourney onward;

		public TrainJourney(int p, TrainJourney t) {
			price = p;
			onward = t;
		}
	}

	public static TrainJourney link(TrainJourney a, TrainJourney b) {
		if (a == null) {
			return b;
		}
		TrainJourney t = a;
		while (t.onward != null) {
			t = t.onward;
		}
		t.onward = b;
		return a;
	}

	public static TrainJourney append(TrainJourney a, TrainJourney b) {
		return a == null ? b : new TrainJourney(a.price, append(a.onward, b));
	}

	public static void visit(TrainJourney journey, Consumer<TrainJourney> c) {
		if (journey != null) {
			c.accept(journey);
			visit(journey.onward, c);
		}
	}
}