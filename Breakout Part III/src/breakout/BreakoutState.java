package breakout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Arrays;

import breakout.utils.*;
import breakout.radioactivity.*;

//import breakout.gui.GameView;

/**
 * Represents the current state of a breakout game.
 * 
 * @invar | getBalls() != null
 * @invar | getBlocks() != null
 * @invar | getPaddle() != null
 * @invar | getBottomRight() != null
 * @invar | Point.ORIGIN.isUpAndLeftFrom(getBottomRight())
 * @invar | Arrays.stream(getBlocks()).allMatch(b -> getField().contains(b.getLocation()))
 * @invar | getField().contains(getPaddle().getLocation())
 */
public class BreakoutState {

	private static final Vector PADDLE_VEL = new Vector(10, 0);
	public static final int MAX_BALL_REPLICATE = 5;
	private static final Vector[] BALL_VEL_VARIATIONS = new Vector[] { new Vector(0, 0), new Vector(2, -2),
			new Vector(-2, 2), new Vector(2, 2), new Vector(-2, -2) };
	public static int MAX_ELAPSED_TIME = 50;
	/**
	 * @invar | bottomRight != null
	 * @invar | Point.ORIGIN.isUpAndLeftFrom(bottomRight)
	 */
	private final Point bottomRight;
	/**
	 * @invar | balls != null
	 * @invar | Arrays.stream(balls).allMatch(b -> getFieldInternal().contains(b.getLocation()))
	 * @representationObject
	 * @representationObjects Each ball is a representation object
	 */
	private Ball[] balls;
	/**
	 * @invar | alphas !=null
	 * @invar | Arrays.stream(alphas).allMatch(b -> getFieldInternal().contains(b.getLocation()))
	 * @representationObject
	 */
	
	private BlockState[] blocks;
	/**
	 * @invar | paddle != null
	 * @invar | getFieldInternal().contains(paddle.getLocation())
	 */
	private PaddleState paddle;

	private final Rect topWall;
	private final Rect rightWall;
	private final Rect leftWall;
	private final Rect[] walls;
	/**
	 * @representationObject
	 */
	private Alpha[] alphas= {};

	/**
	 * Construct a new BreakoutState with the given balls, blocks, paddle.
	 * 
	 * @throws IllegalArgumentException | balls == null
	 * @throws IllegalArgumentException | blocks == null
	 * @throws IllegalArgumentException | bottomRight == null
	 * @throws IllegalArgumentException | paddle == null
	 * @throws IllegalArgumentException | !Point.ORIGIN.isUpAndLeftFrom(bottomRight)
	 * @throws IllegalArgumentException | !(new Rect(Point.ORIGIN,bottomRight)).contains(paddle.getLocation())
	 * @throws IllegalArgumentException | !Arrays.stream(blocks).allMatch(b -> (new Rect(Point.ORIGIN,bottomRight)).contains(b.getLocation()))
	 * @throws IllegalArgumentException | !Arrays.stream(balls).allMatch(b -> (new Rect(Point.ORIGIN,bottomRight)).contains(b.getLocation()))
	 * @post | Arrays.equals(getBalls(),balls)
	 * @post | Arrays.equals(getBlocks(),blocks)
	 * @post | getBottomRight().equals(bottomRight)
	 * @post | getPaddle().equals(paddle)
	 */
	public BreakoutState(Alpha[] alphas, Ball[] balls, BlockState[] blocks, Point bottomRight, PaddleState paddle) {
		if (balls == null)
			throw new IllegalArgumentException();
		if (blocks == null)
			throw new IllegalArgumentException();
		if (bottomRight == null)
			throw new IllegalArgumentException();
		if (paddle == null)
			throw new IllegalArgumentException();

		if (!Point.ORIGIN.isUpAndLeftFrom(bottomRight))
			throw new IllegalArgumentException();
		this.bottomRight = bottomRight;
		if (!getFieldInternal().contains(paddle.getLocation()))
			throw new IllegalArgumentException();
		if (!Arrays.stream(blocks).allMatch(b -> getFieldInternal().contains(b.getLocation())))
			throw new IllegalArgumentException();
		if (!Arrays.stream(balls).allMatch(b -> getFieldInternal().contains(b.getLocation())))
			throw new IllegalArgumentException();

		// balls.clone() does a shallow copy by default
		this.balls = new Ball[balls.length];
		for(int i = 0; i < balls.length; ++i) {
			this.balls[i] = balls[i].clone();
		}
		this.alphas = alphas; 
		
		this.blocks = blocks.clone();
		this.paddle = paddle;

		this.topWall = new Rect(new Point(0, -1000), new Point(bottomRight.getX(), 0));
		this.rightWall = new Rect(new Point(bottomRight.getX(), 0),
				new Point(bottomRight.getX() + 1000, bottomRight.getY()));
		this.leftWall = new Rect(new Point(-1000, 0), new Point(0, bottomRight.getY()));
		this.walls = new Rect[] { topWall, rightWall, leftWall };
	}

	/**
	 * Return the balls of this BreakoutState.
	 *
	 * @creates result
	 */
	public Ball[] getBalls() {
		Ball[] res = new Ball[balls.length];
		for (int i = 0 ; i < balls.length ; ++i) {
			res[i] = balls[i].cloneWithVelocity_and_alphas(balls[i].getVelocity(),balls[i].getLinkedAlphas());
		}
		return res; 
	}
	/**
	 * Function to retrieve all alphas currently in the game
	 * @creates result
	 */
	public Alpha[] getAlphas() {
		if (balls.length==0) {
			return alphas;
		}
		else {
			List<Alpha> alphas = Arrays.asList(balls).stream().flatMap(b -> 
			b.getLinkedAlphas().stream()).collect(Collectors.toList());									
			Alpha[] res = new Alpha[alphas.size()];
				int i = 0;
				for (Alpha alpha : alphas) {
					res[i] = (Alpha) alpha.clone();
					i += 1;
				}
				return res;
			}

	}
	

	/**
	 * Return the blocks of this BreakoutState.
	 *
	 * @creates result
	 */
	public BlockState[] getBlocks() {
		return blocks.clone();
	}

	/**
	 * Return the paddle of this BreakoutState.
	 */
	public PaddleState getPaddle() {
		return paddle;
	}

	/**
	 * Return the point representing the bottom right corner of this BreakoutState.
	 * The top-left corner is always at Coordinate(0,0).
	 */
	public Point getBottomRight() {
		return bottomRight;
	}

	// internal version of getField which can be invoked in partially inconsistent
	// states
	private Rect getFieldInternal() {
		return new Rect(Point.ORIGIN, bottomRight);
	}

	/**
	 * Return a rectangle representing the game field.
	 * 
	 * @post | result != null
	 * @post | result.getTopLeft().equals(Point.ORIGIN)
	 * @post | result.getBottomRight().equals(getBottomRight())
	 */
	public Rect getField() {
		return getFieldInternal();
	}

	private void bounceWalls(Ball ball) {
		for (Rect wall : walls) {
			if (ball.collidesWith(wall)) {
				ball.hitWall(wall);
			}
		}
	}
	
	private void bounceWalls(Alpha alpha) {
		for (Rect wall : walls) {
			if (alpha.collidesWith(wall)) {
				alpha.hitWall(wall);
			}
		}
	}
	

	private Ball removeDead(Ball ball) {
		if( ball.getLocation().getBottommostPoint().getY() > bottomRight.getY()) { return null; }
		else { return ball; }
	}
	
	private Alpha removeDead(Alpha alpha) {
		if( alpha.getLocation().getBottommostPoint().getY() > bottomRight.getY()) { 
			for(Ball ball:alpha.getLinkedBalls()) {
				ball.unLink(alpha);
			}
			return null; 
			}
		else { return alpha; }
	}

	private void clampBall(Ball b) {
		Circle loc = getFieldInternal().constrain(b.getLocation());
	    b.move(loc.getCenter().minus(b.getLocation().getCenter()),0);
	}
	
	private void clampAlpha(Alpha a) {
		Circle loc = getFieldInternal().constrain(a.getLocation());
	    a.move(loc.getCenter().minus(a.getLocation().getCenter()),0);
	}
	
	
	private Ball collideBallBlocks(Ball ball) {
		for (BlockState block : blocks) {
			if (ball.collidesWith(block.getLocation())) {
				boolean destroyed = hitBlock(block);
				ball.hitBlock(block.getLocation(), destroyed);
				paddle = block.paddleStateAfterHit(paddle);
				return block.ballStateAfterHit(ball);
			}
		}
		return ball;
	}

	private boolean hitBlock(BlockState block) {
		boolean destroyed = true;
		ArrayList<BlockState> nblocks = new ArrayList<BlockState>();
		for (BlockState b : blocks) {
			if (b != block) {
				nblocks.add(b);
			} else {
				BlockState nb = block.blockStateAfterHit();
				if (nb != null) {
					nblocks.add(nb);
					destroyed = false;
				}
			}
		}
		blocks = nblocks.toArray(new BlockState[] {});
		return destroyed;
	}

	/**
	 * Move all moving objects one step forward.
	 * 
	 * @mutates this
	 * @mutates ...getBalls(), getAlphas()
	 * @pre | elapsedTime >= 0
	 * @pre | elapsedTime <= MAX_ELAPSED_TIME
	 */
	public void tick(int paddleDir, int elapsedTime) {
		stepBalls(elapsedTime);
		stepAlphas(elapsedTime);
		bounceBallsOnWalls();
		bounceAlphasOnWalls();
		removeDeadBalls();
		removeDeadAlphas();
		bounceBallsOnBlocks();
		bounceBallsOnPaddle(paddleDir);
		bounceAlphasOnPaddle(paddleDir);
		clampBalls();
		clampAlphas();
		balls = Arrays.stream(balls).filter(x -> x != null).toArray(Ball[]::new);
		alphas = Arrays.stream(alphas).filter(x -> x != null).toArray(Alpha[]::new);
	}

	private void clampBalls() {
		for(int i = 0; i < balls.length; ++i) {
			if(balls[i] != null) {
				clampBall(balls[i]);
			}		
		}
	}
	
	private void clampAlphas() {
		for(int i = 0; i < alphas.length; ++i) {
			if(alphas[i] != null) {
				clampAlpha(alphas[i]);
			}		
		}
	}
	

	private void collideBallPaddle(Ball ball, Vector paddleVel) {
		if (ball.collidesWith(paddle.getLocation())) {
			ball.hitPaddle(paddle.getLocation(),paddleVel);
			
			//Creation of Alpha particles !!!
			Alpha[] new_alphas= new Alpha[alphas.length+1];
			for (int i = 0; i < alphas.length; i++)
	            new_alphas[i] = alphas[i];
			Alpha new_alpha = new Alpha(ball.getLocation(),paddleVel.plus(new Vector(-2,-2)));
			new_alphas[new_alphas.length-1]=new_alpha;
			ball.linkTo(new_alpha);
			this.alphas=new_alphas;
			
			int nrBalls = paddle.numberOfBallsAfterHit();
			if(nrBalls > 1) {
				Ball[] curballs = balls;
				balls = new Ball[curballs.length + nrBalls - 1];
				for(int i = 0; i < curballs.length; ++i) {
					balls[i] = curballs[i];
				}
				for(int i = 1; i < nrBalls; ++i) {
					Vector nballVel = ball.getVelocity().plus(BALL_VEL_VARIATIONS[i]);
					balls[curballs.length + i -1] = ball.cloneWithVelocity_and_alphas(nballVel, Collections.emptySet());					
				}
			}
			paddle = paddle.stateAfterHit();
		}
	}
	
	private void collideAlphaPaddle(Alpha alpha, Vector paddleVel) {
		if (alpha.collidesWith(paddle.getLocation())) {
			alpha.hitPaddle(paddle.getLocation(),paddleVel);
			//anti-radioactivity -> spwan ball linked to this alpha. Update balls field
			Ball[] curballs = balls;
			balls = new Ball[balls.length+1];
			for(int i = 0; i < balls.length-1; ++i) {
				balls[i] = curballs[i];
			}
			//create ball with nspeed vector
			Vector nspeed = alpha.getVelocity().plus(new Vector(-2, -2));			
			Ball antiball = alpha.transformToBallWithVelocity(nspeed);
			//link ball to alpha
			antiball.linkTo(alpha);
			balls[balls.length-1] = antiball;
		}
	}
	

	private void bounceBallsOnPaddle(int paddleDir) {
		Vector paddleVel = PADDLE_VEL.scaled(paddleDir);
		Ball[] balls = this.balls; 
		for(int i = 0; i < balls.length; ++i) {
			if(balls[i] != null) {
				collideBallPaddle(balls[i], paddleVel);
			}
		}
	}
	private void bounceAlphasOnPaddle(int paddleDir) {
		Vector paddleVel = PADDLE_VEL.scaled(paddleDir);
		Alpha[] alphas = this.alphas; 
		for(int i = 0; i < alphas.length; ++i) {
			if(alphas[i] != null) {
				collideAlphaPaddle(alphas[i], paddleVel);
			}
		}
	}

	private void bounceBallsOnBlocks() {
		for(int i = 0; i < balls.length; ++i) {
			if(balls[i] != null) {
				balls[i] = collideBallBlocks(balls[i]);
			}
		}
	}

	private void removeDeadBalls() {
		for(int i = 0; i < balls.length; ++i) {
			balls[i] = removeDead(balls[i]);
		}
	}
	
	private void removeDeadAlphas() {
		for(int i = 0; i < alphas.length; ++i) {
			alphas[i] = removeDead(alphas[i]);
		}
	}

	private void bounceBallsOnWalls() {
		for(int i = 0; i < balls.length; ++i) {
			bounceWalls(balls[i]);
		}
	}
	
	private void bounceAlphasOnWalls() {
		for(int i = 0; i < alphas.length; ++i) {
			bounceWalls(alphas[i]);
		}
	}

	private void stepBalls(int elapsedTime) {
		for(int i = 0; i < balls.length; ++i) {
			balls[i].move(balls[i].getVelocity().scaled(elapsedTime), elapsedTime);
		}
	}
	
	private void stepAlphas(int elapsedTime) {
		for(int i = 0; i < alphas.length; ++i) {
			alphas[i].move(alphas[i].getVelocity().scaled(elapsedTime), elapsedTime);
		}
	}
	
	/**
	 * Move the paddle right.
	 * 
	 * @param elapsedTime
	 * 
	 * @mutates this
	 */
	public void movePaddleRight(int elapsedTime) {
		paddle = paddle.move(PADDLE_VEL.scaled(elapsedTime), getField());
	}

	/**
	 * Move the paddle left.
	 * 
	 * @mutates this
	 */
	public void movePaddleLeft(int elapsedTime) {
		paddle = paddle.move(PADDLE_VEL.scaled(-elapsedTime), getField());
	}

	/**
	 * Return whether this BreakoutState represents a game where the player has won.
	 * 
	 * @post | result == (getBlocks().length == 0 && !isDead())
	 * @inspects this
	 */
	public boolean isWon() {
		return getBlocks().length == 0 && !isDead();
	}

	/**
	 * Return whether this BreakoutState represents a game where the player is dead.
	 * 
	 * @post | result == (getBalls().length == 0)
	 * @inspects this
	 */
	public boolean isDead() {
		return getBalls().length == 0;
	}


}
