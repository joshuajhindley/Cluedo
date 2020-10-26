package cluedo;

/**
 * The actions that a player can perform on their turn.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public enum Actions {
	/**
	 * Move their character from one cell to another.
	 */
	Move,
	/**
	 * Make an accusation as to the killer, murder weapon, and location.
	 * (If they are wrong, they are eliminated.)
	 */
	Accuse,
	/**
	 * Make a suggestion as to the killer, murder weapon, and location.
	 */
	Suggest
}
