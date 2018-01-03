package com.akka.pso.tool;

import java.util.List;

/**
 * 计算适度的收益额
 * 
 * @author 杨弢
 * 
 */
public class Fitness {
	public static double fitness(List<Double> x) {
		double sum = 0;
		for (int i = 1; i < x.size(); i++) {
			sum += Math.sqrt(x.get(i));
		}
		return sum;
	}
}