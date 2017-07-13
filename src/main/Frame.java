package main;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the score for a single frame within the game.
 */
public class Frame  {

	/**
	 * Identifies a score type as either an INCOMPLETE score, a REGULAR score (when two attempts have
	 * been performed to knock down the pins), a SPARE (when 10 pins are knocked down within a single 
	 * frame in two tries), or a STRIKE (when 10 pins are knocked down within a single try).
	 */
	private static enum ScoreType { INCOMPLETE, REGULAR, SPARE, STRIKE }
	
	/** Number of pins that can be knocked down in a single frame. **/
	private static int NUM_OF_PINS = 10;
	
	
	/** List of points scored within the current frame. **/
	private List<Integer> points;
	
	/** Type of score within the frame (Regular, Spare, Strike). **/
	private ScoreType scoreType;
	
	/**
	 * Initializes the frame.
	 */
	public Frame() {
		points = new ArrayList<Integer>();
		scoreType = ScoreType.INCOMPLETE;
	}
	
	/**
	 * Checks for an incomplete score.
	 * 
	 * @return True if score is incomplete, false otherwise.
	 */
	public boolean isIncomplete() {
		return scoreType == ScoreType.INCOMPLETE;
	}
	
	/**\
	 * Checks for a spare within the current frame.
	 * 
	 * @return True if score type is a spare, false otherwise.
	 */
	public boolean isSpare() {
		return scoreType == ScoreType.SPARE;
	}
	
	/**
	 * Checks for a strike within the current frame.
	 * 
	 * @return True if score type is a strike, false otherwise.
	 */
	public boolean isStrike() {
		return scoreType == ScoreType.STRIKE;
	}
	
	/**
	 * Sets the score type of the current frame to a spare. and updates the list of points
	 * accordingly.
	 */
	public void scoreSpare() {
		scoreType = ScoreType.SPARE;
		if (points.size() != 1)
			throw new IllegalStateException("There should only be one attempt prior to a spare.");
		else
			points.add(NUM_OF_PINS - points.get(0));	// Remaining pins for second attempt
	}
	
	/**
	 * Sets the score type of the current frame to a strike, and updates the list of points
	 * accordingly.
	 */
	public void scoreStrike() {
		scoreType = ScoreType.STRIKE;
		if (points.size() != 0)
			throw new IllegalStateException("There should only be one attempt prior to a spare.");
		else
			points.add(NUM_OF_PINS);	// Strike = all pins
	}
	
	/**
	 * Updates the list of points.
	 * 
	 * @param num - Number of pins knocked down, which will be used to update the list.
	 */
	public void updateScore(int num) {
		points.add(num);
		if (points.size() >= 2)
			scoreType = ScoreType.REGULAR;
	}
	
	/**
	 * Gets list of points scored within the current Frame.
	 * 
	 * @return List of integer values scored.
	 */
	public List<Integer> getPoints() {
		return points;
	}
	
	/**
	 * Gets number of bowling attempts to knock down pins (0 to 2).
	 * 
	 * @return Number of bowling attempts.
	 */
	public int getAttempts() {
		return points.size();
	}
	
	/**
	 * Injects current frame object into given list.
	 * 
	 * @param frameList - List of frames to inject current frame into.
	 */
	public void inject(List<Frame> frameList) {
		if (frameList == null)
			throw new IllegalArgumentException("Frame list cannot be null!");
		else
			frameList.add(this);
	}
	
	/**
	 * Encapsulates code for final frame, which may have up to three mini-frames.
	 */
	public static class FinalFrame extends Frame {
		/** Mini frames associated with final frame - up to three. **/
		private List<Frame> miniFrames;
		
		/**
		 * Initializes final frame with mini frames attached.
		 * 
		 * @param frameNum
		 */
		public FinalFrame() {
			super();
			
			// Initializes mini frames
			miniFrames = new ArrayList<Frame>();
		}
		
		/**
		 * Adds mini frame to the list of frames associated with the final frame.
		 * 
		 * @param miniFrame - Mini frame to be added to the list.
		 */
		public void addMiniFrame(Frame miniFrame) {
			miniFrames.add(miniFrame);
		}
		
		/**
		 * Gets number of bowling attempts to knock down pins (0 to 3).
		 * 
		 * @return Number of bowling attempts.
		 */
		@Override
		public int getAttempts() {
			int num = 0;
			for (Frame miniFrame : miniFrames)
				num += miniFrame.getAttempts();
			return num;
		}
		
		/**
		 * Injects mini frames into given list.
		 * 
		 * @param frameList - List of frames to inject mini frames into.
		 */
		@Override
		public void inject(List<Frame> frameList) {
			for (Frame frame : miniFrames)
				frame.inject(frameList);
		}
	}
}
