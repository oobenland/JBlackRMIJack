package blackJack.common.player;

import java.rmi.RemoteException;

import blackJack.common.rmi.RMIPlayer;
import blackJack.server.Table;

public class Player extends Person implements RMIPlayer {
	private static long nextID = 0;
	private static final long serialVersionUID = 1L;

	private final long id;
	String name;
	Table table;
	boolean didHit = false;
	boolean didStand = false;
	boolean shouldUpdate = false;

	public Player(String name) {
		id = ++nextID;
		this.name = name;
	}

	public void setShouldUpdate(boolean shouldUpdate) {
		this.shouldUpdate = shouldUpdate;
	}

	/**
	 * The value will be reset to false after calling
	 * this method.
	 * 
	 * @return if the player should update the screen.
	 */
	public boolean shouldUpdate() throws RemoteException {
		boolean returnValue = shouldUpdate;
		shouldUpdate = false;
		return returnValue;
	}

	public boolean hasMoved() {
		return didHit || didStand;
	}

	/**
	 * The value will be reset to false after calling
	 * this method.
	 * 
	 * @return if the player wants a card.
	 */
	public boolean didHit() {
		boolean returnValue = didHit;
		didHit = false;
		return returnValue;
	}

	public boolean didStand() {
		return didStand;
	}

	@Override
	public long getID() throws RemoteException {
		return id;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public void resetDecision() {
		didHit = false;
	}

	public void resetToStartNextRound() {
		super.resetToStartNextRound();
		didHit = didStand = false;
	}

	@Override
	public void doHit() throws RemoteException {
		if (!isMyTurn()) {
			return;
		}
		didHit = true;
	}

	@Override
	public void doStand() throws RemoteException {
		if (!isMyTurn()) {
			return;
		}
		didStand = true;
	}

	@Override
	public boolean doDoubleDown() throws RemoteException {
		if (!isMyTurn()) {
			return false;
		}
		return false;
	}

	@Override
	public boolean doSplit() throws RemoteException {
		if (!isMyTurn()) {
			return false;
		}
		return false;
	}

	@Override
	public boolean isMyTurn() throws RemoteException {
		return table.getCurrentPlayer() == this;
	}

	@Override
	public String getNameOfCurrentPlayer() throws RemoteException {
		return table.getCurrentPlayer().getName();
	}

	@Override
	public boolean isAllowedToTakeACard() {
		return getCardCount() < 21 && !didStand;
	}

	@Override
	public String show() {
		String turn = table.getCurrentPlayer() == this ? ">" : " ";
		return String.format("(%3d) %s %15s: %s -> %d", score, turn, name, cards.toString(), getCardCount());
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "<Player '" + name + "'>";
	}
}
