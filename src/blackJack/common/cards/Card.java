package blackJack.common.cards;

import java.io.Serializable;

public class Card implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public final Suit suit;
	public final Label label;

	public Card(Suit suit, Label label) {
		super();
		this.suit = suit;
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((suit == null) ? 0 : suit.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (label != other.label)
			return false;
		if (suit != other.suit)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.suit.image + this.label.image;
	}

	public static enum Suit {
		Heart((char) 9829), // Herz
		Tile((char) 9830), // Karro
		Clover((char) 9827), // Kreuz
		Pike((char) 9824); // Pik

		public final char image;

		private Suit(final char image) {
			this.image = image;
		}
	}

	public static enum Label {
		Two(2, "2"), Three(3, "3"), Four(4, "4"), Five(5, "5"), Six(6, "6"), Seven(7, "7"), Eight(8, "8"), Nine(9, "9"), Ten(10, "10"), Jack(11, "J"), Knight(
				12, "K"), Queen(13, "Q"), Ace(14, "A");

		public final int labelValue;
		public final String image;

		private Label(final int labelValue, final String image) {
			this.labelValue = labelValue;
			this.image = image;
		}
	}
}
