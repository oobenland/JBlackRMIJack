package blackJack.common.player;

public class Dealer extends Person {
	private static final long serialVersionUID = 1L;
	
	@Override
	public boolean isAllowedToTakeACard() {
		return getCardCount() < 17;
	}
	
	@Override
	public String show() {
		return String.format("(%3s)   %15s: %s -> %d", score, "Dealer", cards.toString(), getCardCount());
	}

}
