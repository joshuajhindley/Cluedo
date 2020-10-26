package cards;

/**
 * The weapons that exist in the game; each room is provided with a toString() method.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public enum Weapon {
	Candlestick {public String toString() { return "Candlestick"; }},
	Dagger 		{public String toString() { return "Dagger"; }},
	LeadPipe 	{public String toString() { return "Lead Pipe"; }},
	Revolver 	{public String toString() { return "Revolver"; }},
	Rope 		{public String toString() { return "Rope"; }},
	Spanner 	{public String toString() { return "Spanner"; }}
}
