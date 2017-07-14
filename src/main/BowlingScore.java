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
	private static final int MAX_FRAMES = 10;
	
	/* Constants should be place before this line */
	
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
	 * Housekeeping method that should be called BEFORE any input is processed. Checks the
	 * current status of the frame, then creates a new frame and updates the index if the 
	 * current frame is no longer incomplete.
	 */
	private void updateFrames() {
		Frame currFrame = frames.get(updateIndex);
		if (!currFrame.isIncomplete() && !currFrame.isFinalFrame()) {
			if (++updateIndex < MAX_FRAMES - 1)
				frames.add(new Frame());
			else
				frames.add(new FinalFrame());
		}
	}
	
	/**
	 * Processes number and updates frames accordingly.
	 * 
	 * @param number - Number of pins knocked down.
	 * @throws IllegalStateException The number of possible pins that can be knocked 
	 * down has been exceeded.
	 */
	public void processNumber(int number) throws IllegalStateException {
		updateFrames();
		
		// Processes the number of pins knocked down within the current frame
		Frame currFrame = frames.get(updateIndex);
		currFrame.updateScore(number);
	}
	
	/**
	 * Processes miss and updates frames accordingly.
	 */
	public void processMiss() {
		updateFrames();
		
		// Processes miss (0 pins knocked down) within the current frame
		Frame currFrame = frames.get(updateIndex);
		currFrame.updateScore(0);
	}
	
	/**
	 * Processes spare and updates frames accordingly.
	 * 
	 * @throws IllegalStateException Spare can only be performed on a frame with 
	 * one roll.
	 */
	public void processSpare() throws IllegalStateException {
		updateFrames();
		
		// Processes spare within the current frame
		Frame currFrame = frames.get(updateIndex);
		currFrame.scoreSpare();
	}
	
	/**
	 * Processes strike and updates frames accordingly.
	 * 
	 * @throws IllegalStateException Strike can only be performed on empty frame.
	 */
	public void processStrike() throws IllegalStateException {
		updateFrames();
		
		// Processes strike within the current frame
		Frame currFrame = frames.get(updateIndex);
		currFrame.scoreStrike();
	}
	
	/**
	 * Gets number of frames in the game so far.
	 * 
	 * @return Number of frames.
	 */
	public int getNumberOfFrames() {
		return frames.size();
	}
	
	/**
	 * Determines whether game is at final frame or not.
	 * 
	 * @return True if game is at final frame, false otherwise.
	 */
	public boolean atFinalFrame() {
		return frames.get(updateIndex).isFinalFrame();
	}
	
	/**
	 * Calculates the total score of the current game by iterating through the
	 * frames and performing calculations accordingly.
	 * 
	 * @return Total score after bonus points from spares and strikes are factored.
	 */
	public int calculateTotalScore() {
		int total = 0;
		
		// Iterate through the points of every frame and sum them up
		for (int i = 0; i < frames.size(); i++) {
			Frame frame = frames.get(i);
			int extra = 0;
			
			// Checks for potential spares and strikes
			if (frame.isSpare()) {
				if (i + 1 < frames.size())
					extra += frame.getSpareBonus(frames.get(i + 1));
			} else if (frame.isStrike()) {
				List<Frame> nextFrames = new ArrayList<Frame>();
				
				// Adds up to two frames to the frame list to process
				if (i + 1 < frames.size())
					nextFrames.add(frames.get(i + 1));
				if (i + 2 < frames.size())
					nextFrames.add(frames.get(i + 2));
				
				extra += frame.getStrikeBonus(nextFrames);
			}
			
			// Calculates points up to current frame
			for (Integer point : frame.getPoints())
				total += point;
			total += extra;
		}
		
		return total;
	}
}
