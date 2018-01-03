package status.impl;

import status.Room;
import status.State;

/**
 * 入住状态
 * 
 * @author 杨弢
 * 
 */
public class CheckInState implements State {
	transient Room hotelManagement;

	public CheckInState(Room hotelManagement) {
		this.hotelManagement = hotelManagement;
	}

	@Override
	public void bookRoom() {
		System.out.println("该房间已经入住了...");
	}

	@Override
	public void unsubscribeRoom() {

	}

	@Override
	public void checkInRoom() {
		System.out.println("该房间已经入住了...");
	}

	@Override
	public void checkOutRoom() {
		System.out.println("退房成功...");
		hotelManagement.setState(hotelManagement.getFreeTimeState()); // 状态变成空闲
	}
}