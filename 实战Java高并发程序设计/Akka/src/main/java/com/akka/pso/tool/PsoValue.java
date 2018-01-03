package com.akka.pso.tool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 投资方案x,总收益value
 * 
 * @author 杨弢
 * 
 */
public final class PsoValue {
	final double value;
	final List<Double> x;

	public PsoValue(double v, List<Double> x) {
		this.value = v;
		List<Double> b = new ArrayList<Double>(5);
		b.addAll(x);
		this.x = Collections.unmodifiableList(b);
	}

	public double getValue() {
		return value;
	}

	public List<Double> getX() {
		return x;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("value:").append(value).append("\n").append(x.toString());
		return sb.toString();
	}

}