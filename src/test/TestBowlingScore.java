package test;

import static org.junit.Assert.*;

import org.junit.Test;

import main.BowlingScore;

public class TestBowlingScore {

	@Test
	public void testNewGame() {
		BowlingScore game = new BowlingScore();
		assertEquals(1, game.getNumberOfFrames());
	}
	
	@Test
	public void testSpare() {
		BowlingScore game = new BowlingScore();
		
		// Attempts illegal spare
		try {
			game.processSpare();
			fail("A spare cannot be processed without at least one roll!");
		} catch (IllegalStateException e) {
			// Now continue with new game
			game = new BowlingScore();
		}
		
		// Hit 2 pins...
		game.processNumber(2);
		assertEquals(1, game.getNumberOfFrames());
		
		// Then hit 8 for a spare
		game.processSpare();
		assertEquals(10, game.calculateTotalScore());
		assertEquals(1, game.getNumberOfFrames());
		
		// With next roll knocking down 3 pins after spare, total becomes 16
		game.processNumber(3);
		assertEquals(16, game.calculateTotalScore());
		assertEquals(2, game.getNumberOfFrames());
	}

	@Test
	public void testStrike() {
		BowlingScore game = new BowlingScore();
		
		// Attempts illegal strike
		game.processNumber(3);
		try {
			game.processStrike();
			fail("A strike cannot be processed with a non-empty frame!");
		} catch (IllegalStateException e) {
			// Now continue with new game
			game = new BowlingScore();
		}
		
		// Hit strike for 10 points...
		game.processStrike();
		assertEquals(10, game.calculateTotalScore());
		assertEquals(1, game.getNumberOfFrames());
		
		// Then hit 5 and 4 for (10 + 5 + 4) + (5 + 4) = 28
		game.processNumber(5);
		assertEquals(20, game.calculateTotalScore());
		assertEquals(2, game.getNumberOfFrames());
		game.processNumber(4);
		assertEquals(28, game.calculateTotalScore());
		assertEquals(2, game.getNumberOfFrames());
	}
	
	@Test
	public void testSpareAndStrike() {
		BowlingScore game = new BowlingScore();
		
		// Strike followed by a 5 then a spare
		game.processStrike();
		assertEquals(10, game.calculateTotalScore());
		assertEquals(1, game.getNumberOfFrames());
		game.processNumber(5);
		assertEquals(20, game.calculateTotalScore());
		assertEquals(2, game.getNumberOfFrames());
		game.processSpare();
		assertEquals(30, game.calculateTotalScore());
		assertEquals(2, game.getNumberOfFrames());
		
		// Followed by another strike... then another strike
		game.processStrike();
		assertEquals(50, game.calculateTotalScore());
		assertEquals(3, game.getNumberOfFrames());
		game.processStrike();
		assertEquals(70, game.calculateTotalScore());
		assertEquals(4, game.getNumberOfFrames());
		
		// Turkey !!!
		game.processStrike();
		assertEquals(100, game.calculateTotalScore());
		assertEquals(5, game.getNumberOfFrames());
	}
	
	@Test
	public void testMiss() {
		BowlingScore game = new BowlingScore();
		
		// Miss first two shots
		game.processMiss();
		assertEquals(0, game.calculateTotalScore());
		assertEquals(1, game.getNumberOfFrames());
		game.processMiss();
		assertEquals(0, game.calculateTotalScore());
		assertEquals(1, game.getNumberOfFrames());
		
		// Then miss twice again...
		game.processMiss();
		assertEquals(0, game.calculateTotalScore());
		assertEquals(2, game.getNumberOfFrames());
		game.processMiss();
		assertEquals(0, game.calculateTotalScore());
		assertEquals(2, game.getNumberOfFrames());
		
		// Then miss again...
		game.processMiss();
		assertEquals(0, game.calculateTotalScore());
		assertEquals(3, game.getNumberOfFrames());
		
		// Then somehow get a spare...
		game.processSpare();
		assertEquals(10, game.calculateTotalScore());
		assertEquals(3, game.getNumberOfFrames());
		
		// But miss, so spare is useless
		game.processMiss();
		assertEquals(10, game.calculateTotalScore());
		assertEquals(4, game.getNumberOfFrames());
		
		// Hit one pin, but it doesn't get spare bonus
		game.processNumber(1);
		assertEquals(11, game.calculateTotalScore());
		assertEquals(4, game.getNumberOfFrames());
		
		// Lucky strike!
		game.processStrike();
		assertEquals(21, game.calculateTotalScore());
		assertEquals(5, game.getNumberOfFrames());
		
		// Then miss next two...
		game.processMiss();
		assertEquals(21, game.calculateTotalScore());
		assertEquals(6, game.getNumberOfFrames());
		game.processMiss();
		assertEquals(21, game.calculateTotalScore());
		assertEquals(6, game.getNumberOfFrames());
		
		// Miss next 6...
		for (int i = 0; i < 6; i++)
			game.processMiss();
		assertEquals(21, game.calculateTotalScore());
		assertEquals(9, game.getNumberOfFrames());
		assertFalse(game.atFinalFrame());		// False before next roll
		
		// Miss shot on final frame...
		game.processMiss();
		assertEquals(21, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		assertTrue(game.atFinalFrame());		// True now
		
		// Spare next one!
		game.processSpare();
		assertEquals(31, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// Then miss final shot
		game.processMiss();
		assertEquals(31, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// Throws error, since roll was processed with a completed final frame
		try {
			game.processMiss();
			fail("A shot cannot be processed after final frame is completed!");
		} catch (IllegalStateException e) {
			// Success
		}
	}
	
	@Test
	public void testMissEverything() {
		BowlingScore game = new BowlingScore();
		
		// Ten frames of epic fails
		for (int i = 1; i <= 10; i++) {
			game.processMiss();
			assertEquals(0, game.calculateTotalScore());
			assertEquals(i, game.getNumberOfFrames());
			game.processMiss();
			assertEquals(0, game.calculateTotalScore());
			assertEquals(i, game.getNumberOfFrames());
		}
		
		// Throws error, since roll was processed with a completed final frame
		try {
			game.processMiss();
			fail("A shot cannot be processed after final frame is completed!");
		} catch (IllegalStateException e) {
			// Success
		}
	}
	
	@Test
	public void testStrikeEverything() {
		BowlingScore game = new BowlingScore();
		
		// One strike
		game.processStrike();
		assertEquals(10, game.calculateTotalScore());
		assertEquals(1, game.getNumberOfFrames());
		
		// Second strike
		game.processStrike();
		assertEquals(30, game.calculateTotalScore());
		assertEquals(2, game.getNumberOfFrames());
		
		// Third strike (turkey!)
		game.processStrike();
		assertEquals(60, game.calculateTotalScore());
		assertEquals(3, game.getNumberOfFrames());
		
		// Seven more regular strikes
		for (int i = 1; i <= 7; i++) {
			game.processStrike();
			assertEquals(60 + (30 * i), game.calculateTotalScore());
			assertEquals(3 + i, game.getNumberOfFrames());
		}
		
		// Final two bonus strikes
		game.processStrike();
		assertEquals(290, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		game.processStrike();
		assertEquals(300, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// Throws error, since roll was processed with a completed final frame
		try {
			game.processStrike();
			fail("A shot cannot be processed after final frame is completed!");
		} catch (IllegalStateException e) {
			// Success
		}
	}
	
	@Test
	public void testSpareBeforeFinalFrameThenMiss() {
		BowlingScore game = new BowlingScore();
		
		// Score 40 points within first 8 turns
		for (int i = 1; i <= 8; i++) {
			game.processNumber(5);
			assertEquals(i * 5, game.calculateTotalScore());
			assertEquals(i, game.getNumberOfFrames());
			game.processMiss();
			assertEquals(i * 5, game.calculateTotalScore());
			assertEquals(i, game.getNumberOfFrames());
		}
		
		// Score spare
		game.processNumber(5);
		game.processSpare();
		assertEquals(50, game.calculateTotalScore());
		assertEquals(9, game.getNumberOfFrames());
		
		// Follow up with miss in final frame
		game.processMiss();
		assertEquals(50, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// Then regular shot
		game.processNumber(2);
		assertEquals(52, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// Throws error, since roll was processed with a completed final frame
		try {
			game.processNumber(3);
			fail("A shot cannot be processed after final frame is completed!");
		} catch (IllegalStateException e) {
			// Success
		}
	}
	
	@Test
	public void testSpareBeforeFinalFrameThenStrike() {
		BowlingScore game = new BowlingScore();
		
		// Score 40 points within first 8 turns
		for (int i = 1; i <= 8; i++) {
			game.processNumber(5);
			assertEquals(i * 5, game.calculateTotalScore());
			assertEquals(i, game.getNumberOfFrames());
			game.processMiss();
			assertEquals(i * 5, game.calculateTotalScore());
			assertEquals(i, game.getNumberOfFrames());
		}
		
		// Score spare
		game.processNumber(5);
		game.processSpare();
		assertEquals(50, game.calculateTotalScore());
		assertEquals(9, game.getNumberOfFrames());
		
		// Follow up with strike in final frame
		game.processStrike();
		assertEquals(70, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// Then a miss and a spare in bonus frame
		game.processMiss();
		assertEquals(70, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		game.processSpare();
		assertEquals(80, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// Throws error, since roll was processed with a completed final frame
		try {
			game.processNumber(3);
			fail("A shot cannot be processed after final frame is completed!");
		} catch (IllegalStateException e) {
			// Success
		}
	}
	
	@Test
	public void testStrikeBeforeFinalFrameThenMiss() {
		BowlingScore game = new BowlingScore();
		
		// Score 40 points within first 8 turns
		for (int i = 1; i <= 8; i++) {
			game.processNumber(5);
			assertEquals(i * 5, game.calculateTotalScore());
			assertEquals(i, game.getNumberOfFrames());
			game.processMiss();
			assertEquals(i * 5, game.calculateTotalScore());
			assertEquals(i, game.getNumberOfFrames());
		}
		
		// Score strike
		game.processStrike();
		assertEquals(50, game.calculateTotalScore());
		assertEquals(9, game.getNumberOfFrames());
		
		// Follow up with miss in final frame
		game.processMiss();
		assertEquals(50, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// Then regular shot, which still has strike bonus from frame 9
		game.processNumber(2);
		assertEquals(54, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// Throws error, since roll was processed with a completed final frame
		try {
			game.processNumber(3);
			fail("A shot cannot be processed after final frame is completed!");
		} catch (IllegalStateException e) {
			// Success
		}
	}
	
	@Test
	public void testStrikeBeforeFinalFrameThenSpare() {
		BowlingScore game = new BowlingScore();
		
		// Score 40 points within first 8 turns
		for (int i = 1; i <= 8; i++) {
			game.processNumber(5);
			assertEquals(i * 5, game.calculateTotalScore());
			assertEquals(i, game.getNumberOfFrames());
			game.processMiss();
			assertEquals(i * 5, game.calculateTotalScore());
			assertEquals(i, game.getNumberOfFrames());
		}
		
		// Score strike
		game.processStrike();
		assertEquals(50, game.calculateTotalScore());
		assertEquals(9, game.getNumberOfFrames());
		
		// Follow up with three points in final frame
		game.processNumber(3);
		assertEquals(56, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// Then spare (seven more points)
		game.processSpare();
		assertEquals(70, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// One more strike for good measure (spare won't apply bonus to this)
		game.processStrike();
		assertEquals(80, game.calculateTotalScore());
		assertEquals(10, game.getNumberOfFrames());
		
		// Throws error, since roll was processed with a completed final frame
		try {
			game.processNumber(3);
			fail("A shot cannot be processed after final frame is completed!");
		} catch (IllegalStateException e) {
			// Success
		}
	}
}
