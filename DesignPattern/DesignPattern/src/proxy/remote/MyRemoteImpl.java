package proxy.remote;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MyRemoteImpl extends UnicastRemoteObject implements MyRemote {

	public MyRemoteImpl() throws RemoteException {

	}

	@Override
	public String sayHello() throws RemoteException {
		return "Server says, 'Hey'";
	}

	public static void main(String[] args) {
		try {
			MyRemote myRemote = new MyRemoteImpl();
			Naming.bind("RemoteHello", myRemote);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
	}
}