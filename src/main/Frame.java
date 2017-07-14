package main;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the score for a single frame within the game.
 */
public class Frame  {

	/**
	 * Identifies the scoring status as either an INCOMPLETE score, a COMPLETE score (when two 
	 * rolls have been performed), a SPARE (when 10 pins are knocked down within a single 
	 * frame with two rolls), or a STRIKE (when 10 pins are knocked down with a single roll).
	 */
	private static enum Status { INCOMPLETE, COMPLETE, SPARE, STRIKE }
	
	/** Number of pins that can be knocked down in a single frame. **/
	public static final int NUM_OF_PINS = 10;
	
	/* Constants should be place before this line */
	
	/** List of points scored within the current frame. **/
	private List<Integer> points;
	
	/** Status of the frame (Incomplete, Regular, Spare, Strike). **/
	private Status status;
	
	/**
	 * Initializes the frame.
	 */
	public Frame() {
		points = new ArrayList<Integer>();
		status = Status.INCOMPLETE;
	}
	
	/**
	 * Checks for an incomplete frame.
	 * 
	 * @return True if the frame is incomplete, false otherwise.
	 */
	public boolean isIncomplete() {
		return status == Status.INCOMPLETE;
	}
	
	/**
	 * Checks for a spare within the current frame.
	 * 
	 * @return True if a spare was scored, false otherwise.
	 */
	public boolean isSpare() {
		return status == Status.SPARE;
	}
	
	/**
	 * Checks for a strike within the current frame.
	 * 
	 * @return True if a strike was scored, false otherwise.
	 */
	public boolean isStrike() {
		return status == Status.STRIKE;
	}
	
	/**
	 * Sets the status of the current frame to a spare, and updates the list of points
	 * accordingly.
	 * 
	 * @throws IllegalStateException Spare can only be performed on a frame with one roll.
	 */
	public void scoreSpare() throws IllegalStateException {
		status = Status.SPARE;
		if (points.size() != 1)
			throw new IllegalStateException("There should be one roll prior to a spare.");
		else
			points.add(NUM_OF_PINS - points.get(0));	// Remaining pins for second roll
	}
	
	/**
	 * Sets the status of the current frame to a strike, and updates the list of points
	 * accordingly.
	 * 
	 * @throws IllegalStateException Strike can only be performed on empty frame.
	 */
	public void scoreStrike() throws IllegalStateException {
		status = Status.STRIKE;
		if (points.size() != 0)
			throw new IllegalStateException("There should zero rolls prior to a strike.");
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
			status = Status.COMPLETE;
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
	 * Gets number of bowling rolls to knock down pins (0 to 2).
	 * 
	 * @return Number of bowling attempts.
	 */
	public int getRolls() {
		return points.size();
	}
	
	/**
	 * Checks whether current frame is final frame of the game.
	 * 
	 * @return Always false.
	 */
	public boolean isFinalFrame() {
		return false;
	}
	
	/**
	 * Injects current frame object into given list.
	 * 
	 * @param frameList - List of frames to inject current frame into.
	 * @throws IllegalStateException Frame list cannot be null.
	 */
	public void inject(List<Frame> frameList) throws IllegalStateException {
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
		 */
		public FinalFrame() {
			super();
			
			// Initializes mini frames
			miniFrames = new ArrayList<Frame>();
			miniFrames.add(new Frame());		// First frame
		}
		
		/**
		 * Checks for a spare within the first mini frame only.
		 * 
		 * @return True if a spare was scored in the first mini frame, false otherwise.
		 */
		@Override
		public boolean isSpare() {
			return miniFrames.get(0).isSpare();
		}
		
		/**
		 * Checks for a strike within the first mini frame only.
		 * 
		 * @return True if a strike was scored in the first mini frame, false otherwise.
		 */
		@Override
		public boolean isStrike() {
			return miniFrames.get(0).isStrike();
		}
		
		/**
		 * Checks the status of the latest mini frame for completion.
		 * 
		 * @return True if latest mini frame has any status other than INCOMPLETE,
		 * false otherwise.
		 */
		private boolean isLatestMiniFrameComplete() {
			return !miniFrames.get(miniFrames.size() - 1).isIncomplete();
		}
		
		/**
		 * Checks whether the latest mini frame has no rolls at all.
		 * 
		 * @return True if latest mini frame has zero rolls, false otherwise.
		 */
		private boolean isLatestMiniFrameEmpty() {
			return miniFrames.get(miniFrames.size() - 1).points.isEmpty();
		}
		
		/**
		 * Checks the statuses of each of the mini frames and updates the overall status of 
		 * the final frame. If any of the three conditions are met, the final frame status 
		 * will be set to COMPLETE.
		 * 
		 * 1) The first mini frame has a status of COMPLETE (implying there is only one frame).
		 * 2) The first mini frame has a status of SPARE or STRIKE, and the second mini frame
		 * does NOT have a status of INCOMPLETE (implying there are only two frames).
		 * 3) The first two mini frames have a status of STRIKE, and the third mini frame
		 * does NOT have a status of INCOMPLETE (implying there are three frames).
		 */
		private void updateStatus() {
			if (isIncomplete()) {
				if (miniFrames.size() == 1) {
					if (miniFrames.get(0).status == Status.COMPLETE)
						super.status = Status.COMPLETE;
				} else if (miniFrames.size() == 2) {
					if (miniFrames.get(0).isStrike() && !miniFrames.get(1).isStrike() && isLatestMiniFrameComplete())
						super.status = Status.COMPLETE;
					else if (miniFrames.get(0).isSpare() && !isLatestMiniFrameEmpty()) {
						super.status = Status.COMPLETE;		// Spare + one additional roll
					}
				} else if (miniFrames.size() == 3) {
					if (miniFrames.get(0).isStrike() && miniFrames.get(1).isStrike()
							&& !isLatestMiniFrameEmpty())
						super.status = Status.COMPLETE;		// 2 strikes + one additional roll
				} else {
					throw new IllegalStateException("The final frame cannot have more than three mini frames!");
				}
			}
		}
		
		/**
		 * Attempts to add new mini frame if the latest mini frame is COMPLETE, which will be used
		 * for the next roll.
		 */
		private void prepareNextFrame() {
			updateStatus();
			if (isIncomplete() && isLatestMiniFrameComplete())
				miniFrames.add(new Frame());	// Creates new frame
		}
		
		/**
		 * Sets the status of the latest mini frame to a spare, and updates the list of points
		 * accordingly.
		 * 
		 * @throws IllegalStateException A completed final frame cannot have additional rolls.
		 */
		@Override
		public void scoreSpare() throws IllegalStateException {
			if (isIncomplete()) {
				Frame frame = miniFrames.get(miniFrames.size() - 1);
				frame.scoreSpare();
				prepareNextFrame();
			} else {
				throw new IllegalStateException("A completed final frame cannot have additional rolls!");
			}
		}
		
		/**
		 * Sets the status of the latest mini frame to a strike, and updates the list of points
		 * accordingly.
		 * 
		 * @throws IllegalStateException A completed final frame cannot have additional rolls.
		 */
		@Override
		public void scoreStrike() {
			if (isIncomplete()) {
				Frame frame = miniFrames.get(miniFrames.size() - 1);
				frame.scoreStrike();
				prepareNextFrame();
			} else {
				throw new IllegalStateException("A completed final frame cannot have additional rolls!");
			}
		}
		
		/**
		 * Updates the list of points of the latest mini frame.
		 * 
		 * @param num - Number of pins knocked down, which will be used to update the list.
		 * @throws IllegalStateException A completed final frame cannot have additional rolls.
		 */
		@Override
		public void updateScore(int num) {
			if (isIncomplete()) {
				Frame frame = miniFrames.get(miniFrames.size() - 1);
				frame.updateScore(num);
				prepareNextFrame();
			} else {
				throw new IllegalStateException("A completed final frame cannot have additional rolls!");
			}
		}
		
		/**
		 * Gets number of bowling rolls to knock down pins (0 to 3).
		 * 
		 * @return Number of bowling attempts.
		 */
		@Override
		public int getRolls() {
			int num = 0;
			for (Frame miniFrame : miniFrames)
				num += miniFrame.getRolls();
			return num;
		}
		
		/**
		 * Checks whether current frame is final frame of the game.
		 * 
		 * @return Always true.
		 */
		@Override
		public boolean isFinalFrame() {
			return true;
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
