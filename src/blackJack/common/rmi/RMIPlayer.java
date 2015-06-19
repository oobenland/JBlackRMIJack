package blackJack.common.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIPlayer extends Remote, RMIPerson {
	
	public long getID() throws RemoteException;
	public boolean shouldUpdate() throws RemoteException;
	
	/**
	 * Player takes a card.
	 * 
	 * @throws RemoteException
	 */
	public void doHit() throws RemoteException;

	/**
	 * Player dosn't want to have a card.
	 * @throws RemoteException
	 */
	public void doStand() throws RemoteException;

	/**
	 * Player tries to double down.
	 * @return true iff allowed to double down.
	 * @throws RemoteException
	 */
	public boolean doDoubleDown() throws RemoteException;

	/**
	 * Player tries to split cards.
	 * @return true iff allowed to split.
	 * @throws RemoteException
	 */
	public boolean doSplit() throws RemoteException;

	/**
	 * @return true iff this player is on turn.
	 * @throws RemoteException
	 */
	public boolean isMyTurn() throws RemoteException;
	
	/**
	 * @return The name of the player who is on turn.
	 * @throws RemoteException
	 */
	public String getNameOfCurrentPlayer() throws RemoteException;
}
