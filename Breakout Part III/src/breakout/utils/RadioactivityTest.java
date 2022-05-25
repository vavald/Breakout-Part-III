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
		
		Alpha alpha1 = fac.createAlpha(defPoint, 700, new Vector(5, 0));
		Ball ball = fac.createSuperchargedBall(new Point(2000, 5000), 700, new Vector(5, 0), 10000);
		Vector oldBallSpeed = fac.getVelocity(ball);
		ball.linkTo(alpha1);
		BlockState tlBlock = fac.createNormalBlockState(origin, new Point(5000, 3750));
		PaddleState paddle = fac.createNormalPaddleState(new Point(2500, 8000));
		BreakoutState state = fac.createBreakoutState(new Alpha[] {alpha1}, new Ball[] {}, new BlockState[] {tlBlock}, BR, paddle);
		Rect rightWall = new Rect(new Point(BR.getX(), 0),
				new Point(BR.getX() + 1000, BR.getY()));
		
		assertFalse(alpha1.collidesWith(rightWall));
//		alpha1.hitWall(rightWall);
//
//		assertNotEquals( oldBallSpeed, ball.getVelocity() ); //speeds have been mirrored.
		
		
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
