package cells;

/**
 * A null cell. These are cells that exist on the board, but are inaccessible.
 *
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public class NullCell implements Cell {
	
	/**
	 * The x-value of this cell on the board
	 */
	private int x;
	
	/**
	 * The y-value of this cell on the board
	 */
	private int y;
	
	/**
	 * Creates a new null cell.
	 * @param x	The x-value of this cell on the board.
	 * @param y	The y-value of this cell on the board.
	 */
	public NullCell(int x, int y) {
		this.x = x; 
		this.y = y;
	}
	
	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}
}
