package status;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 远程代理接口
 * 
 * @author 杨弢
 * 
 */
public interface RoomRemote extends Remote {
	public int getCount() throws RemoteException;

	public String getLocaton() throws RemoteException;

	public State getState() throws RemoteException;
}