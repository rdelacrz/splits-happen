package test;

import static org.junit.Assert.*;

import org.junit.Test;

import main.BowlingScore;

public class TestBowlingScore {

	@Test
	public void testSimpleSpare() {
		BowlingScore game = new BowlingScore();
		try {
			game.processSpare();
			fail("A spare cannot be processed without at least one score!");
		} catch (IllegalStateException e) {
			// Now continue with new game
			game = new BowlingScore();
		}
		
		// Hit 2 pins, then 8 for a spare
		game.processNumber(2);
		game.processSpare();
		assertEquals(10, game.calculateTotalScore());
	}

}
