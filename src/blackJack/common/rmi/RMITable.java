package blackJack.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMITable extends Remote {

	public long getID() throws RemoteException;

	public String show() throws RemoteException;

	public String getDescription() throws RemoteException;

	public void connect(long playerID) throws RemoteException;
	public void disconnect(long playerID) throws RemoteException;
}
