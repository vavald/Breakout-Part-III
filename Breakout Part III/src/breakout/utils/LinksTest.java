package breakout.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

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
		
		//Link testing
		assertEquals(b1.getEcharge(), 1);		//##
		b1.linkTo(a1);
		assertEquals(b1.getEcharge(), -1);		//## one connected alpha > uneven > negatif && alphas have max 1 connection...
		b1.linkTo(a2);
		b1.linkTo(a2);                           // Has no impact !!
		assertEquals(b1.getEcharge(), 1);		//##
		assertEquals(b1.getLinkedAlphas().size(), 2);
		assertTrue(a1.getLinkedBalls().contains(b1));
		assertTrue(a2.getLinkedBalls().contains(b1));
		
		//unLink testing
		b1.unLink(a2);
		b1.unLink(a2);
		assertEquals(b1.getEcharge(), -1);		//##
		assertEquals(b1.getLinkedAlphas().size(), 1);
		assertFalse(a2.getLinkedBalls().contains(b1));
		
		System.out.print("step 2");
		b2.linkTo(a3);
		b2.linkTo(a4);
		b3.linkTo(a4);
		assertEquals(b2.getEcharge(), 2);
		
		//Test links when cloning alpha
		Alpha a4Clone = a4.deepClone();
		System.out.print(a4Clone.getLinkedBalls());
		assertEquals(a4Clone.getLinkedBalls().size(), 2);
		assertTrue(a4Clone.getLinkedBalls().stream().anyMatch(b -> 
							b.getVelocity().equals(b2.getVelocity())));
		
		assertEquals((a4.getLinkedBalls().stream().map(b -> b.getEcharge()).collect(Collectors.toSet())),
				new HashSet<>(Arrays.asList(2, -2)));
		
		//Clone has wrong values for eCharge !!!
		assertEquals((a4Clone.getLinkedBalls().stream().map(b -> b.getEcharge()).collect(Collectors.toSet())),
				"nonce");
		//Two times -2, so set only gives back -2 !!!
		
		
		
		
		
		
		
	}

}
