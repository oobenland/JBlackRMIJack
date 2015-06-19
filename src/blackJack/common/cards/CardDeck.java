package blackJack.common.cards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import blackJack.common.cards.Card.Label;
import blackJack.common.cards.Card.Suit;

public class CardDeck implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private List<Card> cards = new ArrayList<Card>();

	public static CardDeck createFrenchDeck() {
		CardDeck deck = new CardDeck();
		addCardsWithSuit(deck, Suit.Clover);
		addCardsWithSuit(deck, Suit.Heart);
		addCardsWithSuit(deck, Suit.Pike);
		addCardsWithSuit(deck, Suit.Tile);
		return deck;
	}

	private static void addCardsWithSuit(CardDeck deck, Suit suit) {
		deck.cards.add(new Card(suit, Label.Two));
		deck.cards.add(new Card(suit, Label.Three));
		deck.cards.add(new Card(suit, Label.Four));
		deck.cards.add(new Card(suit, Label.Five));
		deck.cards.add(new Card(suit, Label.Six));
		deck.cards.add(new Card(suit, Label.Seven));
		deck.cards.add(new Card(suit, Label.Eight));
		deck.cards.add(new Card(suit, Label.Nine));
		deck.cards.add(new Card(suit, Label.Ten));
		deck.cards.add(new Card(suit, Label.Jack));
		deck.cards.add(new Card(suit, Label.Knight));
		deck.cards.add(new Card(suit, Label.Queen));
		deck.cards.add(new Card(suit, Label.Ace));
	}

	private CardDeck() {}

	public CardDeck(CardDeck... decks) {
		for (CardDeck cardDeck : decks) {
			cards.addAll(cardDeck.cards);
		}
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}
	
	public int size() {
		return cards.size();
	}

	public Card draw() {
		if (isEmpty()) {
			return null;
		}
		// Remove the last card to avoid arraycopy!
		return cards.remove(cards.size() - 1);
	}

	public void shuffle() {
		Collections.shuffle(cards);
	}

	@Override
	public String toString() {
		return "<CardDeck " + cards.size() + " cards: " + cards.toString() + ">";
	}

	public void addAll(CardDeck createCardDeck) {
		List<Card> currentCards = new ArrayList<Card>(this.cards);
		this.cards = new ArrayList<Card>(createCardDeck.cards);
		this.cards.addAll(currentCards);
	}
}
