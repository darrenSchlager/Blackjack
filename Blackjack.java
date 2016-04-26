import java.util.*;

public class Blackjack {

	public static void main(String[] args) {
		
		Scanner keyboard = new Scanner(System.in);
		BlackjackGame game = new BlackjackGame(keyboard);
		boolean gameOver = false;
		System.out.print("=====================\n"+
							"Welcome to Blackjack!\n"+
							"=====================\n"+
							"=>   Face cards (king, queen, and jack) are worth 10 points\n"+
							"=>   An ace is worth 1 point or 11 points\n"+
							"=>   All other cards are worth their numeric value\n"+
							"=>   21 on the first two cards is called Blackjack and it beats all other hands\n"+
							"=>   You win if your hand beats the dealers, without going over 21\n"+
							"=>   No one wins on a tie\n"+
							"=>   If you have less than 21,\n"+
							"     you may draw another card (Hit),\n"+
							"     or end your turn (Stand)\n"+
							"=>   The dealer will not hit if holding 17 or above\n\nPress return/enter to begin.");
		keyboard.nextLine();
		
		do {
			int[] scores;
			
			System.out.print("\nDealing Cards ");
			for(int i=0; i<4; i++) {
				System.out.print(".");
				wait(125);
			}
			System.out.println("\n");
			
			boolean playerHasBlackjack = game.executePlayersTurn();
			scores = game.getScores();
			if(playerHasBlackjack && scores[0]!=21)
				System.out.println("\nBlackjack! You Win!");
			else if(playerHasBlackjack && scores[0]==21)
				System.out.println("\nPush. You tied the dealer.");
			else if(scores[1]>21)
				System.out.println("\nBust. You Lose.");
			else {
				
				System.out.print("\nDealers turn ");
				for(int i=0; i<4; i++) {
					System.out.print(".");
					wait(125);
				}
				System.out.println("\n");
				
				boolean dealerHasBlackjack = game.executeDealersTurn();
				scores = game.getScores();
				if(dealerHasBlackjack)
					System.out.println("\nYou Lose. The dealer has Blackjack.");
				else if(scores[0]==scores[1])
					System.out.println("\nPush. You tied the dealer.");
				else if(scores[1]>21)
					System.out.println("\nBust. You Lose.");
				else if(scores[0]>21 || scores[1]>scores[0])
					System.out.println("\nYou Win! You beat the dealer.");
				else
					System.out.println("\nYou Lose. The dealer beat you.");
			}

			System.out.print("Would you like to play again? ");
			String response = keyboard.nextLine();
			if(!response.matches("(?i)^yes|yea|yeah|y|ok|okay|sure$"))
				gameOver = true;
			else
				game.newGame();
		} while(!gameOver);
		
		System.out.println("\nThanks for playing!");
	}

	public static void wait(int millis){
		long startTime = System.currentTimeMillis();
		long currentTime;
		do {
			currentTime = System.currentTimeMillis();
		} while (currentTime - startTime < millis);
	}
}

class BlackjackGame {
	private Deck deck = new Deck();
	private BlackjackHand dealer;
	private BlackjackHand player;
	private Scanner keyboard;


	public BlackjackGame(Scanner keyboard) {
		this.keyboard = keyboard;
		newGame();
	}

	public void newGame() {
		dealer = new BlackjackHand();
		player = new BlackjackHand();
		
		deck.shuffleDeck();
		for(int i=0; i<2; i++) {
			player.addCard(deck);
			dealer.addCard(deck);
		}
	}

	public boolean executePlayersTurn() {
		if(!player.hasBlackjack() && deck.hasMoreCards()) {
			boolean done = false;
			printHands();
			while(player.getHandValue()<21 && !done) {
				String response = "";
				System.out.println();
				do {
					System.out.print("Hit or Stand? ");
					response = keyboard.nextLine();
				} while(!response.matches("(?i)^hit|h|stand|s$"));
				
				if(!response.matches("(?i)^hit|h$") || !deck.hasMoreCards())
					done = true;
				else {
					player.addCard(deck);
					System.out.println();
					if( player.getHandValue()>21 || player.hasBlackjack() )
						printFinalHands();
					else
						printHands();
				}
			}
			return player.hasBlackjack();
		}
		else {
			printFinalHands();
			return player.hasBlackjack();
		}
	}

	public boolean executeDealersTurn() {
		while(dealer.getHandValue()<player.getHandValue() && dealer.getHandValue()<17 && deck.hasMoreCards())
			dealer.addCard(deck);
		printFinalHands();
		return dealer.hasBlackjack();
	}

	public int[] getScores() {
		return new int[]{dealer.getHandValue(), player.getHandValue()};
	}

	private void printFinalHands() {
		System.out.println("Dealer's Hand : "+dealer.getCardString());
		System.out.println("Value         : "+dealer.getHandValue());
		System.out.println("Your Hand     : "+player.getCardString());
		System.out.println("Value         : "+player.getHandValue());
	}

	private void printHands() {
		System.out.println("Dealer's Hand : "+dealer.getDealerCardString());
		System.out.println("Your Hand     : "+player.getCardString());
		System.out.println("Value         : "+player.getHandValue());
	}
}

class BlackjackHand {
	private List<Card> cards = new ArrayList<Card>();
	private String cardString;
	private String dealerCardString;
	private int handValue;
	private boolean hasBlackjack;

	public BlackjackHand() {
		cardString = "";
		dealerCardString = "";
		handValue = 0;
		hasBlackjack = false;
	}

	public boolean addCard(Deck deck) {
		if(deck.hasMoreCards()) {
			Card card = deck.getCard();
			cards.add(card);
			
			cardString += card.getName()+" ";
			if(cards.size()==1)
				dealerCardString += "[] ";
			else
				dealerCardString += card.getName();
				
			setHandValueAndHasBlackjack();
			return true;
		}
		else
			return false;
	}

	private void setHandValueAndHasBlackjack() {
		int numAces = 0;
		handValue = 0;

		for(Card card : cards) {
			int rank = card.getRank();

			if(rank>10 && rank<14)
				handValue+= 10;
			else if(rank==14) {
				handValue+= 11;
				numAces++;
			}
			else
				handValue += rank;
		}
		while(handValue>21 && numAces>0) {
			handValue-=10;
			numAces--;
		}

		if(handValue==21 && cards.size()==2)
			hasBlackjack = true;
	}

	public String getCardString() {
		return cardString;
	}

	public String getDealerCardString() {
		return dealerCardString;
	}

	public int getHandValue() {
		return handValue;
	}

	public boolean hasBlackjack() {
		return hasBlackjack;
	}
}

class Deck {
	private final int DECK_SIZE = 52;
	private Card[] cards = new Card[DECK_SIZE];
	private Random random = new Random();
	private int deckPosition = 0;

	public Deck() {
		for(int i=0; i<DECK_SIZE; i++) {
			if(i/4==0) cards[i] = new Card("two");
			if(i/4==1) cards[i] = new Card("three");
			if(i/4==2) cards[i] = new Card("four");
			if(i/4==3) cards[i] = new Card("five");
			if(i/4==4) cards[i] = new Card("six");
			if(i/4==5) cards[i] = new Card("seven");
			if(i/4==6) cards[i] = new Card("eight");
			if(i/4==7) cards[i] = new Card("nine");
			if(i/4==8) cards[i] = new Card("ten");
			if(i/4==9) cards[i] = new Card("jack");
			if(i/4==10) cards[i] = new Card("queen");
			if(i/4==11) cards[i] = new Card("king");
			if(i/4==12) cards[i] = new Card("ace");
		}
	}

	public void shuffleDeck() {
		Card[] temp = new Card[DECK_SIZE];
		for(int i=0; i<DECK_SIZE;) {
			int position = random.nextInt(DECK_SIZE);
			if(temp[position]==null) {
				temp[position] = cards[i];
				i++;
			}
		}
		cards = temp;
		deckPosition = 0;
	}

	public boolean hasMoreCards() {
		return deckPosition<DECK_SIZE;
	}

	public Card getCard() {
		if(deckPosition<DECK_SIZE)
			return cards[deckPosition++];
		else
			return null;
	}
}

class Card {
	private String name="";
	private int rank=0;

	public Card(String name) {
		this.name = name.toLowerCase();
		setRank();

	}

	private void setRank() {
		if(name.equals("two")) rank = 2;
		else if(name.equals("three")) rank = 3;
		else if(name.equals("four")) rank = 4;
		else if(name.equals("five")) rank = 5;
		else if(name.equals("six")) rank = 6;
		else if(name.equals("seven")) rank = 7;
		else if(name.equals("eight")) rank = 8;
		else if(name.equals("nine")) rank = 9;
		else if(name.equals("ten")) rank = 10;
		else if(name.equals("jack"))  rank = 11;
		else if(name.equals("queen")) rank = 12;
		else if(name.equals("king")) rank = 13;
		else if(name.equals("ace")) rank = 14;
	}

	public int getRank() {
		return rank;
	}

	public String getName() {
		return name;
	}

	public int compareTo(Card card) {
		if(rank<card.rank)
			return -1;
		else if(rank>card.rank)
			return 1;
		else
			return 0;
	}
}
