package status;

/**
 * 房间监视器
 * 
 * @author 杨弢
 * 
 */
public class RoomMonitor {
	RoomRemote room;

	public RoomMonitor(RoomRemote room) {
		this.room = room;
	}

	public void report() {
		try {
			System.out.println("位置：" + room.getLocaton());
			System.out.println("总数：" + room.getCount());
			System.out.println("状态" + room.getState());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}