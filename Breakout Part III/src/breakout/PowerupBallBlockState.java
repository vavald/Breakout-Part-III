package breakout;

import java.awt.Color;
import breakout.utils.*;
import breakout.radioactivity.*;

public class PowerupBallBlockState extends NormalBlockState {

	private static final int SUPERCHARGED_BALL_LIFETIME = 10000;
	private static final Color COLOR = new Color(0xff, 0x5e, 0x81);

	public PowerupBallBlockState(Rect location) {
		super(location);
	}
	
	/**
	 * 
	 * @creates | result
	 */
	@Override
	public Ball ballStateAfterHit(Ball ballState) {
		SuperChargedBall new_ball = new SuperChargedBall(ballState.getLocation(), ballState.getVelocity(), SUPERCHARGED_BALL_LIFETIME);
		for(Alpha alpha:ballState.getLinkedAlphas()) {
			new_ball.linkTo(alpha);
		}
		return new_ball;//.cloneWithVelocity_and_alphas(ballState.getVelocity(), ballState.getLinkedAlphas());
	}

	@Override
	public Color getColor() {
		return COLOR;
	}

}
