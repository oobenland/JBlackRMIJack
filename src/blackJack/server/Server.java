package blackJack.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import blackJack.common.player.Player;
import blackJack.common.rmi.RMIPlayer;
import blackJack.common.rmi.RMIServer;
import blackJack.common.rmi.RMITable;

public class Server implements RMIServer {
	public final static HashMap<Long, Player> players = new HashMap<Long, Player>();
	public final static HashMap<Long, Table> tables = new HashMap<Long, Table>();

	private Server() throws RemoteException {
		super();
	}

	@Override
	public RMIPlayer createPlayer(String name) throws RemoteException {
		Player player = new Player(name);
		players.put(player.getID(), player);
		return (RMIPlayer) UnicastRemoteObject.exportObject(player, 0);
	}

	@Override
	public RMITable createTable(String desciption) throws RemoteException {
		System.out.println("New table: " + desciption);
		Table table = new Table(desciption);
		new Thread(table).start();
		tables.put(table.getID(), table);
		return (RMITable) UnicastRemoteObject.exportObject(table, 0);
	}

	@Override
	public List<RMITable> getTables() throws RemoteException {
		return new ArrayList<RMITable>(tables.values());
	}

	public static void main(String[] args) throws RemoteException {
		if (args.length != 1) {
			System.out.println("Usage: Server [port]");
			System.exit(0);
		}
		Registry registry = LocateRegistry.createRegistry(Integer.parseInt(args[0]));
		Remote server = (Remote) UnicastRemoteObject.exportObject(new Server(), 0);
		registry.rebind("blackjack", server);

	}
}
