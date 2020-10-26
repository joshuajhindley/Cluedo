package cluedo;
import cells.Cell;

/**
 * A fringe element. This class is used to assist in 
 * finding the best route from one cell to another.
 * 
 * @author Joshua Hindley
 */
public class FringeElement implements Comparable<FringeElement> {
	
	/**
	 * The cell previous to the one associated with this fringe element.
	 */
	private Cell prev;
	
	/**
	 * The cell associated with this fringe element.
	 */
	private Cell cell;
	
	/**
	 * The number of steps taken to get from the start cell to this cell.
	 */
	private int stepsSoFar;
	
	/**
	 * The minimum cost of getting from the starting cell to the goal cell through this cell.
	 */
	private int totalCost;
	
	/**
	 * Creates a new fringe element.
	 * @param cell	The cell associated with this fringe element.
	 * @param prev	The cell previous to the one associated with this fringe element.
	 * @param stepsSoFar	The number of steps taken to get from the start cell to cell.
	 * @param goal	The goal cell.
	 */
	public FringeElement(Cell cell, Cell prev, int stepsSoFar, Cell goal) {
		this.cell = cell;
		this.prev = prev;
		this.stepsSoFar = stepsSoFar;
		this.totalCost = this.stepsSoFar + Math.abs(cell.getX() - goal.getX()) + Math.abs(cell.getY() - goal.getY());
	}
	
	/**
	 * Compares this fringe element to another fringe element and returns 0, 1 or -1
	 * depending on whether this fringe element should go before or after the other fringe element.
	 */
	public int compareTo(FringeElement other) {
		if (this.totalCost > other.totalCost) //this one is worse than other
			return 1;
		else if (this.totalCost < other.totalCost) //this one is better than other
			return -1;
		else
			return 0;
	}
	
	/**
	 * @return The cell associated with this fringe element.
	 */
	public Cell getCell() {
		return this.cell;
	}
	
	/**
	 * @return The cell previous to the node associated with this fringe element.
	 */
	public Cell getPrev() {
		return this.prev;
	}
	
	/**
	 * @return The number of steps taken to get from the start cell to this cell.
	 */
	public int getStepsSoFar() {
		return this.stepsSoFar;
	}	
}