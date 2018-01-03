package com.akka.pso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.akka.pso.tool.Fitness;
import com.akka.pso.tool.GBestMsg;
import com.akka.pso.tool.PBestMsg;
import com.akka.pso.tool.PsoValue;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * 粒子
 * 
 * @author 杨弢
 * 
 */
public class Bird extends UntypedActor {
	private final LoggingAdapter log = Logging.getLogger(getContext().system(),
			this);
	// 个体最优
	private PsoValue pBest = null;
	// 全局最优
	private PsoValue gBest = null;
	// 粒子速度
	private List<Double> velocity = new ArrayList<Double>(5);
	// 投资方案，即每年投资额
	private List<Double> x = new ArrayList<Double>(5);
	// 随机数
	private Random r = new Random();

	@Override
	public void preStart() throws Exception {
		for (int i = 0; i < 5; i++) {
			velocity.add(Double.NEGATIVE_INFINITY);
			x.add(Double.NEGATIVE_INFINITY);
		}

		// x1<=400
		x.set(1, (double) r.nextInt(401));

		// x2<=440-1.1*x1
		double max = 400 - 1.1 * x.get(1);
		if (max < 0) {
			max = 0;
		}
		x.set(2, r.nextDouble() * max);

		// x3<=484-1.21*x1-1.1*x2
		max = 484 - 1.21 * x.get(1) - 1.1 * x.get(2);
		if (max < 0) {
			max = 0;
		}
		x.set(3, r.nextDouble() * max);

		// x4<=5322.4-1.331*x1-1.21*x2-1.1*x3
		max = 532.4 - 1.331 * x.get(1) - 1.21 * x.get(2) - 1.1 * x.get(3);
		if (max < 0) {
			max = 0;
		}
		x.set(4, r.nextDouble() * max);
		// 计算适应度
		double newFit = Fitness.fitness(x);
		pBest = new PsoValue(newFit, x);
		PBestMsg pBestMsg = new PBestMsg(pBest);
		ActorSelection selection = getContext().actorSelection(
				"/user/masterbird");
		selection.tell(pBestMsg, getSelf());
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof GBestMsg) {
			// 全局最优
			gBest = ((GBestMsg) msg).getValue();
			// 更新速度
			for (int i = 1; i < velocity.size(); i++) {
				updateVelocity(i);
			}
			// 更新位置
			for (int i = 1; i < x.size(); i++) {
				updateX(i);
			}
			// 验证
			validateX();
			double newFit = Fitness.fitness(x);
			if (newFit > pBest.getValue()) {
				pBest = new PsoValue(newFit, x);
				PBestMsg pBestMsg = new PBestMsg(pBest);
				getSender().tell(pBestMsg, getSelf());
			}
		} else {
			unhandled(msg);
		}
	}

	public void validateX() {
		if (x.get(1) > 400) {
			x.set(1, (double) r.nextInt(401));
		}
		// x2
		double max = 400 - 1.1 * x.get(1);
		if (x.get(2) > max || x.get(2) < 0) {
			x.set(2, r.nextDouble() * max);
		}
		// x3
		max = 484 - 1.21 * x.get(1) - 1.1 * x.get(2);
		if (x.get(3) > max || x.get(3) < 0) {
			x.set(3, r.nextDouble() * max);
		}
		// x4
		max = 532.4 - 1.331 * x.get(1) - 1.21 * x.get(2) - 1.1 * x.get(3);
		if (x.get(4) > max || x.get(4) < 0) {
			x.set(4, r.nextDouble() * max);
		}
	}

	public double updateVelocity(int i) {
		double v = Math.random() * velocity.get(i) + 2 * Math.random()
				* (pBest.getX().get(i) - x.get(i)) + 2 * Math.random()
				* (gBest.getX().get(i) - x.get(i));
		v = v > 0 ? Math.min(v, 5) : Math.max(v, -5);
		velocity.set(i, v);
		return v;
	}

	public double updateX(int i) {
		double newX = x.get(i) + velocity.get(i);
		x.set(i, newX);
		return newX;
	}
}