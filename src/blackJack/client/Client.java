package blackJack.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import blackJack.common.rmi.RMIPlayer;
import blackJack.common.rmi.RMIServer;
import blackJack.common.rmi.RMITable;

public class Client {
	public final static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	RMIPlayer player;
	RMITable table;

	public Client(RMIPlayer player, RMITable table) {
		super();
		this.player = player;
		this.table = table;
	}

	private void sleepAWhile() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void showDisplay(String footer, int numberOfLinesOfFooter) throws Exception {
		int numberOfLinesOfDisplay = 0;
		String display = this.table.show();
		int index = display.indexOf('\n');
		while (index != -1) {
			numberOfLinesOfDisplay++;
			index = display.indexOf('\n', index + 1);
			System.out.println(index);
		}
		int numberOfLinesToAdd = (24 - numberOfLinesOfDisplay) - numberOfLinesOfFooter;
		numberOfLinesToAdd = Math.max(0, numberOfLinesToAdd);
		System.out.print(display);
		System.out.print(new String(new char[numberOfLinesToAdd]).replace('\0', '\n'));
		System.out.print(footer);
	}

	public void run() {
		try {
			while (true) {
				do {
					sleepAWhile();
					if (player.shouldUpdate()) {
						showDisplay("", 1);
					}
				} while (!player.isMyTurn());
				String line = null;
				do {
					showDisplay("=== My Turn! ===========\nTake a card [Y/n]: ", 2);
					line = reader.readLine();
					if (line.length() == 1 && line.toLowerCase().charAt(0) == 'q') {
						this.table.disconnect(player.getID());
						System.exit(0);
					} else if (line.length() == 1 && line.toLowerCase().charAt(0) == 'n') {
						player.doStand();
						break;
					} else if (line.isEmpty() || line.toLowerCase().charAt(0) == 'y') {
						player.doHit();
						break;
					}
				} while (true);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws RemoteException, NotBoundException, IOException {
		if (args.length != 2) {
			System.out.println("Usage: Client [url] [port]");
			System.out.println("You should use a 80x24 shell.");
			System.exit(0);
		}
		showSplashScreen();
		Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
		RMIServer server = (RMIServer) registry.lookup("blackjack");

		if (server == null) {
			System.err.println("Connection faild.");
			return;
		}
		System.out.println("Connected to server.");
		System.out.println("====================");
		String name = "";
		while (name.isEmpty()) {
			System.out.print("Your name: ");
			name = reader.readLine();
		}
		RMIPlayer player = server.createPlayer(name);
		RMITable table = null;

		List<RMITable> tables = server.getTables();
		if (tables.size() > 0) {
			System.out.println("Choose Table:");
			for (int i = 0; i < tables.size(); i++) {
				System.out.format("%3d: %s\n", i + 1, tables.get(i).getDescription());
			}
		}
		System.out.println("c: Create new table");
		System.out.println("q: Quit");

		while (true) {
			System.out.print("> ");
			String input = reader.readLine();
			if (input.contains("q")) {
				return;
			} else if (input.contains("c")) {
				name = "";
				while (name.isEmpty()) {
					System.out.print("A name for the table: ");
					name = reader.readLine();
				}
				table = server.createTable(name);
				break;
			} else if (tables.size() > 0) {
				try {
					int index = Integer.parseInt(input) - 1;
					if (index >= 0 && index < tables.size()) {
						table = tables.get(index);
						break;
					}
				} catch (NumberFormatException e) {
					// ignore..
				}
			}
		}
		table.connect(player.getID());

		final RMITable finalTable = table;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					finalTable.disconnect(player.getID());
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		new Client(player, table).run();
	}
	
	public static void showSplashScreen() throws IOException {
		System.out.println();
		System.out.println("                                       @                    ");
		System.out.println("                                      @@@@                  ");
		System.out.println("                                    @@@@@@@                 ");
		System.out.println("                                   @@@@@@@@@                ");
		System.out.println("                                 @@@@@@@@@@@@@              ");
		System.out.println("                               @@@@@@@@@@@@@@@@@            ");
		System.out.println("                             @@@@@@@@@@@@@@@@@@@@@          ");
		System.out.println("                           @@@@@@@@@@@@@@@@@@@@@@@@@        ");
		System.out.println("                          @@@@@@@@@@@@@@@@@@@@@@@@@@@@      ");
		System.out.println("                        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@     ");
		System.out.println("                        @@@@@@@@@@@@ BLACK @@@@@@@@@@@@     ");
		System.out.println("                       @@@@@@@@@@@@@ JACK  @@@@@@@@@@@@@    ");
		System.out.println("                       @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@     ");
		System.out.println("                        @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@     ");
		System.out.println("                         @@@@@@@@@@@@@@@@@@@@@@@@@@@@       ");
		System.out.println("                            @@@@@@@@  @@@   @@@@@@          ");
		System.out.println("                                     @@@@@                  ");
		System.out.println("                                    @@@@@@@                 ");
		System.out.println();
		System.out.println("               ~~ What happens in Las Vegas stays on Facebook ~~");
		System.out.println();
		System.out.println("To play this game you only need [Enter] and [n]. Press [Enter] to start.");
		reader.readLine();
	}
}
