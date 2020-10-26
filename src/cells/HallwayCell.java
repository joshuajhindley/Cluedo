package cells;
import cluedo.Player;

/**
 * A hallway cell. That is, a cell on the board that represents a location in the hallway.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public class HallwayCell implements Cell {
	
	/**
	 * The player on this cell.
	 */
	private Player playerOnMe;
	
	/**
	 * The x-value of this cell on the board.
	 */
	private int x;
	
	/**
	 * The y-value of this cell on the board.
	 */
	private int y;
	
	/**
	 * Whether this cell has been visited while searching for a route.
	 */
	private boolean visited = false;
	
	/**
	 * The cell before this one on the route.
	 */
	private Cell prev;
	
	/**
	 * Creates a new hallway cell.
	 * @param x	The x-value of this cell on the board.
	 * @param y	The y-value of this cell on the board.
	 */
	public HallwayCell(int x, int y) {
		this.x = x; 
		this.y = y;
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
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
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
