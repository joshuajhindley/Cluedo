package cluedo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cards.Room;
import cells.Cell;
import cells.DoorCell;
import cells.HallwayCell;
import cells.NullCell;
import cells.RoomCell;

/**
 * The board that the game will be played on.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public class Board {
	
	/**
	 * The cells that makeup this board.
	 */
	private Cell[][] cells = new Cell[27][24];

	/**
	 * The players that are playing the game.
	 */
	private ArrayList<Player> players;
	
	/**
	 * The doors for each room.
	 */
	private HashMap<Room, ArrayList<DoorCell>> doors = new HashMap<Room, ArrayList<DoorCell>>(); 
	
	/**
	 * The default cell that a player will stand on in each room.
	 */
	private HashMap<Room, RoomCell> defaultCell = new HashMap<Room, RoomCell>();
	
	/**
	 * Creates a new board.
	 * @param pl	The players in the current game.
	 */
	public Board(ArrayList<Player> pl) {
		this.players = pl;		
				
		//The initial board where dashes are null cells, dots are hallway cells, 
		//equals and carets are door cells and letters are room cells.
		String startingBoard = 
				//     ABCDEFGHIJKLMNOPQRSTUVWX
				/*0*/ "------------------------" +
				/*1*/ "---------.----.---------" +
				/*2*/ "KKKKKK-...BBBB...-CCCCCC" +
				/*3*/ "KKKKKK..BBBBBBBB..CCCCCC" +
				/*4*/ "KKKKKK..BBBBBBBB..CCCCCC" +
				/*5*/ "KKKKKK..BBBBBBBB..^CCCCC" +
				/*6*/ "KKKKKK..=BBBBBB=...CCCC-" +
				/*7*/ "-KKK=K..BBBBBBBB........" +
				/*8*/ "........B=BBBB=B.......-" +
				/*9*/ "-.................RRRRRR" +
				/*10*/"DDDDD.............=RRRRR" +
				/*11*/"DDDDDDDD..-----...RRRRRR" +
				/*12*/"DDDDDDDD..-----...RRRRRR" +
				/*13*/"DDDDDDD=..-----...RRRR=R" +
				/*14*/"DDDDDDDD..-----........-" +
				/*15*/"DDDDDDDD..-----...YYYYY-" +
				/*16*/"DDDDDD=D..-----..YYYYYYY" +
				/*17*/"-.........-----..=YYYYYY" +
				/*18*/".................YYYYYYY" +
				/*19*/"-........HH==HH...YYYYY-" +
				/*20*/"LLLLLL^..HHHHHH........." +
				/*21*/"LLLLLLL..HHHHH=........-" +
				/*22*/"LLLLLLL..HHHHHH..^SSSSSS" +
				/*23*/"LLLLLLL..HHHHHH..SSSSSSS" +
				/*24*/"LLLLLLL..HHHHHH..SSSSSSS" +
				/*25*/"LLLLLL-.-HHHHHH-.-SSSSSS" +
				/*26*/"------------------------";

		int x = 0;
		int y = 0;
		//for each cell
		for (char c : startingBoard.toCharArray()) {
			//the end of the row has been reached
			if (x == this.cells[0].length) {
				x = 0;
				y++;
			}
			
			//null cell
			if (c == '-')
				this.cells[y][x] = new NullCell(x, y);
			
			//hallway cell
			else if (c == '.')
				this.cells[y][x] = new HallwayCell(x, y);
						
			//door cell
			else if (c == '=' || c == '^')
				this.cells[y][x] = new DoorCell(c == '^', x, y);		
			
			//room cell
			else {
				if (c == 'K')
					this.cells[y][x] = new RoomCell(Room.Kitchen, x, y);
				else if (c == 'B')
					this.cells[y][x] = new RoomCell(Room.BallRoom, x, y);
				else if (c == 'C')
					this.cells[y][x] = new RoomCell(Room.Conservatory, x, y);
				else if (c == 'R')
					this.cells[y][x] = new RoomCell(Room.BilliardRoom, x, y);
				else if (c == 'Y')
					this.cells[y][x] = new RoomCell(Room.Library, x, y);
				else if (c == 'S')
					this.cells[y][x] = new RoomCell(Room.Study, x, y);
				else if (c == 'H')
					this.cells[y][x] = new RoomCell(Room.Hall, x, y);
				else if (c == 'L')
					this.cells[y][x] = new RoomCell(Room.Lounge, x, y);
				else if (c == 'D')
					this.cells[y][x] = new RoomCell(Room.DiningRoom, x, y);
			}
			x++;
		}
		addCellInformation();		
		setDefaultCells();		
		setPlayerStartingLocations();				
	}
	
	/**
	 * Sets the default cells players will stand on when in the rooms.
	 */
	public void setDefaultCells() {
		this.defaultCell.put(Room.BallRoom, 	(RoomCell) this.cells[6][9]);
		this.defaultCell.put(Room.BilliardRoom, (RoomCell) this.cells[11][18]);
		this.defaultCell.put(Room.Conservatory, (RoomCell) this.cells[4][18]);
		this.defaultCell.put(Room.DiningRoom, 	(RoomCell) this.cells[14][1]);
		this.defaultCell.put(Room.Hall, 		(RoomCell) this.cells[20][9]);
		this.defaultCell.put(Room.Kitchen, 		(RoomCell) this.cells[5][0]);
		this.defaultCell.put(Room.Library, 		(RoomCell) this.cells[16][18]);
		this.defaultCell.put(Room.Lounge, 		(RoomCell) this.cells[22][1]);
		this.defaultCell.put(Room.Study, 		(RoomCell) this.cells[23][18]);
	}
	
	/**
	 * Sets all of the players' starting locations.
	 */
	private void setPlayerStartingLocations() {
		for (Player p : this.players) {
			if (p.getCharacter().equals(Character.MrsWhite)) { //set Mrs White's starting location
				((HallwayCell) this.cells[1][9]).setPlayerHere(p);
				p.setLocation(this.cells[1][9]);
			} else if (p.getCharacter().equals(Character.RevGreen)) { //set Rev. Green's starting location
				((HallwayCell) this.cells[1][14]).setPlayerHere(p);
				p.setLocation(this.cells[1][14]);
			} else if (p.getCharacter().equals(Character.MrsPeacock)) { //set Mrs Peacock's starting location
				((HallwayCell) this.cells[7][23]).setPlayerHere(p);
				p.setLocation(this.cells[7][23]);
			} else if (p.getCharacter().equals(Character.ProfPlum)) { //set Prof. Plum's starting location
				((HallwayCell) this.cells[20][23]).setPlayerHere(p);
				p.setLocation(this.cells[20][23]);
			} else if (p.getCharacter().equals(Character.MissScarlet)) { //set Miss Scarlet's starting location
				((HallwayCell) this.cells[25][7]).setPlayerHere(p);
				p.setLocation(this.cells[25][7]);
			} else if (p.getCharacter().equals(Character.ColMustard)) { //set Col. Mustard's starting location
				((HallwayCell) this.cells[18][0]).setPlayerHere(p);
				p.setLocation(this.cells[18][0]);
			}
		}
	}
	
	/**
	 * Adds additional information to doorCells that were unavailable upon creation.
	 */
	private void addCellInformation() {
		//for all door cells
		for (int y = 0; y < this.cells.length; y++) {
			for (int x = 0; x < this.cells[0].length; x++) {
				if (this.cells[y][x] instanceof DoorCell) {
					//sets the room of the doorCell
					DoorCell d = (DoorCell) this.cells[y][x];
					//cell to left
					if (this.cells[y][x-1] instanceof RoomCell)
						d.setRoom(((RoomCell) this.cells[y][x-1]).getRoom());
					//cell above
					else if (this.cells[y-1][x] instanceof RoomCell)
						d.setRoom(((RoomCell) this.cells[y-1][x]).getRoom());
					//cell to right
					else if (this.cells[y][x+1] instanceof RoomCell)
						d.setRoom(((RoomCell) this.cells[y][x+1]).getRoom());
					
					//adds the door to the doors arrayList
					if (this.doors.containsKey(d.getRoom()))
						this.doors.get(d.getRoom()).add(d);
					else
						this.doors.put(d.getRoom(), new ArrayList<DoorCell>(Arrays.asList(d)));
				}
			}	
		}
	}
	
	/**
	 * Gets a cell on the board given an x and y.
	 * @param x	The x-position of the cell.
	 * @param y	The y-position of the cell.
	 * @return The cell at that x and y, or null if the x or y is invalid.
	 */
	public Cell getCell(int x, int y) {
		if (x < 0 || y < 0 || x >= this.cells[0].length || y >= this.cells.length )
			return null;
		return this.cells[y][x];
	}
	
	/**
	 * Gets a list of the doors to a room, given the room.
	 * @param r The room to get the doors for.
	 * @return An unmodifiable list of the doors for that room.
	 */
	public List<DoorCell> getDoors(Room r) {
		return Collections.unmodifiableList(this.doors.get(r));
	}
	
	/**
	 * Gets the default cell that the player stands on in a room, given the room.
	 * @param r	The room to get the default cell of.
	 * @return The default cell.
	 */
	public RoomCell getDefaultCell(Room r) {
		return this.defaultCell.get(r);
	}
	
	/**
	 * Resets all the cells on the board so they are unvisited and
	 * their previous cell is null.
	 */
	public void resetCells() {
		for (Cell[] cellRow : this.cells) {
			for (Cell c : cellRow) {
				c.setVisited(false);
				c.setPrev(null);
			}
		}
	}
}
