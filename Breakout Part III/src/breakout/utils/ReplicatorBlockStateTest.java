package breakout.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import breakout.radioactivity.*;
import breakout.*;

class ReplicatorBlockStateTest {
	Point p11;
	Point p05;
	Point p38;
	Point pm14;

	Rect r1138;
	Rect rm1438;

	BlockState b1;

	@BeforeEach
	void setUp() throws Exception {
		p11 = new Point(1, 1);
		p05 = new Point(0, 5);
		p38 = new Point(3, 8);
		pm14 = new Point(-1, 4);
		r1138 = new Rect(p11, p38);
		rm1438 = new Rect(pm14, p38);
		b1 = new ReplicatorBlockState(r1138);
	}

	@Test
	void testBlock() {
		assertEquals(r1138, b1.getLocation());
	}

	@Test
	void testBlockStateAfterHit() {
		assertEquals(null, b1.blockStateAfterHit());
	}

	@Test
	void testBallStateAfterHit() {
		Ball b = new NormalBall(new Circle(p05,2), new Vector(10,10)); 
		assertEquals(b, b1.ballStateAfterHit(b));
	}

	@Test
	void testPaddleStateAfterHit() {
		PaddleState p = new NormalPaddleState(pm14); 
		assertEquals(ReplicatingPaddleState.class, b1.paddleStateAfterHit(p).getClass());
		assertEquals(p.getCenter(), b1.paddleStateAfterHit(p).getCenter());
	}

	@Test
	void testGetColor() {
		assertEquals(new Color(0xcf, 0x5e, 0x51), b1.getColor());
	}
}
