package status;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import status.impl.BookedState;
import status.impl.CheckInState;
import status.impl.FreeTimeState;

/**
 * 房间类
 * 
 * @author 杨弢
 * 
 */
public class Room extends UnicastRemoteObject implements RoomRemote {
	State freeTimeState; // 空闲状态
	State checkInState; // 入住状态
	State bookedState; // 预订状态

	State state;

	int count;
	String location;

	public Room(String location, int numberRooms) throws RemoteException {
		freeTimeState = new FreeTimeState(this);
		checkInState = new CheckInState(this);
		bookedState = new BookedState(this);

		state = freeTimeState; // 初始状态为空闲
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public String getLocaton() {
		return location;
	}

	/**
	 * 预订房间
	 */
	public void bookRoom() {
		state.bookRoom();
	}

	/**
	 * 入住
	 */
	public void checkInRoom() {
		state.checkInRoom();
	}

	/**
	 * 退订房间
	 */
	public void unsubscribeRoom() {
		state.unsubscribeRoom();
	}

	/**
	 * 退房
	 */
	public void checkOutRoom() {
		state.checkOutRoom();
	}

	public String toString() {
		return "该房间的状态是:" + getState().getClass().getName();
	}

	public State getFreeTimeState() {
		return freeTimeState;
	}

	public void setFreeTimeState(State freeTimeState) {
		this.freeTimeState = freeTimeState;
	}

	public State getCheckInState() {
		return checkInState;
	}

	public void setCheckInState(State checkInState) {
		this.checkInState = checkInState;
	}

	public State getBookedState() {
		return bookedState;
	}

	public void setBookedState(State bookedState) {
		this.bookedState = bookedState;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
}