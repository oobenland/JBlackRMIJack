package blackJack.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RMIServer extends Remote {
	public RMIPlayer createPlayer(String name) throws RemoteException;
	public RMITable createTable(String desciption) throws RemoteException;
	public List<RMITable> getTables() throws RemoteException;
}
