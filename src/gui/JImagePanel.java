package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import cluedo.Character;
import cluedo.Player;


/**
 * A JPanel that can hold an image and players.
 * Draws the image and any players on top of that image.
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
@SuppressWarnings("serial")
public class JImagePanel extends JPanel {
	
	/**
	 * The image to draw (the board image).
	 */
	public Image img;
	
	/**
	 * The players to draw on the board.
	 */
	public ArrayList<Player> players = new ArrayList<Player>();
	
	/**
	 * The current player (drawn with a green outline).
	 */
	public Player currentPlayer;
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		//draws the image
		if (this.img != null)
			g.drawImage(this.img, 0, 0, null);
		
		//makes circles look nicer
		Graphics2D g2 = (Graphics2D)g;		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (this.players.size() > 0) {
			for (Player p : this.players) {
				//gets the x and y where the player should be drawn
				int x = p.getLocation().getX();
				int y = p.getLocation().getY();
				int xToDraw = x * 27 + 33;
				int yToDraw = (y - 1) * 29 + 35;
				//gets the color of the player
				if (p.getCharacter() == Character.ColMustard)
					g2.setColor(Color.YELLOW.darker());
				else if (p.getCharacter() == Character.MissScarlet)
					g2.setColor(Color.RED);
				else if (p.getCharacter() == Character.MrsPeacock)
					g2.setColor(Color.BLUE);
				else if (p.getCharacter() == Character.MrsWhite)
					g2.setColor(Color.WHITE);
				else if (p.getCharacter() == Character.ProfPlum)
					g2.setColor(Color.PINK.darker().darker());
				else if (p.getCharacter() == Character.RevGreen)
					g2.setColor(Color.GREEN.darker());
				//draws the player
				g2.fillOval(xToDraw, yToDraw, 19, 19);
				
				//outlines all the players
				//outlines the current player with a bright green border
				if (p == this.currentPlayer) {
					g2.setStroke(new BasicStroke(3));
					g2.setColor(Color.GREEN.brighter());
				} else {
					g2.setStroke(new BasicStroke(1));
					g2.setColor(Color.BLACK);
				}
				g.drawOval(xToDraw, yToDraw, 19, 19);
			}			
		}		
	}
}