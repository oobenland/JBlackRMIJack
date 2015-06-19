package blackJack.common.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import blackJack.common.cards.Card;
import blackJack.common.cards.Card.Label;
import blackJack.common.rmi.RMIPerson;
import blackJack.server.Table;

public abstract class Person implements RMIPerson, Serializable {
	private static final long serialVersionUID = 1L;

	private Table currentTable;
	long score = 0;
	
	// Access to cards must be synchronized!
	protected List<Card> cards = new ArrayList<Card>();

	public synchronized void addCard(Card card) {
		cards.add(card);
	}

	public Table getCurrentTable() {
		return currentTable;
	}

	public void setCurrentTable(Table currentTable) {
		this.currentTable = currentTable;
	}

	public void didWin() {
		++score;
	}
	
	public boolean didBust() {
		return getCardCount() > 21;
	}
	
	/**
	 * A Black Jack is an ace and a card with value greater equals 10.
	 * A player with a Black Jack wins.
	 * 
	 * @return
	 */
	public synchronized boolean hasBlackJack() {
		if (cards.size() > 2) {
			return false;
		}
		boolean hasAce = false;
		boolean has10OrHigher = false;
		for (Card card : cards) {
			hasAce |= (card.label == Label.Ace);
			has10OrHigher |= (card.label.labelValue >= 10);
		}
		return hasAce && has10OrHigher;
	}

	public synchronized int getCardCount() {
		// Sort cards to get aces at last.
		cards.sort(new Comparator<Card>() {
			@Override
			public int compare(Card o1, Card o2) {
				return o1.label.ordinal() - o2.label.ordinal();
			}
		});

		int numberOfAces = 0;
		int count = 0;

		// count all execpt of aces. Aces need specal treatment...
		for (Card card : cards) {
			if (card.label.ordinal() <= Label.Ten.ordinal()) {
				count += card.label.labelValue;
			} else if (card.label != Label.Ace) {
				count += 10;
			} else {
				numberOfAces++;
			}
		}

		// Try to get as many points with the aces
		int[] acesCounts = getAcesCounts(numberOfAces);
		int finalCount = count;
		for (int i = 0; i < acesCounts.length; i++) {
			finalCount = count + acesCounts[i];
			if (finalCount == 21) {
				break;
			} else if (finalCount > 21 && i != 0) {
				finalCount = count + acesCounts[i - 1];
				break;
			}
		}

		return finalCount;
	}

	private int[] getAcesCounts(int numberOfAces) {
		int[] counts = new int[numberOfAces * 2];
		for (int i = 0; i < counts.length; i++) {
			counts[i] = (numberOfAces - i) + (i * 11);
		}
		return counts;
	}

	public synchronized void resetToStartNextRound() {
		cards.clear();
	}

	public abstract boolean isAllowedToTakeACard();

	public abstract String show();
}
