package com.akka.pso.tool;

/**
 * 全局最优解的消息
 * 
 * @author 杨弢
 * 
 */
public final class GBestMsg {
	final PsoValue value;

	public GBestMsg(PsoValue v) {
		this.value = v;
	}

	public PsoValue getValue() {
		return value;
	}
}