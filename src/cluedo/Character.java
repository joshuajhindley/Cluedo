package cluedo;

/**
 * The characters that exist in the game; each character is provided with a toString() method.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public enum Character {
	MrsWhite 	{public String toString() {return "White";}},
	RevGreen 	{public String toString() {return "Green";}},
	MrsPeacock 	{public String toString() {return "Peacock";}},
	ProfPlum 	{public String toString() {return "Plum";}},
	MissScarlet {public String toString() {return "Scarlett";}},
	ColMustard 	{public String toString() {return "Mustard";}};
}