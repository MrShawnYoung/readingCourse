package status.impl;

import status.Room;
import status.State;

/**
 * 预定状态
 * 
 * @author 杨弢
 * 
 */
public class BookedState implements State {
	transient Room hotelManagement;

	public BookedState(Room hotelManagement) {
		this.hotelManagement = hotelManagement;
	}

	@Override
	public void bookRoom() {
		System.out.println("该房间已经给预定了...");
	}

	@Override
	public void unsubscribeRoom() {
		System.out.println("退订成功,欢迎下次光临...");
		hotelManagement.setState(hotelManagement.getFreeTimeState());
	}

	@Override
	public void checkInRoom() {
		System.out.println("入住成功...");
		hotelManagement.setState(hotelManagement.getCheckInState());
	}

	@Override
	public void checkOutRoom() {

	}
}