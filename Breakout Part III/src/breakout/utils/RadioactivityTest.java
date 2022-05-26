package breakout.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import breakout.BlockState;
import breakout.BreakoutFacade;
import breakout.BreakoutState;
import breakout.PaddleState;
import breakout.radioactivity.Alpha;
import breakout.radioactivity.Ball;

class RadioactivityTest {
	
	static final Point BR = new Point(50000, 30000);
	static final Point origin = new Point(0, 0);
	static final BreakoutFacade fac = new BreakoutFacade();
	static final Point defPoint = new Point(5000, 5000);
	static final Vector downSpeed = new Vector(0, 5);

	@Test
	void Alphatest() {
		Alpha alpha = fac.createAlpha(defPoint.plus(downSpeed), 700, downSpeed);
		assertEquals(alpha.getEcharge(),1);
		assertEquals(alpha.getCenter(),new Point(5000, 5005));
		alpha.setVelocity(new Vector(0, 0));
		assertEquals(alpha.getVelocity(),new Vector(0, 0));
		alpha.setLocation(new Circle(BR, 0));
		assertEquals(alpha.getCenter(), new Point(50000, 30000));
		assertEquals(alpha.getColor(), Color.white);
		
		Alpha alpha1 = fac.createAlpha(new Point(48850,5000), 700, new Vector(5, -1));
		Ball ball = fac.createNormalBall(new Point(48750, 4900), 700, new Vector(4, -2));
		Ball ball2 = fac.createNormalBall(new Point(48750, 5100), 700, new Vector(2, -2));
		Ball ball3 = fac.createNormalBall(new Point(48750, 5200), 700, new Vector(1, -2));
		Vector oldBallSpeed = fac.getVelocity(ball);
		ball.linkTo(alpha1);
		ball2.linkTo(alpha1);
		ball3.linkTo(alpha1);
		//System.out.print(ball.getLinkedAlphas().size()); //size of linkedalphas set  = 1
		//System.out.print(ball.getEcharge()); // ball echarge ==2 ?
		Rect rightWall = new Rect(new Point(BR.getX()-1000, 0),
				new Point(BR.getX(), BR.getY()));
		
		assertEquals(alpha1.getLocation().getOutermostPoint(new Vector(1,0)),new Point(49200,5000)); // 
		
		assertTrue(rightWall.contains(alpha1.getLocation().getOutermostPoint(new Vector(1,0)))); //  Right outermost point of alpha is in wall
		
		assertEquals(rightWall.collideWith(alpha1.getLocation()),new Vector(1,0)); // 
		
		assertTrue(alpha1.collidesWith(rightWall)); //collision between alpha and wall will be true if asked for it 
		
		alpha1.hitWall(rightWall); // collision is true ! 
		
		Vector balltoalpha = new Vector(100,100);
		
		//System.out.print(balltoalpha.product(balltoalpha));

		assertNotEquals( oldBallSpeed, ball.getVelocity() ); //speeds still the same because ball charge == 1
		
		
		//Testing Alpha > hitWall > @post
		
	}
	
	
	@Test
	void SuperChargedBallTest() {
		Ball ball = fac.createSuperchargedBall(new Point(2000, 5000), 700, new Vector(5, 0), -10);
		assertEquals(ball.getColor(), Color.yellow);
		ball.setLocation(new Circle(BR, 0));
		assertEquals(ball.getCenter(), new Point(50000, 30000));

	}

}
