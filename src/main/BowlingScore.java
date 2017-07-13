package main;

import java.util.ArrayList;
import java.util.List;

import main.Frame.FinalFrame;

/**
 * A class that encapsulates the variables and methods that are used to simulate a single game 
 * of American Ten-Pin Bowling. It will read in a sequence of rolls and produce the total score of
 * the game at the end.
 * 
 * @author Roger Delacruz
 */
public class BowlingScore {
	
	/** Maximum number of frames in a game. **/
	private static int MAX_FRAMES = 10;
	
	
	/** Gets index for frame updates. **/
	private int updateIndex;
	
	/** List of frames. **/
	private List<Frame> frames;
	
	/**
	 * Initializes the frames for the start of the game.
	 */
	public BowlingScore() {
		updateIndex = 0;
		frames = new ArrayList<Frame>(MAX_FRAMES);	// Sets array list with max capacity
		frames.add(new Frame());		// First frame
	}
	
	/**
	 * Processes number and updates frames accordingly.
	 * 
	 * @param number - Number of pins knocked down.
	 */
	public void processNumber(int number) {
		Frame frame = frames.get(updateIndex);
		
		if (frame.isIncomplete()) {
			
		}
	}
	
}
