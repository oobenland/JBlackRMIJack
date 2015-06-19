package blackJack.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import blackJack.common.cards.CardDeck;
import blackJack.common.player.Dealer;
import blackJack.common.player.Player;
import blackJack.common.rmi.RMITable;

public class Table implements RMITable, Runnable {
	private static long nextID = 0;

	private final long id;
	String desciption;
	Dealer dealer = new Dealer();
	List<Player> players = new ArrayList<Player>();
	List<Player> waitingPlayers = new ArrayList<Player>();
	List<Player> disconnectedPlayers = new ArrayList<Player>();
	Player currentPlayer = null;
	CardDeck cardDeck;

	public Table(String desciption) {
		id = ++nextID;
		this.desciption = desciption;
		this.cardDeck = createCardDeck();
	}

	private CardDeck createCardDeck() {
		CardDeck deck = new CardDeck(CardDeck.createFrenchDeck(), CardDeck.createFrenchDeck(), CardDeck.createFrenchDeck(), CardDeck.createFrenchDeck(),
				CardDeck.createFrenchDeck(), CardDeck.createFrenchDeck());
		deck.shuffle();
		return deck;
	}

	@Override
	public long getID() throws RemoteException {
		return id;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	@Override
	public void connect(long playerID) throws RemoteException {
		Player player = Server.players.get(playerID);
		player.setShouldUpdate(true);
		player.setTable(this);
		waitingPlayers.add(player);
		System.out.println("Player connected: " + player);
	}

	@Override
	public void disconnect(long playerID) throws RemoteException {
		Player player = Server.players.get(playerID);
		waitingPlayers.remove(player);
		disconnectedPlayers.add(player);

		// If player is on turn, we have to do something...
		player.doStand();
		System.out.println("Player disconnected: " + player);
	}

	private void playersShouldUpdate() {
		for (Player player : players) {
			player.setShouldUpdate(true);
		}
		for (Player player : waitingPlayers) {
			player.setShouldUpdate(true);
		}
	}

	private void giveAllPlayersOneCard() {
		for (Player player : players) {
			giveOneCardToPlayer(player);
		}
	}

	private void giveOneCardToPlayer(Player player) {
		player.addCard(cardDeck.draw());
	}

	private void giveOneCardToDealer() {
		dealer.addCard(cardDeck.draw());
	}

	private void letPlayerDoOneMove(Player player) {
		if (!player.isAllowedToTakeACard()) {
			return;
		}
		currentPlayer = player;
		System.out.println(player + "'s turn...");
		// Wait for remote player to do something...
		while (!player.hasMoved()) {
			waitAWhile();
		}
		if (player.didHit()) {
			giveOneCardToPlayer(player);
		}
		playersShouldUpdate();
		currentPlayer = null;
	}

	private void resetAllPlayersAndDealerForNextRound() {
		dealer.resetToStartNextRound();
		for (Player player : players) {
			player.resetToStartNextRound();
		}
	}

	private void addNewCardIfNeeded() {
		if (this.cardDeck.size() < 50) {
			this.cardDeck.addAll(createCardDeck());
		}
	}

	private void letPlayersMove() {
		boolean allPlayersDone = false;
		while (!allPlayersDone) {
			allPlayersDone = true;
			for (Player player : players) {
				letPlayerDoOneMove(player);
				allPlayersDone &= !player.isAllowedToTakeACard();
			}
		}
	}

	private void letDealerMove() {
		while (dealer.isAllowedToTakeACard()) {
			giveOneCardToDealer();
		}
	}
	
	private void showWhoWins() {
		int dealerCount = dealer.getCardCount();
		if (dealerCount == 21) {
			for (Player player : players) {
				if (player.hasBlackJack()) {
					System.out.println(player.getName() + " wins with black jack!");
					player.didWin();
				}
			}
			System.out.println("Dealer wins with 21!");
			dealer.didWin();
		} else if (!dealer.didBust()) {
			boolean didAPlayerWin = false;
			for (Player player : players) {
				if (!player.didBust() && player.getCardCount() > dealerCount) {
					System.out.println(player.getName() + " wins with more points than dealer!");
					player.didWin();
					didAPlayerWin = true;
				}
			}
			if (!didAPlayerWin) {
				System.out.println("Dealer wins with more points than players!");
				dealer.didWin();
			}
		} else {
			for (Player player : players) {
				if (!player.didBust()) {
					System.out.println(player.getName() + " wins with dealer busted!");
					player.didWin();
				}
			}
		}
	}

	public void run() {
		waitForPlayersFirstTime();

		while (true) {
			addNewCardIfNeeded();

			waitForPlayersToPlayWith();
			letLeaveDisconnectedPlayers();
			letJoinWatingPlayers();

			System.out.println("Players: " + players);
			resetAllPlayersAndDealerForNextRound();

			System.out.println("NEW ROUND!");
			giveAllPlayersOneCard();
			giveOneCardToDealer();
			giveAllPlayersOneCard();
			playersShouldUpdate();

			letPlayersMove();
			letDealerMove();
			showWhoWins();
			playersShouldUpdate();
			waitForNextRound();
			playersShouldUpdate();
		}
	}

	@Override
	public String show() throws RemoteException {
		StringBuilder builder = new StringBuilder();
		// ================================================================
		builder.append("==== Table (" + desciption + ") ====>\n");
		builder.append(dealer.show() + "\n\n");
		for (Player player : players) {
			builder.append(player.show() + "\n");
		}

		return builder.toString();
	}

	private void letJoinWatingPlayers() {
		players.addAll(waitingPlayers);
		waitingPlayers.clear();
	}

	private void letLeaveDisconnectedPlayers() {
		waitingPlayers.removeAll(disconnectedPlayers);
		players.removeAll(disconnectedPlayers);
	}

	private void waitForNextRound() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void waitForPlayersFirstTime() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void waitAWhile() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void waitForPlayersToPlayWith() {
		while (players.isEmpty() && waitingPlayers.isEmpty()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public String getDescription() throws RemoteException {
		return "<Table: " + desciption + " (" + players.size() + " Players)>";
	}

	@Override
	public String toString() {
		return "<Table: " + desciption + " (" + players.size() + " Players)>";
	}
}
