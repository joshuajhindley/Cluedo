package cluedo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import cards.Card;
import cards.CharacterCard;
import cards.Room;
import cards.RoomCard;
import cards.Weapon;
import cards.WeaponCard;
import cells.Cell;
import cells.DoorCell;
import cells.RoomCell;

/**
 * A player in the game.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public class Player {
	
	/**
	 * The character that this player is playing as.
	 */
	private Character myCharacter;
	
	/**
	 * The cards in this player's hand.
	 */
	private List<Card> myCards = new ArrayList<Card>();
	
	/**
	 * Whether the player has been eliminated from the game or not.
	 */
	private boolean isNotEliminated = true;
	
	/**
	 * The cell the player's character is in.
	 */
	private Cell myLocation;
	
	/**
	 * The player's notes.
	 */
	private String notes = "Notes:\n";
	
	/**
	 * The player's name.
	 */
	private String playerName;

	/**
	 * Creates a new player.
	 * @param c The character this player is playing as.
	 * @param playerName This player's name.
	 */
	public Player(Character c, String playerName) {
		this.myCharacter = c;
		this.playerName = playerName;
	}
	
	/**
	 * @return This player's character.
	 */
	public Character getCharacter() {
		return this.myCharacter;
	}
	
	/**
	 * @return This player's name.
	 */
	public String getName() {
		return this.playerName;
	}
	 
	/**
	 * Adds a card to this player's hand then orders the hand.
	 * @param c The card to add.
	 */
	public void addCard(Card c) {
		this.myCards.add(c);
		//orders the cards as (L->R) weapons, rooms, characters.
		Collections.sort(this.myCards, new Comparator<Card>() {
			public int compare(Card c1, Card c2) {
				if (c1 instanceof CharacterCard || (c1 instanceof RoomCard && c2 instanceof WeaponCard))
					return -1;
				if(c1.getClass() == c2.getClass())
					return 0;
				else
					return 1;
			}
		});
	}
	
	/**
	 * @return This player's notes.
	 */
	public String getNotes() {
		return this.notes;
	}
	
	/**
	 * Updates this player's notes.
	 * @param notes The updated notes.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * @return An unmodifiableList of this player's cards.
	 */
	public List<Card> getHand() {
		return Collections.unmodifiableList(this.myCards);
	}
	
	/**
	 * Sets this player's location.
	 * @param c	The cell to move this player to.
	 */
	public void setLocation(Cell c) {
		this.myLocation = c;
	}
	
	/**
	 * @return The cell where this player is located.
	 */
	public Cell getLocation() {
		return this.myLocation;
	}
	
	/**
	 * @return If this player is eliminated (true) or still in the game (false).
	 */
	public boolean isEliminated() {
		return !this.isNotEliminated;
	}

	/**
	 * Eliminate this player from the game.
	 */
	public void eliminate() {
		this.isNotEliminated = false;
	}
	
	@Override
	public String toString() {
		return this.myCharacter.toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Player))
			return false;
		Player p = (Player) other;
		if(p.myCharacter == this.myCharacter && p.playerName == this.playerName)
			return true;
		return false;
	}

	/**
	 * Determines whether a move is possible.
	 * @param board	The board the player is on.
	 * @param goalCell	The cell the player wants to move to.
	 * @param diceRoll	The dice roll that the player got.
	 * @return A stack containing cells that form the route
	 * 			to the goal cell, or null if the route is impossible.
	 */
	public Stack<Cell> doMove(Board board, Cell goalCell, int diceRoll) {

		Cell previousLocation = this.myLocation;
		List<DoorCell> doorCells = null;
		
		//if the player clicked on a roomCell, try to find 
		//a route to all the doorCells into that room
		int numTries = 1;
		if (goalCell instanceof RoomCell) {
			doorCells = board.getDoors(((RoomCell) goalCell).getRoom());
			numTries = doorCells.size();
		}
		
		//make sure player not trying to move back into the same room
		if (goalCell instanceof RoomCell && this.myLocation instanceof RoomCell)
			if (((RoomCell) goalCell).getRoom() == ((RoomCell) this.myLocation).getRoom())
				return null;
		else if(goalCell instanceof DoorCell && this.myLocation instanceof RoomCell)
			if(((DoorCell) goalCell).getRoom() == ((RoomCell) this.myLocation).getRoom())
				return null;
		
		for (int i = 0; i < numTries; i++) {
			board.resetCells();
			
			//if the player is in a room move them to the door closest to their goal
			if (this.myLocation instanceof RoomCell) {
				List<DoorCell> doors = board.getDoors(((RoomCell) this.myLocation).getRoom());
				DoorCell bestDoor = doors.get(0);
				for (DoorCell d : doors) {
					if ((Math.abs(d.getX() - goalCell.getX()) + Math.abs(d.getY() - goalCell.getY())) < 
							(Math.abs(bestDoor.getX() - goalCell.getX()) + Math.abs(bestDoor.getY() - goalCell.getY())))
						bestDoor = d;
				}
				this.myLocation = bestDoor;
			}
			//if the player wants to move into a room
			//set the goalCell to the next doorCell
			if (doorCells != null)
				goalCell = doorCells.get(i);

			//if a route is found
			if (new Route(this.myLocation, goalCell, board, diceRoll).findRoute() != null) {
				//if the new cell is a DoorCell, set the newLocation to a cell in the room
				RoomCell newLocation = null;
				if (goalCell instanceof DoorCell) {
					newLocation = board.getDefaultCell(((DoorCell) board.getCell(goalCell.getX(), goalCell.getY())).getRoom());
					while (true) {
						if (newLocation.getPlayerHere() != null)
							newLocation = (RoomCell) board.getCell(newLocation.getX() + 1, newLocation.getY());
						else
							break;
					}
					newLocation.setPrev(goalCell);
				}
				Stack<Cell> route = new Stack<Cell>();
				
				//add the finalCell to the route
				if (newLocation != null)
					route.push(newLocation);
				else
					route.push(goalCell);
				
				//add cells to the route until the initial cell is added
				while (route.peek().getPrev() != null)
					route.push(route.peek().getPrev());
				
				return route;
			}
		}
		//no route found
		this.myLocation = previousLocation;
		return null;
	}

	
	/**
	 * Performs a suggestion of a room, character and weapon.
	 * @param players	The players that have cards.
	 * @param board	The board the players are on.
	 * @param c	The character to suggest.
	 * @param w	The weapon to suggest.
	 * @param parent	The parent component (i.e. where to center dialogs).
	 * @return A string consisting of the part of the suggestion refuted and by who 
	 * 			or "No one can refute that suggestion." if no one can refute the suggestion.
	 */
	public String doSuggest(ArrayList<Player> players, Board board, Character c, Weapon w, JFrame parent) {
		
		boolean playerAnswered = false;
		
		//moves the player with the suggested character to the suggested room, if such a player exists
		for (Player player : players) {	
			if (player.getCharacter() == c) {
				if (player.getLocation() instanceof RoomCell)
					if (((RoomCell) player.getLocation()).getRoom() == ((RoomCell) this.myLocation).getRoom())
						break;
				RoomCell newLocation = board.getDefaultCell(((RoomCell) this.myLocation).getRoom());
				while (true) {
					if (newLocation.getPlayerHere() != null)
						newLocation = (RoomCell) board.getCell(newLocation.getX() + 1, newLocation.getY());
					else
						break;
				}
				player.myLocation.setPlayerHere(null);
				player.myLocation = newLocation;
				newLocation.setPlayerHere(player);
				break;
			}
		}
		//asks all players other than this player to refute the suggestion 
		for (Player player : players) {
			if (!playerAnswered && !this.equals(player)) {
				//asks the player
				String asked = player.getAsked(w, c, ((RoomCell) this.getLocation()).getRoom(), parent);
				if (asked != null) {
					return asked;
				}
			}
		}
		//no other player had cards to refute the suggestion
		return "No one can refute that suggestion.";
	}
	
	/**
	 * Asks the player to select a card in their hand to refute the
	 * suggested weapon, character and room.
	 * @param w	The suggested weapon.
	 * @param c	The suggested character.
	 * @param r	The suggested room.
	 * @param parent	The parent component (i.e. where to center dialogs).
	 * @return A string consisting of this player's name and the refutation 
	 * 			or null if the suggestion could not be refuted.
	 */
	private String getAsked(Weapon w, Character c, Room r, JFrame parent) {
		
		boolean hasWeapon = false;
		boolean hasCharacter = false;
		boolean hasRoom = false;
		String selectedString = "### ERROR ###";
		List<String> values = new ArrayList<String>();
		
		//checks the players hand for any of the suggested cards
		for (Card card : this.myCards) {
			if (card instanceof RoomCard) {
				if (((RoomCard) card).room == r) 
					hasRoom = true;
			} else if (card instanceof CharacterCard) {
				if (((CharacterCard) card).thisCharacter == c)  
					hasCharacter = true;  
			} else if (card instanceof WeaponCard) {
				if (((WeaponCard) card).weapon == w)  
					hasWeapon = true; 
			}
		}
		//the player cannot refute the suggestion
		if (!hasWeapon && !hasCharacter && !hasRoom) {
			return null;
		}

		JOptionPane.showMessageDialog(parent, "Please pass the game to " + this.playerName);
		//adds the potential refutations to the values
		if (hasWeapon) {
			values.add(w.toString());
		}
		if (hasCharacter) {
			values.add(c.toString());
		}
		if (hasRoom) {
			values.add(r.toString());
		}
		//force the user to choose a response
		while (true) {
			Object selected = JOptionPane.showInputDialog(parent, "Choose your response:", this.toString() + ":", JOptionPane.DEFAULT_OPTION, null, values.toArray(), "0");
			if (selected != null){
			    selectedString = selected.toString();
			    break;
			}
		}
		return this.playerName + " has the " + selectedString + " Card";	
	}
	
	/**
	 * Perform an accusation.
	 * @param players	The players in the game.
	 * @param board	The board the players are on.
	 * @param w	The accused murder weapon.
	 * @param c	The accused murderer.
	 * @param r	The accused murder room.
	 * @param murderer	The actual murderer.
	 * @param murderRoom	The actual murder room.
	 * @param murderWeapon	The actual murder weapon.
	 * @return Whether the accusation was correct (true, player wins, game over)
	 * 			or incorrect (false, player is eliminated).
	 */
	public boolean doAccuse(ArrayList<Player> players, Board board, Weapon w, Character c, Room r, CharacterCard murderer, RoomCard murderRoom, WeaponCard murderWeapon) {
		
		//correct guess, game over
		if (murderer.thisCharacter == c && murderRoom.room == r && murderWeapon.weapon == w) 
			return true;
		
		//incorrect guess, game continues
		//moves the accused player to the room, if the player exists and is not already in the room
		for (Player player : players) {
			if (player.getCharacter() == c) { 	
				if (player.getLocation() instanceof RoomCell)
					if (((RoomCell) player.getLocation()).getRoom() == r)
						break;
				RoomCell newLocation = board.getDefaultCell(r);
				while (true) {
					if (newLocation.getPlayerHere() != null)
						newLocation = (RoomCell) board.getCell(newLocation.getX() + 1, newLocation.getY());
					else
						break;
				}
				player.myLocation.setPlayerHere(null);
				player.myLocation = newLocation;
				newLocation.setPlayerHere(player);
				break;
			}
		}
			return false;
	}
}