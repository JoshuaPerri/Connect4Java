/**
 *  Assignment 7
 *  FinalCulminatingJoshuaPerri.java
 *  This program is a digital version of connect 4
 *  Joshua Perri
 *  January 24, 2019
 *  version 1.0
 */
package finalculminatingjoshuaperri;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class FinalCulminatingJoshuaPerri {
    
    public static JFrame gameWindow = new JFrame("Connect 4");           // The window that the game is stored in
    public static JFrame promptWindow = new JFrame("Enter Information"); // The window that takes down your name and other information
    public static JPanel gameContentPane = new JPanel();                 // The ContentPane for the game window
    public static JPanel promptContentPane = new JPanel();               // The ContentPane for the prompt window
    public static JButton[][] button = new JButton[7][6];                // The board buttons, with indexs corresponding to the width and height of the board
    public static JLabel lblPrompt = new JLabel();                       // The label of the prompt window
    public static JLabel lblWelcome = new JLabel();                      // The label of the welcome text
    public static JTextField input = new JTextField();                   // The textfield that the user types into
    public static JLabel[] lblName = new JLabel[2];                      // The labels that contain the names entered at the start and the score
    public static JButton btnReset = new JButton("Play again");          // Reset button
    public static JButton btnExit = new JButton("Exit");                 // Exit button
    public static Timer timer;                                           // Timer to control the computer player
    
    public static int turnNum = 0;               // The current turn number used to prevent draws
    public static int currentPlayer = 1;         // The current player - 1 = redPlayer, 2 = yellowPlayer
    public static int[][] board = new int[7][6]; // The information of the board - 0 = no piece, 1 = red piece, 2 = yellow piece
    public static int pick = 0;                  // The column of the button that the user picks
    public static boolean cmpPlayer = false;     // True if there is a computer player enabled
    public static boolean gameDone = false;      // True if the game has been won
    
    public static String p1Name = "";  // The name entered as player 1
    public static String p2Name = "";  // The name entered as player 2
    public static int p1Wins = 0;      // The number of wins player 1 has
    public static int p2Wins = 0;      // The number of wins player 2 has
    
    public static int width = 7;  // The width of the board
    public static int height = 6; // The height of the board
    
    public static int promptState = 1; // The state of the prompt window, used to change the prompt 
    
    public static void main(String[] args) {
        
        //Creates both GUIs
        createPromptGUI();
        createGameGUI();
        
    }
    /**
     *  Creates the GUI for the prompt window
     *  Only called once - doesn't need any parameters
     */
    private static void createPromptGUI() {
        
        System.out.println("Creating Prompt GUI...");
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        // Creates the prompt window and sets some properties
        promptWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        promptWindow.setAlwaysOnTop(true);
        promptWindow.setVisible(true);
        promptWindow.resize(400, 300);
        
        // Adds the prompt label and the text to the label
        lblPrompt.setText("Please enter your name:");
        promptContentPane.add(lblPrompt);

        // Adds the actionListener to the textfield
        input.addActionListener((ActionEvent e) -> {
            
            switch (promptState) {
                case 1:  // For the first user input to the testfield
                    
                    promptState++;
                    p1Name = input.getText();
                    input.setText("");
                    System.out.println("Player 1 name - " + p1Name);
                    lblName[0].setText(p1Name);
                    lblPrompt.setText("Is there a second player? (Enter yes or no)");
                    break;
                    
                case 2: // For the second user input to the testfield
                    
                    String current;
                    current = input.getText();
                    current = current.toUpperCase();
                    System.out.println("User entered - " + current);
                    input.setText("");
                    
                    switch (current) {
                        case "NO": // If the user enters "no" to the second text prompt
                            
                            cmpPlayer = true;
                            promptState = 4;
                            p2Name = "CPU";
                            lblName[1].setText(p2Name);
                            break;
                        case "YES": // If the user enters "yes" to the second text prompt
                            
                            cmpPlayer = false;
                            promptState = 3;
                            lblPrompt.setText("Enter the second player's name:");
                            break;
                        default: // If the user enters an invalid input to the prompt
                            
                            System.out.println(current + " - is not a valid input");
                            break;
                    }   
                    break;
                    
                case 3: // For the third user input to the testfield, if they select to have a second player
                    
                    promptState++;
                    p2Name = input.getText();
                    input.setText("");
                    System.out.println("Player 2 name - " + p2Name);
                    lblName[1].setText(p2Name);
                    break;
                default:
                    
                    break;
            }
            
            if (promptState == 4) { // Hides the prompt window when the prompts are complete and starts up the game
                
                // Welcomes the players to the game
                if (cmpPlayer) {
                    System.out.println("Welcome " + p1Name + " to Connect 4!");
                } else {
                    System.out.println("Welcome " + p1Name + " and " + p2Name +" to Connect 4!");
                }
                
                
                promptWindow.setVisible(false);
                initializeBoard();
                enableButtons();
                player1Turn();
            }
            
        });
        
        // Sets some properties of the textfield input
        input.setPreferredSize(new Dimension(110, 25));
        promptContentPane.add(input);
        
        // Sets some properties of the label lblWelcome
        lblWelcome.setPreferredSize(new Dimension(160, 100));
        lblWelcome.setText("Welcome to Connect 4!");
        promptContentPane.add(lblWelcome);
        
        // Adds the content pane to the window
        promptWindow.setContentPane(promptContentPane);
    }
    /**
     *  Creates the GUI for the game window
     *  Only called once - doesn't need any parameters
     */
    private static void createGameGUI() {
        
        System.out.println("Creating Game GUI...");
        JFrame.setDefaultLookAndFeelDecorated(true);
        
        // Creates the game window and sets some properties
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setVisible(true);
        gameWindow.resize(1000, 1000);
        
        // Sets the layout of the content pane as GridBagLayout
        gameContentPane.setLayout(new GridBagLayout());
        
        // Creates a new GridBagConstraints with the name constraints to layout the objects in the window
        GridBagConstraints constraints = new GridBagConstraints();
        
        // Sets the width and height of the grid
        constraints.gridx = width;
        constraints.gridy = height + 2;
        
        // Sets the insets or margins of the layout
        constraints.insets.set(5, 5, 5, 5);
        
        // Creates the buttons for the board
        for(int row = 0; row < height; row++) {
            for(int column = 0; column < width; column++) { // Runs for each tile on the board
                
                // Initializes the current button
                button[column][row] = new JButton();
                
                // Sets the size of the current button
                button[column][row].setPreferredSize(new Dimension(110, 110));
                
                // Sets the location of the current button in the grid
                constraints.gridx = column + 1;
                constraints.gridy = row + 1;
                
                // Sets the text of the buttton to its column which is used later 
                button[column][row].setText(""+column);
                
                // Adds the button event ActionListener
                button[column][row].addActionListener((ActionEvent e) -> {
                    
                    // Finds the column of the button that was pressed by locating its text
                    int current;
                    current = e.getSource().toString().indexOf("text=");
                    
                    // Sets pick to the column of the button pressed and formats it into an int
                    pick = (int)e.getSource().toString().charAt(current + 5);
                    pick -= 48;
                    
                    // Calls buttonAction()
                    buttonAction();
                    
                });  
                
                // Disables the current button
                button[column][row].setEnabled(false);
                
                // Sets the boarder of the current button
                button[column][row].setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
                
                // Adds the current button to the content pane
                gameContentPane.add(button[column][row], constraints);
                
            }
        }
        
        // Initiates the labels and sets the alignment
        lblName[0]= new JLabel("", SwingConstants.LEFT);
        lblName[1]= new JLabel("", SwingConstants.RIGHT);
        
        // Sets the fonts for each label
        lblName[0].setFont(new Font("Courier New", Font.BOLD, 20));
        lblName[1].setFont(new Font("Courier New", Font.BOLD, 20));
        
        // Sets how wide the labels are
        constraints.gridwidth = 3;
        constraints.gridheight = 1;
        
        // Sets the location fo the first label
        constraints.gridx = 1;
        constraints.gridy = 0;
        
        // Adds the first label to the contentPane
        gameContentPane.add(lblName[0], constraints);
        
        // Sets the location fo the second label
        constraints.gridx = 6;
        constraints.gridy = 0;
        
        // Adds the second label to the contentPane
        gameContentPane.add(lblName[1], constraints);
        
        // Adds the ActionListener to the button reset
        btnReset.addActionListener((ActionEvent e) -> {
            
            // Resets the game
            gameDone = false;
            initializeBoard();        // Clears the board
            enableButtons();          // Enables the proper buttons
            if (currentPlayer == 1) { // Gives the first move to the loser of the game
                player1Turn();
            } else {
                player2Turn();
            }
            btnReset.setEnabled(false);
            
        });  
        
        // Sets the size of the reset button
        btnReset.setPreferredSize(new Dimension(150, 50));
        
        // Sets the location of the reset button
        constraints.gridx = 1;
        constraints.gridy = 7;
        
        // Disables the reset button by default
        btnReset.setEnabled(false);
        
        // Adds the reset button to the contentPane
        gameContentPane.add(btnReset, constraints);
        
        // Adds the ActionListener to the exit button
        btnExit.addActionListener((ActionEvent e) -> {
            
            // Exits the program
            System.out.println("Exiting game");
            System.exit(0);
               
        });  
        
        // Sets the size of the exit button
        btnExit.setPreferredSize(new Dimension(150, 50));
        
        // Sets the location of the exit button
        constraints.gridx = 5;
        constraints.gridy = 7;
        
        // Adds the exit button to the contentPane
        gameContentPane.add(btnExit, constraints);
        
        // Sets the widows contentPane to gameContentPane
        gameWindow.setContentPane(gameContentPane);
        
        /**
         * Fixes a graphical bug where the buttons would randomly not show up
         * Updating one of the buttons seems to allow all of them to appear every time
         */
        button[0][0].setVisible(true);

    }
    /**
     *  Enables the proper buttons after a piece is placed in the board
     *  Doesn't need any parameters, as it only uses the board state - board[][]
     */
    public static void enableButtons() {
        
        boolean found = false; // Stores whether or not the current height of the column is found
        
        /**
         *  The first loop allows the code to work for each column in the board
         *  The second loop increments downward, meaning that it starts at the maximum value, 
         *  being at the bottom of the board, and works it way up
         *  It checks if the current board position is occupied and the position above it is 
         *  not occupied
         *  If these statements are true then it enables the button above the current position
         */
        for(int column = 0; column < width; column++) {
            for(int row = (height - 1); row >= 0; row--) {
                
                // Sets the current button to false
                button[column][row].setEnabled(false);
                
                try {
                    if (board[column][row] != 0 && board[column][row - 1] == 0) {
                        button[column][row - 1].setEnabled(true);
                        found = true;
                        break;
                    }
                } catch(ArrayIndexOutOfBoundsException e) {
                    found = true;
                    break; 
                }
            }

            // If it did not find any buttons to be valid, the first one must be valid
            if (found == false) {
                button[column][height - 1].setEnabled(true);
            }
            
            // Resets found to false
            found = false;
        }
    }
    /**
     *  Starts or resets the board to being empty
     *  Doesn't need any parameters, only uses statements
     */
    public static void initializeBoard() {
        
        System.out.println("Emptying board...");
        
        /**
         *  Sets each button to having a regular white background 
         *  and sets its board[][] value to 0, or empty
         */
        for(int row = 0; row < height; row++) {
            for(int column = 0; column < width; column++) {
                
                board[column][row] = 0;
                button[column][row].setBackground(Color.white);
                button[column][row].setForeground(Color.white);
                button[column][row].setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
                
            }
        }
    }
    /**
     *  Controls what happens when a board button is pressed
     *  Requires "pick" to have the correct value of the column of pressed button
     */
    public static void buttonAction() {
        int index = height;
        /**
         *  Decrements, meaning it moves upwards on the board stating at the bottom
         *  Checks if lowest slot is occupied and if it isn't, it places the piece there
         *  If the slot is occupied, it tries the one above it, etc.
         */
        while (true) {
            index--;
            if (board[pick][index] == 0) {
                board[pick][index] = currentPlayer;
                
                // Based on the current players turn, it lays either a red or yellow token
                if (currentPlayer == 1) {
                    button[pick][index].setBackground(Color.red);
                    button[pick][index].setForeground(Color.red);
                } else {
                    button[pick][index].setBackground(Color.yellow);
                    button[pick][index].setForeground(Color.yellow);
                }

                break;
            }
        }
        
        // Resets the buttons to include the new information
        enableButtons();
        // Checks if anyone has one the game based on the most recent move
        checkWinner();
        
        // Based on who's turn it is currently, it starts the other players turn
        if (currentPlayer == 1) {
            player2Turn();
        } else if (currentPlayer == 2) {
            player1Turn();
        }
        
    }
    /**
     *  Controls what happens at the start of player1's turn
     *  No parameters needed
     */
    public static void player1Turn() {
        System.out.println(p1Name + "'s turn");
        currentPlayer = 1;  // Sets the active player to player1
        turnNum++;          // Increments the turn counter
    } 
    /**
     *  Controls what happens at the start of player2's turn
     *  Must know whether or not there is a CPU
     */
    public static void player2Turn() {
        System.out.println(p2Name + "'s turn");
        currentPlayer = 2; // Sets the active player to player2
        
        // If the computer player is active, call cmpWait()
        if (cmpPlayer) {
            cmpWait();
        }
        
        turnNum++; // Increments the turn counter
    }
    /**
     *  Checks if a player has one the game
     *  Only uses data stored in board[][], so no parameters are used
     */
    public static void checkWinner() {
        
        // Represents if someone has won
        boolean winner = true;
        
        // Checks every point on the board
        for (int column = 0; column < width; column++) {
            for (int row = 0; row < height; row++) {
                for (int dir = 0; dir < 4; dir++) {
                    /**
                     *  "dir" is the direction of the line, which refers to -
                     *   Down
                     *   Right
                     *   Diagonal Up Right
                     *   Diagonal Down Right
                     */
                    
                    winner = true;
                    
                    for (int num = 0; num < 4; num++) {
                        /**
                         *  "num" is the number in the line that the check is currently on 
                         *  working like this example in the horizontal direction
                         *  0 1 2 3
                         *  
                         *  The loop checks the first index with each subsequent number, determining 
                         *  that if any of them are not the same as the first then a win is not possible
                         *  from this point in this direction
                         */
                        
                        if (board[column][row] != 0) {
                            switch (dir) {
                                case 0: // Diagonal Up Right
                                    try {
                                        if (board[column][row] != board[column + num][row - num]) {
                                            winner = false;
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        winner = false;
                                    }
                                    
                                    break;
                                case 1: // Right
                                    try {
                                        if (board[column][row] != board[column + num][row]) {
                                            winner = false;
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        winner = false;
                                    }

                                    break;
                                case 2: // Diagonal Down Right
                                    try {
                                        if (board[column][row] != board[column + num][row + num]) {
                                            winner = false;
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        winner = false;
                                    }

                                    break;
                                case 3: // Down
                                    try {
                                        if (board[column][row] != board[column][row + num]) {
                                            winner = false;
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        winner = false;
                                    }

                                    break;
                                default:
                                    break;
                                    
                            }
                        
                        } else {
                            winner = false;
                        } 
                    }
                    if (winner) { // If someone has won the game
                        
                        if (currentPlayer == 1) { // If the current turn is player1's then they won
                            System.out.println(p1Name + " wins!");
                            p1Wins++;
                            lblName[0].setText(p1Name + " - " + p1Wins);
                        } else { // If the current turn is player2's then they won
                            System.out.println(p2Name + " wins!");
                            p2Wins++;
                            lblName[1].setText(p2Wins + " - " + p2Name);
                        }
                        
                        // Resets the turn counter
                        turnNum = -1;
                        
                        // Disables the board
                        for(int column1 = 0; column1 < width; column1++) {
                            for(int row1 = (height - 1); row1 >= 0; row1--) {
                            button[column1][row1].setEnabled(false);
                        }}
                        
                        // Ends the game
                        gameDone = true;
                        
                        // Enables the reset button so the user can play again
                        btnReset.setEnabled(true);
                        
                        break;
                    }
                    
                }
                if (winner) {
                    break;
                }
            }
            if (winner) {
                break;
            }
        }
        
        // Checks if the game is a draw
        if (turnNum == 42) {
            
            // Resets the turn counter
            turnNum = 0;
            System.out.println("Its a draw!");
            
            // Disables the board
            for(int column = 0; column < width; column++) {
                for(int row = (height - 1); row >= 0; row--) {
                    button[column][row].setEnabled(false);
            }}
            
            // Enables the reset button so the user can play again
            btnReset.setEnabled(true);
            
        }
        
    }
    /**
     *  Controls the small wait that occurs when the CPU is taking its turn
     *  The subroutine requires gameDone to know whether or not the CPU should take its turn
     */
    public static void cmpWait() {
        // Only runs if the game is not over
        if (!gameDone) {
            
            // Disables all the buttons so that you can't continue playing during the CPU turn
            for(int column = 0; column < width; column++) {
                for(int row = (height - 1); row >= 0; row--) {
                    button[column][row].setEnabled(false);
            }}
            
            // Creates the ActionListener for the timer
            ActionListener aL;
            aL = (ActionEvent evt) -> {
                cmpPlayerControl();
            };
            
            // Sets properties of the timer and starts it up
            timer = new Timer(1000, aL);
            timer.setRepeats(false);
            timer.start();
        }
    }
    /**
     *  Controls where the computer places its piece in the board
     *  Doesn't have any parameters
     */
    public static void cmpPlayerControl() {
        
        // The current choice for which column the CPU will place its piece
        int current;
        
        // Randomly chooses a number from 0 - 6, corresponding to the index of the board columns
        current = (int)(Math.random() * 7);
        
        int index = height;
        while (true) {
            index--;
            
            if (board[current][index] == 0) {
                board[current][index] = 2;
                button[current][index].setBackground(Color.yellow);
                break;
            } 
            
            if (index == 0) {
                // Resets the loop and chooses a new column if it previously chose an full column
                index = height;
                current = (int)(Math.random() * 7);
            }
        }
        
        // Enables the proper buttons for the next turn
        enableButtons();
        // Checks if anyone has won the game 
        checkWinner();
        // Starts player1's turn
        player1Turn();
    }
}
