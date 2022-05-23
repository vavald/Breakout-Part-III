package other;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import breakout.BreakoutFacade;
import breakout.radioactivity.*;
import breakout.utils.Point;
import breakout.utils.Vector;

class LinksTest {
	//Parameters to define balls & alphas
	static final BreakoutFacade fac = new BreakoutFacade();
	static final Point defPoint = new Point(5000, 5000);
	static final Vector downSpeed = new Vector(0, 5);
	
	@Test
	void test() {
		//Create balls & alphas
		Ball b1 = fac.createNormalBall(defPoint, 700, downSpeed);
		Ball b2 = fac.createNormalBall(defPoint, 700, downSpeed);
		Ball b3 = fac.createNormalBall(defPoint, 700, downSpeed);
		
		Alpha a1 = fac.createAlpha(defPoint, 700, downSpeed);
		Alpha a2 = fac.createAlpha(defPoint, 700, downSpeed);
		Alpha a3 = fac.createAlpha(defPoint, 700, downSpeed);
		Alpha a4 = fac.createAlpha(defPoint, 700, downSpeed);
		
		b1.linkTo(a1);
		b1.linkTo(a2);
		assertEquals(b1.getLinkedAlphas().size(), 2);
		assertTrue(a1.getLinkedBalls().contains(b1));
		assertTrue(a2.getLinkedBalls().contains(b1));
	}

}
