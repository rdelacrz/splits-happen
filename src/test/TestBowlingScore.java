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
		
		// Nine regular frames of strikes, plus three strikes in final frame
		for (int i = 1; i <= 12; i++) {
			game.processStrike();
		}
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
}
