package cards;

/**
 * A weapon card that a player may possess.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public class WeaponCard implements Card {
	
	/**
	 * The weapon of this card.
	 */
	public final Weapon weapon;
	
	/**
	 * Creates a new weapon card to represent the given weapon.
	 * @param w The weapon this card represents.
	 */
	public WeaponCard(Weapon w) {
		weapon = w;
	}
	
	@Override
	public String toString() {
		return weapon.toString();
	}
}
