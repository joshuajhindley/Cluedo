package cells;
import cards.Room;
import cluedo.Player;

/**
 * A room cell. That is, a cell on the board that is within one of the rooms.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public class RoomCell implements Cell {
	
	/**
	 * The room that this cell is in.
	 */
	private Room room;
	
	/**
	 * The player on this cell.
	 */
	private Player playerOnMe;
	
	/**
	 * The previous cell before this one on the route.
	 */
	private Cell prev;
	
	/**
	 * The x-value of this cell on the board.
	 */
	private int x;
	
	/**
	 * The y-value of this cell on the board.
	 */
	private int y;
	
	/**
	 * Creates a new room cell.
	 * @param room	The room that this cell is part of.
	 * @param x	The x-value of this cell on the board.
	 * @param y	The y-value of this cell on the board.
	 */
	public RoomCell(Room room, int x, int y) {
		this.x = x; 
		this.y = y;
		this.room = room;
	}
	
	/**
	 * @return The room this cell belongs to.
	 */
	public Room getRoom() {
		return this.room;
	}
	
	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}
	
	@Override
	public void setPlayerHere(Player p) {
		this.playerOnMe = p;
	}
	
	@Override
	public Player getPlayerHere() {
		return this.playerOnMe;
	}
	
	@Override
	public void setPrev(Cell prev) {
		this.prev = prev;
	}
	
	@Override
	public Cell getPrev() {
		return this.prev;
	}
}
