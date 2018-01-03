package com.akka.pso.tool;

/**
 * 个体最优解的消息
 * 
 * @author 杨弢
 * 
 */
public final class PBestMsg {
	final PsoValue value;

	public PBestMsg(PsoValue v) {
		this.value = v;
	}

	public PsoValue getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value.toString();
	}
}