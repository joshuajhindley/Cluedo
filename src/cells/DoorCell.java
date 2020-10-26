package cells;

import cards.Room;
import cluedo.Player;

/**
 * A door cell. That is, a cell on the board that represents a door between a room and the hallway.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public class DoorCell implements Cell {
	
	/**
	 * Whether the door is vertical door.
	 * A vertical door is a door at a corner of a room that cannot be 
	 * entered from the side, and can only be entered from above or below.
	 */
	private boolean verticalDoor = false;
	
	/**
	 * The room that this door connects to.
	 */
	private Room room;
	
	/**
	 * The x-value of this cell on the board.
	 */
	private int x;
	
	/**
	 * The y-value of this cell on the board.
	 */
	private int y;
	
	/**
	 * The player on this cell, if there is such a player.
	 */
	private Player playerOnMe;
	
	/**
	 * Whether this cell has been visited while searching for a route.
	 */
	private boolean visited = false;
	
	/**
	 * The cell before this one on the route.
	 */
	private Cell prev;
	
	/**
	 * Creates a new door cell.
	 * @param b	Whether this door is vertical.
	 * @param x The x-value of this cell on the board.
	 * @param y The y-value of this cell on the board.
	 */
	public DoorCell(boolean b, int x, int y) {
		this.verticalDoor = b;
		this.x = x; 
		this.y = y;
	}
	
	/**
	 * Sets the room that this door belongs to.
	 * @param r	The room.
	 */
	public void setRoom(Room r) {
		this.room = r;
	}
	
	/**
	 * @return The room this door belongs to.
	 */
	public Room getRoom() {
		return this.room;
	}
	
	/**
	 * @return If this is a vertical door or a horizontal door.
	 */
	public boolean isVerticalDoor() {
		return this.verticalDoor;
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
	public boolean isVisited() {
		return this.visited;
	}
	
	@Override
	public void setVisited(boolean b) {
		this.visited = b;
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