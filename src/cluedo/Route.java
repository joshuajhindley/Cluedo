package cluedo;

import java.util.PriorityQueue;

import cells.Cell;
import cells.DoorCell;
import cells.HallwayCell;
import cells.NullCell;
import cells.RoomCell;

/**
 * A class for finding routes between cells on the board.
 * 
 * @author Joshua Hindley
 */
public class Route {
	
	/**
	 * The game board.
	 */
	private Board board; 
	
	/**
	 * The cell the route starts from.
	 */
	private Cell fromCell;
	
	/**
	 * The goal cell of the route.
	 */
	private Cell toCell;
	
	/**
	 * The maximum number of moves allowed from the starting cell to the goal cell.
	 */
	private int maxMoves;
	
	/**
	 * A queue of elements to search through for a route.
	 */
	private PriorityQueue<FringeElement> fringe = new PriorityQueue<FringeElement>();
	
	/**
	 * Creates a new route.
	 * @param from	The cell to start the search from.
	 * @param to	The cell to get to.
	 * @param b	The board the cells are part of.
	 * @param max	The maximum number of moves to get to the cell.
	 */
	public Route(Cell from, Cell to, Board b, int max) {
		this.fromCell = from;
		this.toCell = to;	
		this.board = b;
		this.maxMoves = max;
	}
	
	/**
	 * Finds a route between the starting and goal cells.
	 * @return The cell that a route was found to (or null if no route was found).
	 */
	public Cell findRoute() {		
		FringeElement curr;
		Cell currCell;
		Cell neigh;
		boolean add;
		//adds the initial cell to the fringe
		this.fringe.offer(new FringeElement(this.fromCell, null, 0, this.toCell));
		//search through the fringe until a route is found or the fringe is empty
		while (!this.fringe.isEmpty()) {
			//get the next fringe element and cell  
			curr = this.fringe.poll();
			currCell = curr.getCell();
			if (!currCell.isVisited()) {
				currCell.setVisited(true);
				currCell.setPrev(curr.getPrev());
				//the best route has been found
				if(currCell.equals(this.toCell))
					return this.toCell;
	
				int x = 0;
				int y = -1;
				//for each cell neighbouring this cell
				while (true) {					
					neigh = this.board.getCell((currCell.getX() + x), (currCell.getY() + y));
					
					if (neigh != null) {
						add = true;
						//if neighbour is an invalid cell to move to
						if (neigh instanceof NullCell || neigh instanceof RoomCell)
							add = false;
						//if the max moves has been reached
						else if (curr.getStepsSoFar() + 1 > this.maxMoves)
							add = false;
						//if there is another player on the neighbour
						else if (neigh instanceof HallwayCell && ((HallwayCell) neigh).getPlayerHere() != null)
							add = false;
						//if one of the cells is a vertical door and the other cell is on a different x-axis
						else if(((neigh instanceof DoorCell && ((DoorCell) neigh).isVerticalDoor()) ||
								(currCell instanceof DoorCell && ((DoorCell) currCell).isVerticalDoor())) && neigh.getX() != currCell.getX())
							add = false;
						
						if (add && !neigh.isVisited()) {
							//add the neighbour to the fringe
							this.fringe.offer(new FringeElement(neigh, currCell, curr.getStepsSoFar()+1, this.toCell));
						}
					}
					//updates the x and y for the next neighbour
					if (y == -1)
						y = 1;
					else if (y == 1) {
						y = 0;
						x = -1;
					}
					else if (x == -1)
						x = 1;
					//all neighbours have been checked
					else
						break;
				}				
			}
		}
		//no route was found
		return null;
	}
}