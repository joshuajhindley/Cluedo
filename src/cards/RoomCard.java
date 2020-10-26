package cards;

/**
 * A room card that a player may possess.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public class RoomCard implements Card {
	
	/**
	 * The room of this card.
	 */
	public final Room room;
	
	/**
	 * Creates a new room card to represent the given room.
	 * @param r The room this card represents.
	 */
	public RoomCard(Room r) {
		room = r;
	}
	
	@Override
	public String toString() {
		return room.toString();
	}
}
