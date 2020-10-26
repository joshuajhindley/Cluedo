package gui;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.AbstractAction;

import javax.imageio.ImageIO;

import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import java.io.IOException;

import cards.Card;
import cards.CharacterCard;
import cards.Room;
import cards.RoomCard;
import cards.Weapon;
import cards.WeaponCard;
import cells.Cell;
import cells.RoomCell;
import cluedo.Actions;
import cluedo.Board;
import cluedo.Character;
import cluedo.Player;

/**
 * 
 * @author Joshua Harwood
 * @author Joshua Hindley
 */
@SuppressWarnings("unused")
public class GUI {

	private ArrayList<Player> players = new ArrayList<Player>();
	private ArrayList<Card> allCards = new ArrayList<Card>();
	private Player currentPlayer;

	private RoomCard murderRoom;
	private WeaponCard murderWeapon;
	private CharacterCard murderer;
	
	private Weapon guessWeapon = null;
	private Character guessCharacter = null;
	private Room guessRoom = null;

	private static Board board;
	
	private JFrame frmCluedo;
	private JImagePanel pnGame = new JImagePanel(); 
	private JImagePanel pnDice1 = new JImagePanel();
	private JImagePanel pnDice2 = new JImagePanel();
	private JImagePanel pnCard1,  pnCard2,  pnCard3,  pnCard4,  pnCard5,  pnCard6;
	
	private JPanel pnStart;
	private JTextField tfName;
	private JRadioButton rbW;
	private JRadioButton rbG;	
	private JRadioButton rbK;
	private JRadioButton rbP;
	private JRadioButton rbS;
	private JRadioButton rbM;
	private JButton btnAddPlayer;
	private JButton btnStartGame;

	private boolean playing = true;

	private JLabel lblPlayersTurn;
	private JButton btnNextTurn;
	private JRadioButton rdbtnAccuse;
	private JRadioButton rdbtSuggest;
	private JRadioButton rdbtnMove;
	private JButton btnMakeGuess;
	private JButton btnDoAction;
	private JPanel pnGuess;
	private JPanel pnGuessRoom;
	private final ButtonGroup actionButtonGroup = new ButtonGroup();
	private final ButtonGroup playerButtonGroup = new ButtonGroup();

	private Cell goalCell;
	private int diceRoll = 0;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmCluedo.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}


	/**
	 * Selects a murderer, murder weapon and room, then shuffles
	 * all the cards together and distributes them to the players
	 */
	public void setUpCards() {
		//gets all the room, weapon and character cards
		ArrayList<RoomCard> roomCards = new ArrayList<RoomCard>();
		ArrayList<WeaponCard> weaponCards = new ArrayList<WeaponCard>();
		ArrayList<CharacterCard> characterCards = new ArrayList<CharacterCard>();

		//generates the cards
		for(Character c : Character.values())
			characterCards.add(new CharacterCard(c));
		for(Weapon w : Weapon.values())
			weaponCards.add(new WeaponCard(w));
		for(Room r : Room.values())
			roomCards.add(new RoomCard(r));
		//shuffles all the cards
		Collections.shuffle(characterCards);
		Collections.shuffle(roomCards);
		Collections.shuffle(weaponCards);

		//selects the murderer, location and weapon
		murderer = characterCards.remove(0);
		murderRoom = roomCards.remove(0);
		murderWeapon = weaponCards.remove(0);

		//adds all the remaining cards to the allCards list and distributes the cards to the players
		allCards.addAll(characterCards);
		allCards.addAll(roomCards);
		allCards.addAll(weaponCards);
		Collections.shuffle(allCards);
		distributeCards();
	}

	/**
	 * Distributes all the remaining cards to the players
	 * until there are no cards left
	 */
	public void distributeCards() {
		while(true)
		{			
			for(Player p : players) 
			{
				if (allCards.isEmpty())
					return;
				p.addCard(allCards.remove(0));			
			}
		}
	}


	/**
	 * takes the players turn with their selected action
	 * @param a  -  the player's selected option
	 */
	public void takeTurn(Actions a) {
		if(players.size() > 0)
		{
			if(a == Actions.Move) {
				//gets the route
				Stack<Cell> route = currentPlayer.doMove(board, goalCell, diceRoll);
				Cell curr = null;

				if(route != null) {
					//moves the player to the goal one cell at a time
					while(!route.isEmpty()) {
						curr = route.pop();
						currentPlayer.getLocation().setPlayerHere(null);
						currentPlayer.setLocation(curr);
						curr.setPlayerHere(currentPlayer);				
						pnGame.paintAll(pnGame.getGraphics());
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				board.resetCells();
				pnGame.repaint();
				if(curr != null) {
					diceRoll = 0;
					checkActions(true);
				} else {
					JOptionPane.showMessageDialog(frmCluedo, "You cannot move to that cell.", "Invalid Move!", JOptionPane.WARNING_MESSAGE);					
				}


			} else if (a == Actions.Accuse) {
				//make an accusation
				if(currentPlayer.doAccuse(players, board, guessWeapon, guessCharacter, guessRoom, murderer, murderRoom, murderWeapon)) { 
					//correct, game over
					playing = false;
				} else {
					//incorrect, player eliminated
					currentPlayer.eliminate();
				}
				pnGame.repaint();


			} else if (a == Actions.Suggest) {
				//make a suggestion
				JOptionPane.showMessageDialog(frmCluedo, currentPlayer.doSuggest(players, board, guessCharacter, guessWeapon, frmCluedo));
				pnGame.repaint();
			}

		} else {
			//end the game as all players are out
			playing = false;
		}
	}


	/**
	 * updates the current player with the player
	 */
	public void updatePlayer() {
		while(true) {
			if(players.size() != 0) {
				currentPlayer = players.remove(0);
				pnGame.currentPlayer = currentPlayer;
			} else {
				currentPlayer = new Player(Character.ColMustard, "ERROR");
			}

			players.add(currentPlayer);
			//keep the loop going until we find a player that is in the game
			if(!currentPlayer.isEliminated()) { 
				break; 
			}	
		}
		pnGame.players = new ArrayList<Player>();
		for(Player p : players) {
			if(!p.isEliminated())
				pnGame.players.add(p);
		}

		checkActions();
		lblPlayersTurn.setText("It is " + currentPlayer.getName() +"'s Turn");
	}

	/**
	 * prompt the user what their suggestion is
	 * @param accuse  - if it is an accusation or not (this will also ask the room if true)
	 */
	public void askUser(boolean accuse) {
		pnGuess.setVisible(true);

		if(accuse) {
			pnGuessRoom.setVisible(true);
		} else {
			pnGuessRoom.setVisible(false);
		}
	}

	/**
	 * check the actions that the player has done given that they have moved or not
	 * @param moved  -  has the player moves
	 */
	public void checkActions(boolean moved) {
		checkActions();	
		if(moved)
			rdbtnMove.setEnabled(false);  
	}

	/**
	 * check what actions that the player can do and set the radio buttons accordingly 
	 */
	public void checkActions() {
		actionButtonGroup.clearSelection();
		if(currentPlayer != null) {
			//the player is in a room so can move, Accuse, suggest
			if(currentPlayer.getLocation() instanceof RoomCell) { 
				rdbtnAccuse.setEnabled(true);
				rdbtSuggest.setEnabled(true);
				rdbtnMove.setEnabled(true);
				btnDoAction.setEnabled(true);
				//the player is in a hallway so only can move
			} else { 
				rdbtnAccuse.setEnabled(false);
				rdbtSuggest.setEnabled(false);
				rdbtnMove.setEnabled(true);
				btnDoAction.setEnabled(true);
			}
		}
	}

	/**
	 * updates and displays the users cards on the game
	 */
	private void updateCards() {
		List<Card> hand = currentPlayer.getHand();
		Card currCard;
		Image img;
		ArrayList<JImagePanel> panels = new ArrayList<JImagePanel>(Arrays.asList(pnCard1, pnCard2, pnCard3, pnCard4, pnCard5, pnCard6));
		for(int i = 0; i < hand.size(); i++) {
			currCard = hand.get(i);
			try {
				img = ImageIO.read(getClass().getResource("/images/" + currCard.toString() + ".png"));
				panels.get(i).img = img;
				panels.get(i).repaint();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * restart the game
	 */
	private void restart() {
		String[] args = new String[0];
		main(args);
		frmCluedo.setVisible(false);
	}

	/**
	 * ask the user if they want to quit and if they do then the game will exit otherwise it will keep going
	 */
	private void quit() {
		int reply = JOptionPane.showConfirmDialog(frmCluedo, "Are you sure you want to quit", "Please confirm", JOptionPane.YES_NO_OPTION);
		if (reply == JOptionPane.YES_OPTION) {
			System.exit(0);
		} else {
			frmCluedo.getRootPane().requestFocus();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("serial")
	private void initialize() {
		//setup the main game frame
		frmCluedo = new JFrame();
		frmCluedo.setResizable(false);
		frmCluedo.setTitle("Cluedo");
		frmCluedo.setBounds(100, 100, 987, 939);
		frmCluedo.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frmCluedo.getContentPane().setLayout(null);

		frmCluedo.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				quit();				
			}
		});


		/*
		 * the key bindings
		 */

		//escape key binding
		frmCluedo.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "Quit");
		frmCluedo.getRootPane().getActionMap().put("Quit", new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {			
				quit();				
			}
		});

		//Move key binding
		frmCluedo.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_M , 0), "Move");
		frmCluedo.getRootPane().getActionMap().put("Move", new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(rdbtnMove.isEnabled()) {
					rdbtnMove.doClick();
				} else {
					JOptionPane.showMessageDialog(frmCluedo, "You cannot move"); //do suggest
				}
			}
		});

		//suggest key binding
		frmCluedo.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S , 0), "Suggest");
		frmCluedo.getRootPane().getActionMap().put("Suggest", new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(rdbtSuggest.isEnabled()) {
					rdbtSuggest.doClick();
				} else {
					JOptionPane.showMessageDialog(frmCluedo, "You cannot suggest"); //do suggest
				}
			}
		});

		//accuse key binding
		frmCluedo.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A , 0), "Accuse");
		frmCluedo.getRootPane().getActionMap().put("Accuse", new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(rdbtnAccuse.isEnabled()) {
					rdbtnAccuse.doClick();
				} else {
					JOptionPane.showMessageDialog(frmCluedo, "You cannot accuse"); //do suggest
				}
			}
		});

		//enter key binding for action OK, next turn, and adding player
		frmCluedo.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER , 0), "Enter");
		frmCluedo.getRootPane().getActionMap().put("Enter", new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(pnStart.isVisible()) { //the player wants to add a player
					btnAddPlayer.doClick();
				} else if (pnGuess.isVisible()){
					btnMakeGuess.doClick();
				} else if(rdbtnAccuse.isEnabled() || rdbtnMove.isEnabled() || rdbtSuggest.isEnabled()) {
					btnDoAction.doClick();
				} else {
					btnNextTurn.doClick();
				}
			}
		});

		//shift key binding
		frmCluedo.getRootPane().getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP , 0), "Start");
		frmCluedo.getRootPane().getActionMap().put("Start", new AbstractAction() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(pnStart.isVisible()) { //the player wants to add a player
					btnStartGame.doClick();
				} 
			}
		});







		//##################################################
		//##################################################
		//set up the Start panel with the character creation 
		pnStart = new JPanel();
		pnStart.setBounds(205, 168, 354, 222);
		frmCluedo.getContentPane().add(pnStart);
		pnStart.setLayout(null);

		JLabel lblNewLabel_1 = new JLabel("Please enter your name and player...");
		lblNewLabel_1.setBounds(10, 11, 331, 23);
		pnStart.add(lblNewLabel_1);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 16));

		String texts[] = {"", "Name...", "White", "Green", "Peacock", "Mustard", "Scarlett", "Plum"};

		//Text field for the user to input their name
		tfName = new JTextField();
		tfName.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) { //clear the text
				if(Arrays.stream(texts).parallel().anyMatch(tfName.getText()::equals))
					tfName.setText("");
			}
		});
		tfName.setBounds(10, 45, 331, 20);
		pnStart.add(tfName);
		tfName.setText("Name...");
		tfName.setColumns(10);

		//each of the players that are able to be played 
		rbW = new JRadioButton("Mrs White");
		rbW.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(Arrays.stream(texts).parallel().anyMatch(tfName.getText()::equals) && rbW.isEnabled())
					tfName.setText("White");

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbW.setBounds(10, 76, 109, 23);
		pnStart.add(rbW);	
		playerButtonGroup.add(rbW);

		rbG = new JRadioButton("Rev. Green");
		rbG.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(Arrays.stream(texts).parallel().anyMatch(tfName.getText()::equals) && rbG.isEnabled())
					tfName.setText("Green");

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbG.setBounds(121, 76, 109, 23);
		pnStart.add(rbG);
		playerButtonGroup.add(rbG);

		JRadioButton rbK = new JRadioButton("Mrs Peacock");
		rbK.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(Arrays.stream(texts).parallel().anyMatch(tfName.getText()::equals) && rbK.isEnabled())
					tfName.setText("Peacock");

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbK.setBounds(232, 76, 109, 23);
		pnStart.add(rbK);
		playerButtonGroup.add(rbK);

		JRadioButton rbM = new JRadioButton("Col. Mustard");
		rbM.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(Arrays.stream(texts).parallel().anyMatch(tfName.getText()::equals) && rbM.isEnabled())
					tfName.setText("Mustard");

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbM.setBounds(232, 102, 109, 23);
		pnStart.add(rbM);
		playerButtonGroup.add(rbM);

		JRadioButton rbS = new JRadioButton("Miss Scarlett");
		rbS.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(Arrays.stream(texts).parallel().anyMatch(tfName.getText()::equals) && rbS.isEnabled())
					tfName.setText("Scarlett");

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbS.setBounds(121, 102, 109, 23);
		pnStart.add(rbS);
		playerButtonGroup.add(rbS);

		JRadioButton rbP = new JRadioButton("Prof. Plum");
		rbP.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(Arrays.stream(texts).parallel().anyMatch(tfName.getText()::equals) && rbP.isEnabled())
					tfName.setText("Plum");

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbP.setBounds(10, 102, 109, 23);
		pnStart.add(rbP);
		playerButtonGroup.add(rbP);

		//the button to start the game after the user has added the correct amount of players
		btnStartGame = new JButton("Start Game");
		btnStartGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//if enabled, hide panel
				if(btnStartGame.isEnabled()) {
					pnStart.setVisible(false);
					setUpCards();
					//creates the board to play the game on
					board = new Board(players);	
					//sets the player to the first players turn
					pnGame.players = players;
					rdbtnMove.doClick();
					pnGame.repaint();
					updatePlayer();
					//sets the players on the board
					updateCards();
					btnNextTurn.setEnabled(false);
				}	

				frmCluedo.getRootPane().requestFocus();

			}
		});
		btnStartGame.setEnabled(false);
		btnStartGame.setBounds(10, 166, 331, 23);
		pnStart.add(btnStartGame);

		//add the player that the user has created into the game
		btnAddPlayer = new JButton("Add Player");
		btnAddPlayer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tfName.getText().length() == 0)
				{
					JOptionPane.showMessageDialog( btnAddPlayer, "Please enter a name.");
					tfName.grabFocus();
					return;
				} 
				for(Player p : players) {
					if(p.getName().equals(tfName.getText())) {
						JOptionPane.showMessageDialog(btnAddPlayer, "A player with that name already exists.");
						tfName.grabFocus();
						return;
					}
				}
				String name = tfName.getText().toUpperCase();
				name = name.substring(0, 1) + name.substring(1).toLowerCase();
				if(rbW.isSelected() && rbW.isEnabled()) {
					rbW.setEnabled(false);
					players.add(new Player(Character.MrsWhite, name));
				} else if (rbG.isSelected() && rbG.isEnabled()) {
					rbG.setEnabled(false);
					players.add(new Player(Character.RevGreen, name));
				} else if (rbK.isSelected() && rbK.isEnabled()) {
					rbK.setEnabled(false);
					players.add(new Player(Character.MrsPeacock, name));
				} else if (rbM.isSelected() && rbM.isEnabled()) {
					rbM.setEnabled(false);
					players.add(new Player(Character.ColMustard, name));
				} else if (rbS.isSelected() && rbS.isEnabled()) {
					rbS.setEnabled(false);
					players.add(new Player(Character.MissScarlet, name));
				} else if (rbP.isSelected() && rbP.isEnabled()) {
					rbP.setEnabled(false);
					players.add(new Player(Character.ProfPlum, name));
				} else {
					JOptionPane.showMessageDialog( btnAddPlayer, "Please select a player.");
					return;
				}

				playerButtonGroup.clearSelection();

				tfName.setText("Name...");
				if(players.size() >= 3) {
					btnStartGame.setEnabled(true);
				} 
				if (players.size() > 6) {
					btnAddPlayer.setEnabled(false);
				}

				frmCluedo.getRootPane().requestFocus();
			}
		});
		btnAddPlayer.setBounds(10, 132, 331, 23);
		pnStart.add(btnAddPlayer);




		//##################################################
		//the panels that hold the dice images

		pnDice1.setBackground(Color.LIGHT_GRAY);
		pnDice1.setBounds(10, 804, 75, 75);
		frmCluedo.getContentPane().add(pnDice1);
		pnDice1.setLayout(null);

		pnDice2.setLayout(null);
		pnDice2.setBackground(Color.LIGHT_GRAY);
		pnDice2.setBounds(95, 804, 75, 75);
		frmCluedo.getContentPane().add(pnDice2);

		//##################################################
		//the text area that holds the players notes
		JTextArea taNotes = new JTextArea();
		taNotes.setText("Notes:");
		taNotes.setBounds(725, 270, 246, 528);
		frmCluedo.getContentPane().add(taNotes);


		//##################################################
		//Next turn Button
		btnNextTurn = new JButton("Next Turn");
		btnNextTurn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(btnNextTurn.isEnabled() && !rdbtnAccuse.isEnabled() && !rdbtnMove.isEnabled() && !rdbtSuggest.isEnabled()) {
					if(currentPlayer != null) {
						currentPlayer.setNotes(taNotes.getText());
					}
					btnDoAction.setVisible(true);
					rdbtnMove.doClick();
					updatePlayer();
					taNotes.setText(currentPlayer.getNotes());
					if(currentPlayer != null) {
						updateCards();
					}
					pnGame.repaint();

					btnNextTurn.setEnabled(false);
				} else {
					JOptionPane.showMessageDialog(frmCluedo, "Please take your turn"); //do suggest
				}


				frmCluedo.getRootPane().requestFocus();



			}
		});
		btnNextTurn.setFont(new Font("Tahoma", Font.BOLD, 16));
		btnNextTurn.setBounds(725, 804, 246, 74);
		frmCluedo.getContentPane().add(btnNextTurn);


		//##################################################
		//Panels for each of the cards (From right to left, i.e. pnCard1 is the rightmost panel)
		pnCard6 = new JImagePanel();
		pnCard6.setLayout(null);
		pnCard6.setBackground(SystemColor.menu);
		pnCard6.setBounds(335, 804, 55, 75);
		frmCluedo.getContentPane().add(pnCard6);

		pnCard5 = new JImagePanel();
		pnCard5.setLayout(null);
		pnCard5.setBackground(SystemColor.menu);
		pnCard5.setBounds(400, 804, 55, 75);
		frmCluedo.getContentPane().add(pnCard5);

		pnCard4 = new JImagePanel();
		pnCard4.setLayout(null);
		pnCard4.setBackground(SystemColor.menu);
		pnCard4.setBounds(465, 804, 55, 75);
		frmCluedo.getContentPane().add(pnCard4);

		pnCard3 = new JImagePanel();
		pnCard3.setLayout(null);
		pnCard3.setBackground(SystemColor.menu);
		pnCard3.setBounds(530, 804, 55, 75);
		frmCluedo.getContentPane().add(pnCard3);

		pnCard2 = new JImagePanel();
		pnCard2.setLayout(null);
		pnCard2.setBackground(SystemColor.menu);
		pnCard2.setBounds(595, 804, 55, 75);
		frmCluedo.getContentPane().add(pnCard2);

		pnCard1 = new JImagePanel();
		pnCard1.setLayout(null);
		pnCard1.setBackground(SystemColor.menu);
		pnCard1.setBounds(660, 804, 55, 75);
		frmCluedo.getContentPane().add(pnCard1);		


		//##################################################
		//##################################################
		//label to indicate the players turn
		lblPlayersTurn = new JLabel("It is X's Turn");
		lblPlayersTurn.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPlayersTurn.setBounds(723, 11, 248, 14);
		frmCluedo.getContentPane().add(lblPlayersTurn);


		//##################################################
		//##################################################
		//set up the action panel
		//that panel that holds Move, Suggest, Accuse, Do Nothing actions
		JPanel pnAction = new JPanel();
		pnAction.setBounds(723, 36, 248, 223);
		frmCluedo.getContentPane().add(pnAction);
		pnAction.setLayout(null);

		//the actions 
		rdbtnMove = new JRadioButton("Move");
		rdbtnMove.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				frmCluedo.getRootPane().requestFocus();
			}
		});
		rdbtnMove.setFont(new Font("Tahoma", Font.PLAIN, 16));
		rdbtnMove.setBounds(6, 37, 130, 30);
		pnAction.add(rdbtnMove);

		rdbtSuggest = new JRadioButton("Suggest");
		rdbtSuggest.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				frmCluedo.getRootPane().requestFocus();
			}
		});
		rdbtSuggest.setFont(new Font("Tahoma", Font.PLAIN, 16));
		rdbtSuggest.setBounds(6, 70, 130, 30);
		pnAction.add(rdbtSuggest);

		rdbtnAccuse = new JRadioButton("Accuse");
		rdbtnAccuse.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				frmCluedo.getRootPane().requestFocus();
			}
		});
		rdbtnAccuse.setFont(new Font("Tahoma", Font.PLAIN, 16));
		rdbtnAccuse.setBounds(6, 103, 130, 30);
		pnAction.add(rdbtnAccuse);

		//button to commit the action
		btnDoAction = new JButton("OK");
		btnDoAction.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {


				if(btnDoAction.isEnabled()) {
					btnDoAction.setEnabled(false);

					if (rdbtSuggest.isSelected()) {
						askUser(false);
						rdbtSuggest.setEnabled(false);
						rdbtnAccuse.setEnabled(false);
						rdbtnMove.setEnabled(false);
					} else if(rdbtnAccuse.isSelected()) {
						askUser(true);
						rdbtSuggest.setEnabled(false);
						rdbtnAccuse.setEnabled(false);
						rdbtnMove.setEnabled(false);
					}  else if (rdbtnMove.isSelected()) {
						
						//Rolls dice
						Random rand = new Random();
						int die1 = rand.nextInt(6) + 1;
						int die2 = rand.nextInt(6) + 1;
						//gets path to dice images
						String die1String = "/images/dice_" + die1 + ".png";
						String die2String = "/images/dice_" + die2 + ".png";
						diceRoll = die1 + die2;
						Image img;
						//Makes it look like the dice are rolling, with changing values
						try {
							for(int i = 0; i < 10; i++) {
								img = ImageIO.read(getClass().getResource("/images/dice_" + (rand.nextInt(6) + 1) + ".png"));
								pnDice1.img = img;
								pnDice1.paintAll(pnDice1.getGraphics());
								img = ImageIO.read(getClass().getResource("/images/dice_" + (rand.nextInt(6) + 1) + ".png"));
								pnDice2.img = img;
								pnDice2.paintAll(pnDice2.getGraphics());
								Thread.sleep(100);
							}
							//sets the dice panels to show the values of the actual roll
							img = ImageIO.read(getClass().getResource(die1String));
							pnDice1.img = img;
							img = ImageIO.read(getClass().getResource(die2String));
							pnDice2.img = img;
							pnDice1.repaint();
							pnDice2.repaint();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					} else{
						//none selected
						JOptionPane.showMessageDialog(frmCluedo, "Please select an action");
						btnDoAction.setEnabled(true);
					}
				} else {
					JOptionPane.showMessageDialog(frmCluedo, "Please end your turn");
				}
				btnNextTurn.setEnabled(true);
				frmCluedo.getRootPane().requestFocus();
			}
		});
		
		btnDoAction.setBounds(10, 182, 228, 30);
		pnAction.add(btnDoAction);

		JLabel lblNewLabel = new JLabel("What would you like to do this turn?");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(10, 0, 228, 30);
		pnAction.add(lblNewLabel);

		//adds the buttons to a button group so that the user can only select one
		actionButtonGroup.add(rdbtnMove);
		actionButtonGroup.add(rdbtSuggest);
		actionButtonGroup.add(rdbtnAccuse);


		
		//##################################################
		//##################################################
		//setup the Game panel
		pnGame.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				//gets the cell that the player clicks on
				if(!pnStart.isVisible()) {
					int x = arg0.getX();
					int y = arg0.getY();
					//within valid squares on the board
					if(x >= 29 && x <= 676 && y >= 31 && y <= 755) {
						int boardX = (x-29)/27;
						int boardY = (y-31)/29 + 1;
						Cell boardCell = board.getCell(boardX, boardY);
						goalCell = boardCell;
						//tries to move the player to that cell
						if(rdbtnMove.isSelected())
							takeTurn(Actions.Move);

					}
				}

				frmCluedo.getRootPane().requestFocus();
			}
		});
		pnGame.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				//shows the information of players the user hovers the mouse on
				if(!pnStart.isVisible() && !pnGuess.isVisible() && !pnGuessRoom.isVisible()) {
					int x = e.getX();
					int y = e.getY();
					//valid square on the board
					if(x >= 29 && x <= 676 && y >= 31 && y <= 755) {
						int boardX = (x-29)/27;
						int boardY = (y-31)/29 + 1;
						Cell boardCell = board.getCell(boardX, boardY);
						Player playerOnCell = boardCell.getPlayerHere();
						if(playerOnCell != null)
							pnGame.setToolTipText(playerOnCell.getName() + " - " + playerOnCell.toString());
						else
							pnGame.setToolTipText("");
					}
				}
			}
		});

		pnGame.setBounds(10, 10, 707, 788);
		frmCluedo.getContentPane().add(pnGame);




		//##################################################
		//set the game picture on the panel
		Image img;
		try {
			img = ImageIO.read(getClass().getResource("/images/cluedo_board.png"));
			pnGame.img = img;
			pnGame.setLayout(null);
		} catch (Exception e) {}




		//##################################################
		//##################################################
		//set up the guess panel for the player

		ButtonGroup bgGuessChar = new ButtonGroup();
		ButtonGroup bgGuessWeap = new ButtonGroup();
		ButtonGroup bgGuessRoom = new ButtonGroup();

		pnGuess = new JPanel();
		pnGuess.setLayout(null);
		pnGuess.setBounds(196, 123, 354, 395);
		pnGame.add(pnGuess);
		pnGuess.setVisible(false);

		JLabel lblPleaseSelectThe = new JLabel("Please select the following");
		lblPleaseSelectThe.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseSelectThe.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblPleaseSelectThe.setBounds(10, 11, 330, 23);
		pnGuess.add(lblPleaseSelectThe);

		//##################################################
		//guessing a character
		JPanel pnGuessCharacter = new JPanel();
		pnGuessCharacter.setBounds(10, 45, 330, 86);
		pnGuess.add(pnGuessCharacter);
		pnGuessCharacter.setLayout(null);

		JLabel lblPleaseSelectA = new JLabel("Please select a character");
		lblPleaseSelectA.setBounds(10, 5, 303, 16);
		pnGuessCharacter.add(lblPleaseSelectA);
		lblPleaseSelectA.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseSelectA.setFont(new Font("Tahoma", Font.PLAIN, 13));


		JRadioButton rbGuessMustard = new JRadioButton("Col. Mustard");
		rbGuessMustard.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessCharacter = Character.ColMustard;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessMustard.setBounds(228, 54, 109, 23);
		pnGuessCharacter.add(rbGuessMustard);
		bgGuessChar.add(rbGuessMustard);

		JRadioButton rbGuessPeacock = new JRadioButton("Mrs Peacock");
		rbGuessPeacock.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessCharacter = Character.MrsPeacock;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessPeacock.setBounds(228, 28, 109, 23);
		pnGuessCharacter.add(rbGuessPeacock);
		bgGuessChar.add(rbGuessPeacock);

		JRadioButton rbGuessGreen = new JRadioButton("Rev. Green");
		rbGuessGreen.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessCharacter = Character.RevGreen;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessGreen.setBounds(117, 28, 109, 23);
		pnGuessCharacter.add(rbGuessGreen);
		bgGuessChar.add(rbGuessGreen);

		JRadioButton rbGuessScarlett = new JRadioButton("Miss Scarlett");
		rbGuessScarlett.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessCharacter = Character.MissScarlet;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessScarlett.setBounds(117, 54, 109, 23);
		pnGuessCharacter.add(rbGuessScarlett);
		bgGuessChar.add(rbGuessScarlett);

		JRadioButton rbGuessWhite = new JRadioButton("Mrs White");
		rbGuessWhite.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessCharacter = Character.MrsWhite;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessWhite.setBounds(6, 28, 109, 23);
		pnGuessCharacter.add(rbGuessWhite);
		bgGuessChar.add(rbGuessWhite);

		JRadioButton rbGuessPlum = new JRadioButton("Prof. Plum");
		rbGuessPlum.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessCharacter = Character.ProfPlum;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessPlum.setBounds(6, 54, 109, 23);
		pnGuessCharacter.add(rbGuessPlum);
		bgGuessChar.add(rbGuessPlum);


		//##################################################
		//the weapon guess
		JPanel pnGuessWeapon = new JPanel();
		pnGuessWeapon.setLayout(null);
		pnGuessWeapon.setBounds(10, 142, 330, 86);
		pnGuess.add(pnGuessWeapon);


		JLabel lblPleaseSelectA_1 = new JLabel("Please select a weapon");
		lblPleaseSelectA_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseSelectA_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblPleaseSelectA_1.setBounds(10, 5, 305, 16);
		pnGuessWeapon.add(lblPleaseSelectA_1);


		JRadioButton rbGuessKnife = new JRadioButton("Dagger");
		rbGuessKnife.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessWeapon = Weapon.Dagger;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessKnife.setBounds(228, 54, 109, 23);
		pnGuessWeapon.add(rbGuessKnife);
		bgGuessWeap.add(rbGuessKnife);

		JRadioButton rbGuessLeadPipe = new JRadioButton("Lead Pipe");
		rbGuessLeadPipe.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessWeapon = Weapon.LeadPipe;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessLeadPipe.setBounds(228, 28, 109, 23);
		pnGuessWeapon.add(rbGuessLeadPipe);
		bgGuessWeap.add(rbGuessLeadPipe);

		JRadioButton rbGuessCandle = new JRadioButton("Candlestick");
		rbGuessCandle.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessWeapon = Weapon.Candlestick;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessCandle.setBounds(117, 28, 109, 23);
		pnGuessWeapon.add(rbGuessCandle);
		bgGuessWeap.add(rbGuessCandle);

		JRadioButton rbGuessRevolver = new JRadioButton("Revolver");
		rbGuessRevolver.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessWeapon = Weapon.Revolver;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessRevolver.setBounds(117, 54, 109, 23);
		pnGuessWeapon.add(rbGuessRevolver);
		bgGuessWeap.add(rbGuessRevolver);

		JRadioButton rbGuessWrench = new JRadioButton("Spanner");
		rbGuessWrench.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessWeapon = Weapon.Spanner;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessWrench.setBounds(6, 28, 109, 23);
		pnGuessWeapon.add(rbGuessWrench);
		bgGuessWeap.add(rbGuessWrench);

		JRadioButton rbGuessRope = new JRadioButton("Rope");
		rbGuessRope.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessWeapon = Weapon.Rope;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessRope.setBounds(6, 54, 109, 23);
		pnGuessWeapon.add(rbGuessRope);
		bgGuessWeap.add(rbGuessRope);


		//##################################################       	
		//the room in the guess
		pnGuessRoom = new JPanel();
		pnGuessRoom.setLayout(null);
		pnGuessRoom.setBounds(10, 239, 330, 109);
		pnGuess.add(pnGuessRoom);

		JLabel label = new JLabel("Please select a room");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Tahoma", Font.PLAIN, 13));
		label.setBounds(10, 0, 310, 16);
		pnGuessRoom.add(label);


		JRadioButton rbGuessStudy = new JRadioButton("Study");
		rbGuessStudy.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessRoom = Room.Study;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessStudy.setBounds(232, 49, 109, 23);
		pnGuessRoom.add(rbGuessStudy);
		bgGuessRoom.add(rbGuessStudy);

		JRadioButton rbGuessConservatory = new JRadioButton("Conservatory");
		rbGuessConservatory.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessRoom = Room.Conservatory;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessConservatory.setBounds(232, 23, 109, 23);
		pnGuessRoom.add(rbGuessConservatory);
		bgGuessRoom.add(rbGuessConservatory);

		JRadioButton rbGuessBallRoom = new JRadioButton("Ball Room");
		rbGuessBallRoom.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessRoom = Room.BallRoom;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessBallRoom.setBounds(121, 23, 109, 23);
		pnGuessRoom.add(rbGuessBallRoom);
		bgGuessRoom.add(rbGuessBallRoom);

		JRadioButton rbGuessLibrary = new JRadioButton("Library");
		rbGuessLibrary.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessRoom = Room.Library;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessLibrary.setBounds(121, 49, 109, 23);
		pnGuessRoom.add(rbGuessLibrary);
		bgGuessRoom.add(rbGuessLibrary);

		JRadioButton rbGuessKitchen = new JRadioButton("Kitchen");
		rbGuessKitchen.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessRoom = Room.Kitchen;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessKitchen.setBounds(10, 23, 109, 23);
		pnGuessRoom.add(rbGuessKitchen);
		bgGuessRoom.add(rbGuessKitchen);

		JRadioButton rbGuessBilliard = new JRadioButton("Billiard Room");
		rbGuessBilliard.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessRoom = Room.BilliardRoom;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessBilliard.setBounds(10, 49, 109, 23);
		pnGuessRoom.add(rbGuessBilliard);
		bgGuessRoom.add(rbGuessBilliard);

		JRadioButton rbGuessHall = new JRadioButton("Hall");
		rbGuessHall.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessRoom = Room.Hall;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessHall.setBounds(10, 75, 109, 23);
		pnGuessRoom.add(rbGuessHall);
		bgGuessRoom.add(rbGuessHall);

		JRadioButton rbGuessLounge = new JRadioButton("Lounge");
		rbGuessLounge.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessRoom = Room.Lounge;

				frmCluedo.getRootPane().requestFocus();
			}
		});
		rbGuessLounge.setBounds(121, 75, 109, 23);
		pnGuessRoom.add(rbGuessLounge);
		bgGuessRoom.add(rbGuessLounge);

		JRadioButton rbGuessDining = new JRadioButton("Dining Room");
		rbGuessDining.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				guessRoom = Room.DiningRoom;
			}
		});
		rbGuessDining.setBounds(232, 75, 109, 23);
		pnGuessRoom.add(rbGuessDining);
		bgGuessRoom.add(rbGuessDining);

		//##################################################
		//Confirming a guess
		btnMakeGuess = new JButton("Make Guess");
		btnMakeGuess.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//there has been a valid guess
				if( (guessCharacter != null) && (guessWeapon  !=  null) && ((guessRoom != null) || (!pnGuessRoom.isVisible())) ) { 

					pnGuess.setVisible(false);
					if(pnGuessRoom.isVisible()) {
						pnGuess.setVisible(false);
						pnGuessRoom.setVisible(false);
						takeTurn(Actions.Accuse);
						//correct accusation player wins
						if(!playing) {
							int reply = JOptionPane.showConfirmDialog(frmCluedo, "Game Over\n " + currentPlayer.getName() + " has won!\n The murder was committed by " + murderer + " in the " + murderRoom + " with the " + murderWeapon + "\n Would you like to play again?", "GAME OVER", JOptionPane.YES_NO_OPTION);
							if (reply == JOptionPane.YES_OPTION) {
								restart();
							} else {
								System.exit(0);
							}
						} else {
							//there are no players left
							if(pnGame.players.size() <= 1) { 
								int reply = JOptionPane.showConfirmDialog(frmCluedo, "That was incorrect.\n" + currentPlayer.getName() + " is out\n\nGame over all players are out\nThe murder was committed by " + murderer + " in the " + murderRoom + " with the " + murderWeapon + "\n Would you like to play again?", "GAME OVER", JOptionPane.YES_NO_OPTION);
								if (reply == JOptionPane.YES_OPTION) {
									restart();
								} else {
									System.exit(0);
								}
							} else {
								JOptionPane.showMessageDialog(frmCluedo, "That was incorrect.\n " + currentPlayer.getName() + " is now out of the game!"); 
							}
						} 
					} else {
						pnGuess.setVisible(false);
						takeTurn(Actions.Suggest);
					}

				} 

				frmCluedo.getRootPane().requestFocus();

			}
		});

		btnMakeGuess.setBounds(10, 359, 330, 23);
		pnGuess.add(btnMakeGuess);



		//##################################################
		//##################################################
		//setting up the JmenuBar
		//menu bar to hold the options
		JMenuBar menuBar = new JMenuBar();
		frmCluedo.setJMenuBar(menuBar);

		//file option on the menu bar
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		//quit option under file
		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				quit();
			}
		});
		mnFile.add(mntmQuit);

		//adds game option to the menu bar
		JMenu mnGame = new JMenu("Game");
		menuBar.add(mnGame);

		//adds restart option to the game
		JMenuItem mntmRestart = new JMenuItem("Restart");
		mntmRestart.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				restart();
			}
		});
		mnGame.add(mntmRestart);

		JMenuItem mntmHelp = new JMenuItem("Help");
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JOptionPane.showMessageDialog(frmCluedo, "Key Bindings:\n " +
						"\nActions:\n" +
						"M  -  Select Move\n" +
						"A  -  Select Accuse\n" +
						"S  -  Select Suggest\n" +
						"\nCommits:\n" +
						"Enter  -  When doing actions it commits actions\n" +
						"       -  When all actions are complete, end turn\n" +
						"       -  When adding players, add a player\n" +
						"       -  When making a suggestion or accusation, commit suggest / accuse \n" +
						"Up  	-  When adding players, Start game\n" +
						"\nOther:\n" +
						"esc  -  Quit Game\n"); 				

			}
		});
		mnGame.add(mntmHelp);

		JMenu mnDebug = new JMenu("Debug");
		mnGame.add(mnDebug);

		JMenuItem mntmAns = new JMenuItem("Reveal Answer");
		mntmAns.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(murderer != null) {
					JOptionPane.showMessageDialog(frmCluedo, "The Murderer is:    " + murderer +
							"\nThe Room is:     " + murderRoom.room.name() +
							"\nThe Weapon is:    " + murderWeapon);
				} else {
					JOptionPane.showMessageDialog(frmCluedo, "The game has not begun");
				}


			}
		});
		mnDebug.add(mntmAns);
	}
}