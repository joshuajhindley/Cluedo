package cells;

import cluedo.Player;

/**
 * A interface to represent the different cells on the board.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public interface Cell {
			
	/**
	 * @return The x-value of this cell on the board.
	 */
	public int getX();
	
	/**
	 * @return The y-value of this cell on the board.
	 */
	public int getY();
	
	/**
	 * @return If the cell has been visited while searching for a route.
	 */
	public default boolean isVisited() {
		return false;
	}
	
	/**
	 * Set whether a cell has been visited when searching for a route.
	 * @param b	Whether to visit (true) or unvisit (false) the cell.
	 */
	public default void setVisited(boolean b) {}
	
	/**
	 * Moves a player to this cell.
	 * @param p	The player to move here.
	 */
	public default void setPlayerHere(Player p) {}
	
	/**
	 * @return The player here, if a player is here, otherwise null.
	 */
	public default Player getPlayerHere() {
		return null;
	}

	/**
	 * Sets the cell before this one in the route.
	 * @param prev	The cell before this one.
	 */
	public default void setPrev(Cell prev) {}
	
	/**
	 * @return The cell previous to this one in the route.
	 */
	public default Cell getPrev() {
		return null;
	}
}
