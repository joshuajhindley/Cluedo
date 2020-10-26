package cards;

/**
 * The rooms that exist in the game; each room is provided with a toString() method.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
public enum Room {
	Kitchen 		{public String toString() {return "Kitchen";}},
	BallRoom 		{public String toString() {return "Ball Room";}},
	Conservatory 	{public String toString() {return "Conservatory";}},
	BilliardRoom 	{public String toString() {return "Billiard Room";}},
	Library 		{public String toString() {return "Library";}},
	Study 			{public String toString() {return "Study";}},
	Hall 			{public String toString() {return "Hall";}},
	Lounge 			{public String toString() {return "Lounge";}},
	DiningRoom 		{public String toString() {return "Dining Room";}}
}
