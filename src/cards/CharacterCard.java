package cards;
import cluedo.Character;

/**
 * A character card that a player may possess.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public class CharacterCard implements Card {
	
	/**
	 * The character of this card.
	 */
	public final Character thisCharacter;
	
	/**
	 * Creates a new character card to represent the given character.
	 * @param c The character this card represents.
	 */
	public CharacterCard(Character c) {
		this.thisCharacter = c;
	}
	
	@Override
	public String toString() {
		return this.thisCharacter.toString();
	}
}
