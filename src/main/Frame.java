package main;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the score for a single frame within the game.
 * 
 * @author Roger Delacruz
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
	 * @throws IllegalStateException The number of possible pins that can be knocked down
	 * has been exceeded.
	 */
	public void updateScore(int num) throws IllegalStateException {
		points.add(num);
		if (points.size() >= 2) {
			if (points.get(0) + points.get(1) > NUM_OF_PINS)
				throw new IllegalStateException("Only " + NUM_OF_PINS 
						+ " pins can be knocked down in a single frame.");
			status = Status.COMPLETE;
		}
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
	 * Calculates bonus points obtained from next frame, assuming that the current
	 * frame had a spare scored.
	 * 
	 * @param nextFrame - Next frame to apply spare bonus to.
	 * @return Bonus points from spare.
	 */
	public int getSpareBonus(Frame nextFrame) {
		if (isSpare() && nextFrame != null && nextFrame.getPoints().size() > 0)
			return nextFrame.getPoints().get(0);
		else
			return 0;
	}
	
	/**
	 * Calculates bonus points obtained from next frames, assuming that the current
	 * frame had a strike scored.
	 * 
	 * @param nextFrames - Next frames to apply spare bonus to.
	 * @return Bonus points from strike.
	 */
	public int getStrikeBonus(List<Frame> nextFrames) {
		if (isStrike() && nextFrames != null) {
			int bonus = 0;
			int rolls = 0;
			
			// Gets bonus points from first two rolls (while frames last)
			for (Frame frame : nextFrames) {
				for (Integer rollPoints : frame.getPoints()) {
					bonus += rollPoints;
					rolls += 1;
					if (rolls >= 2)
						break;
				}
				if (rolls >= 2)
					break;
			}
			
			return bonus;
		} else {
			return 0;
		}
	}
	
	/**
	 * Gets number of bowling rolls to knock down pins (0 to 2).
	 * 
	 * @return Number of bowling attempts.
	 */
	public int getRolls() {
		return getPoints().size();
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
	 * Encapsulates code for final frame, which may have up to two bonus frames.
	 */
	public static class FinalFrame extends Frame {
		/** Bonus frames associated with final frame - up to two. **/
		private List<Frame> bonusFrames;
		
		/** New variable for final frame to determine whether frame is finished. **/
		private boolean finished;
		
		/**
		 * Initializes final frame with bonus frames attached.
		 */
		public FinalFrame() {
			super();
			finished = false;
			bonusFrames = new ArrayList<Frame>();
		}
		
		/**
		 * Checks the status of the latest frame for completion.
		 * 
		 * @return True if latest frame (main or bonus frame) has any status other than INCOMPLETE,
		 * false otherwise.
		 */
		private boolean isLatestFrameComplete() {
			if (isIncomplete())
				return false;
			else
				if (bonusFrames.size() == 0)
					return true;
				else
					return !bonusFrames.get(bonusFrames.size() - 1).isIncomplete();
		}
		
		/**
		 * Checks whether the latest bonus frame has no rolls at all.
		 * 
		 * @return True if latest bonus frame has zero rolls, false otherwise.
		 * @throws IllegalStateException No bonus frames to check.
		 */
		private boolean isLatestBonusFrameEmpty() throws IllegalStateException {
			if (bonusFrames.size() == 0)
				throw new IllegalStateException("There are no bonus frames to check!");
			return bonusFrames.get(bonusFrames.size() - 1).points.isEmpty();
		}
		
		/**
		 * Checks the statuses of each of the frames and updates the overall finished status of 
		 * the final frame. If any of the following conditions are met, the final frame's finished 
		 * status will be set to true.
		 * 
		 * 1) The final frame has a status of COMPLETE (implying there will be no bonus frames).
		 * 2) The final frame has a status of SPARE and the first and only bonus frame does NOT 
		 * have a status of INCOMPLETE (implying there will only be one bonus frame).
		 * 3) The final frame has a status of STRIKE and the first and only bonus frame has a
		 * non-INCOMPLETE status that is not a STRIKE (two strikes in a row would imply that there
		 * will be a third roll, meaning the final frame isn't finished yet).
		 * 4) The final frame and first bonus frame have a status of STRIKE, and the second bonus 
		 * frame does NOT have a status of INCOMPLETE (implying there are now three rolls total).
		 */
		private void updateStatus() {
			if (!finished) {
				if (bonusFrames.size() == 0) {
					if (super.status == Status.COMPLETE)
						finished = true;
				} else if (bonusFrames.size() == 1) {
					if (isStrike() && !bonusFrames.get(0).isStrike() && isLatestFrameComplete())
						finished = true;
					else if (isSpare() && !isLatestBonusFrameEmpty()) {
						finished = true;	// Spare + one additional roll
					}
				} else if (bonusFrames.size() == 2) {
					if (isStrike() && bonusFrames.get(0).isStrike() && !isLatestBonusFrameEmpty())
						finished = true;	// 2 strikes + one additional roll
				} else {
					throw new IllegalStateException("The final frame cannot have more than three mini frames!");
				}
			}
		}
		
		/**
		 * Attempts to add bonus frame if the current or latest bonus frame is COMPLETE, 
		 * which will be used for the next roll.
		 */
		private void prepareNextFrame() {
			updateStatus();
			if (!finished && isLatestFrameComplete())
				bonusFrames.add(new Frame());	// Creates new frame
		}
		
		/**
		 * Sets the status of the latest mini frame to a spare, and updates the list of points
		 * accordingly.
		 * 
		 * @throws IllegalStateException A completed final frame cannot have additional rolls.
		 */
		@Override
		public void scoreSpare() throws IllegalStateException {
			if (!finished) {
				if (isIncomplete())
					super.scoreSpare();
				else {
					Frame frame = bonusFrames.get(bonusFrames.size() - 1);
					frame.scoreSpare();
				}
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
			if (!finished) {
				if (isIncomplete())
					super.scoreStrike();
				else {
					Frame frame = bonusFrames.get(bonusFrames.size() - 1);
					frame.scoreStrike();
				}
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
			if (!finished) {
				if (isIncomplete())
					super.updateScore(num);
				else {
					Frame frame = bonusFrames.get(bonusFrames.size() - 1);
					frame.updateScore(num);
				}
				prepareNextFrame();
			} else {
				throw new IllegalStateException("A completed final frame cannot have additional rolls!");
			}
		}
		
		/**
		 * Gets list of points scored within the current Frame, including bonus rolls.
		 * 
		 * @return List of integer values scored.
		 */
		@Override
		public List<Integer> getPoints() {
			List<Integer> totalPoints = new ArrayList<Integer>();
			totalPoints.addAll(super.getPoints());
			for (Frame frame : bonusFrames)
				totalPoints.addAll(frame.getPoints());
			return totalPoints;
		}
		
		/**
		 * Final frame doesn't calculate bonus points from future frames.
		 * 
		 * @param nextFrame - Next frame to apply spare bonus to (will be ignored).
		 * @return 0.
		 */
		@Override
		public int getSpareBonus(Frame nextFrame) {
			return 0;
		}
		
		/**
		 * Final frame doesn't calculate bonus points from future frames.
		 * 
		 * @param nextFrames - Next frame to apply strike bonus to (will be ignored).
		 * @return 0.
		 */
		@Override
		public int getStrikeBonus(List<Frame> nextFrames) {
			return 0;
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
	}
}
