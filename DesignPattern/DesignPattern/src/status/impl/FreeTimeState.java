package status.impl;

import status.Room;
import status.State;

/**
 * 空闲状态
 * 
 * @author 杨弢
 * 
 */
public class FreeTimeState implements State {
	transient Room hotelManagement;

	public FreeTimeState(Room hotelManagement) {
		this.hotelManagement = hotelManagement;
	}

	@Override
	public void bookRoom() {
		System.out.println("您已经成功预订了...");
		hotelManagement.setState(hotelManagement.getBookedState());
	}

	@Override
	public void unsubscribeRoom() {

	}

	@Override
	public void checkInRoom() {
		System.out.println("您已经成功入住了...");
		hotelManagement.setState(hotelManagement.getCheckInState());
	}

	@Override
	public void checkOutRoom() {

	}
}
