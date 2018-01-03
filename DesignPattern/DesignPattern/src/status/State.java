package status;

import java.io.Serializable;

/**
 * 状态接口
 * 
 * @author 杨弢
 * 
 */
public interface State extends Serializable {
	/**
	 * 预订房间
	 */
	public void bookRoom();

	/**
	 * 退订房间
	 */
	public void unsubscribeRoom();

	/**
	 * 入住
	 */
	public void checkInRoom();

	/**
	 * 退房
	 */
	public void checkOutRoom();
}