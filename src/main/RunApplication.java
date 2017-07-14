package main;
import java.util.Scanner;

/**
 * Main class where input is read from user, parsed. and corresponding output is printed.
 * 
 * @author Roger Delacruz
 */
public class RunApplication {
	/**
	 * Specialized Exception class designed to be thrown when user inputs the "quit" 
	 * or "exit" command. It should only be caught by the end of the main function, 
	 * so that execution can end.
	 */
	private static class QuitException extends Exception {
		private static final long serialVersionUID = 1L;

		private QuitException() {
			super("Quit/Exit command has been invoked by user.");
		}
	}
	
	// For fancy-looking splash text
	private static final String SPLASH = 
			"********************************************************\r" +
			"                                                      \r" +
			"  Bowling Score application written by Roger Delacruz.\r" +
			"                                                      \r" +
			"********************************************************\r";
	
	// Instructions
	private static final String INSTRUCTIONS =
			"This application will read input to simulate a single game of American\r" +
			"Ten-Pin Bowling. Please input a string representing all the frames of a\r" +
			"single game in order to obtain their scores.\r\r" +
			"'X' indicates a strike, '/' indicates a spare, '-' indicates a miss, and\r" +
			"a number indicates the number of pins knocked down in the roll.\r";
	
	// Line separator
	private static final String LINE_SEP = "---------------------------------------------------------";
	
	
	private Scanner scanner;
	private BowlingScore game;
	
	/**
	 * Initializes the built-in scanner which will be used to read user input.
	 */
	public RunApplication() {
		scanner = new Scanner(System.in);		// Reads from standard input
		game = new BowlingScore();
	}
	
	/**
	 * Closes scanner once its usage is no longer needed.
	 */
	public void close() {
		scanner.close();
	}
	
	/**
	 * Wraps the nextLine() function call with a check for the "quit" or "exit" string, and 
	 * throws a QuitException that will be caught by the main function.
	 * 
	 * @return User input string.
	 * @throws QuitException Thrown when user inputs the "quit" or "exit" command.
	 */
	private String readLine() throws QuitException {
		String input = scanner.nextLine().trim();
		if (input.equals("quit") || input.equals("exit"))
			throw new QuitException();
		return input;
	}
	
	/**
	 * Checks given input to see if input string is valid. If any characters other
	 * than "X" (MUST be capital), "/", "-", or numbers 1-9 are read, string is invalid.
	 * 
	 * @return True if input string is valid, false otherwise.
	 */
	private static boolean inputIsValid(String inputStr) {
		for (int i = 0; i < inputStr.length(); i++) {
			char c = inputStr.charAt(i);
			if (c != 'X' && c != '/' && c != '-' && c != '1' && c != '2' 
					&& c != '3' && c != '4' && c != '5' && c != '6' && c != '7' 
					&& c != '8' && c != '9') {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Runs a single game and reads in user input to determine the state of each frame.
	 * Gets the resulting total score of the game.
	 * 
	 * @return Total score of a single game
	 * @throws QuitException The "quit" or "exit" command has been invoked.
	 * @throws IllegalStateException Some illegal operation was attempted within the game.
	 */
	private int getTotalScore() throws QuitException, IllegalStateException {
		int totalScore;
		System.out.print("Enter input: ");
		String inputStr = readLine();
		
		// Prompts user for input until valid input is entered
		while (!inputIsValid(inputStr)) {
			System.out.print("Invalid format, please enter input again: ");
			inputStr = readLine();
		}
		
		// Parses input and runs appropriate bowling methods
		for (int i = 0; i < inputStr.length(); i++) {
			char c = inputStr.charAt(i);
			switch (c) {
				case 'X' :
					game.processStrike();
					break;
				case '/' :
					game.processSpare();
					break;
				case '-' :
					game.processMiss();
					break;
				default :	// Because input is valid, this will always be a number
					int num = Integer.parseUnsignedInt(String.valueOf(c));
					game.processNumber(num);
					break;
			}
		}
		
		// Calculates final score after game is finished
		totalScore = game.calculateTotalScore();
		
		// Starts a new game
		game = new BowlingScore();
		
		return totalScore;
	}

	public static void main(String[] args) {
		RunApplication app = new RunApplication();
		
		// Prints fancy splash text in the beginning, along with instructions
		System.out.println(SPLASH);
		System.out.println(INSTRUCTIONS);
		
		// Wraps all code in try/catch block to handle quit commands
		try {
			// User can exit at any time by typing "quit" or "exit"
			while (true) {
				try {
					int score = app.getTotalScore();
					System.out.println("\rTotal score: " + score + "\r");
					System.out.println(LINE_SEP + "\r");
				} catch (IllegalStateException e) {
					System.out.println("Error: You attempted to perform an illegal operation...");
					app.game = new BowlingScore();		// Resets game state
				}
			}
		} catch (QuitException e) {
			System.out.println("The quit/exit command has been invoked. Exiting application...");
		} finally {
			app.close();		// Should always be closed after being opened
		}
	}
}
