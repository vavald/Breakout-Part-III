package breakout.radioactivity;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import breakout.BreakoutState;
import breakout.utils.Circle;
import breakout.utils.Point;
import breakout.utils.Rect;
import breakout.utils.Vector;
/**
 * @invar | getEcharge() == 1
 * @invar | getLocation() != null
 * @invar | getVelocity() != null
 * 
 * Bidirectional association;
 * The linked balls of this alpha themselves, are linked to this alpha
 * @invar | getLinkedBalls() != null
 * @invar | getLinkedBalls().stream().allMatch(b -> b.getLinkedAlphas().contains(this)
 * 		  | 	&& b != null)
 */
public class Alpha {
	protected Circle location;
	protected Vector velocity;
	private final int eCharge = 1;

	/**
     * @invar | linkedBalls != null
     * @invar | linkedBalls.stream().allMatch(b -> b.getLinkedAlphas().contains(this)
     * 		  | 	&& b != null)
     * @represenationObject
     * @peerObjects
     */
    HashSet<Ball> linkedBalls = new HashSet<>();
    /**
     * @post | result != null
     * 
     * @creates | result
     * @peerObjects
     */
    public Set<Ball> getLinkedBalls() {
    	return Set.copyOf(linkedBalls); }
    
    
    /**
	 * Construct a new alpha at a given `location`, with a given `velocity`.
	 * @pre | location != null
	 * @pre | velocity != null
	 * 
	 * @mutates | this
	 * 
	 * @post | getLocation().equals(location)
	 * @post | getVelocity().equals(velocity)
	 * 
	 * @post | getLinkedBalls().isEmpty()
	 */
    public Alpha(Circle location, Vector velocity) {
		this.location = location;
		this.velocity = velocity;
	}
	
    /**
	 * Return this alpha's location.
	 */
	public Circle getLocation() {
		return location;
	}
	
	/**
	 * Return this alpha's velocity.
	 */
	public Vector getVelocity() {
		return velocity;
	}

	/**
	 * Return the echarge
	 */
	public int getEcharge() {
		return eCharge;
	}
	
	/**
	 * Return this alpha's center.
	 * 
	 * @post | getLocation().getCenter().equals(result)
	 * @inspects this
	 */
	public Point getCenter() {
		return getLocation().getCenter();
	}
	
	/**
	 * @pre | location != null
	 * @post | getLocation() == location
	 */
	public void setLocation(Circle location) {
		this.location = location;
	}

	/**
	 * @pre | velocity != null
	 * @post | getVelocity() == velocity
	 */
	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
	}
	
	/**
	 * Check whether this alpha collides with a given `rect`.
	 * 
	 * @pre | rect != null
	 * @post | result == (rect.collideWith(getLocation()) != null &&
	 * 		 | 			(getVelocity().product(rect.collideWith(getLocation())) > 0))
	 * @inspects this
	 */
	public boolean collidesWith(Rect rect) {
		Vector coldir = rect.collideWith(getLocation());
		return coldir != null && (getVelocity().product(coldir) > 0);
	}
	

	
	/**
	 * Update the BallState after hitting a paddle at a given location.
	 * 
	 * @pre | rect != null
	 * @pre | collidesWith(rect)
	 * @pre | paddleVel != null
	 * @post | getLocation().equals(old(getLocation()))
	 * @mutates this
	 */
	public void hitPaddle(Rect rect, Vector paddleVel) {
		Vector nspeed = bounceOn(rect);
		velocity = nspeed.plus(paddleVel.scaledDiv(5));
	}
	
	/**
	 * Update the alpha after hitting a wall at a given location.
	 * 
	 * @pre | rect != null
	 * @pre | collidesWith(rect)
	 * @post | getLocation().equals(old(getLocation()))
	 * 
//	 *@post Velocity of linked balls gets updated with static, magnet fonction
//	 * 		| getLinkedBalls().stream().allMatch(b1 -> old(getLinkedBalls()).stream().anyMatch(b -> b1.getVelocity().equals( 
//	 * 		| 				Vector.magnetSpeed(old(getCenter()), b.getCenter(), b.getEcharge(), b.getVelocity()))))
	 * 
//	 * @post | true == getLinkedBalls().stream().allMatch(b1 -> old(deepClone()).getLinkedBalls().stream().anyMatch(b -> 
//	 * 		 | 		b1.getVelocity().equals( Vector.magnetSpeed(old(getCenter()), b.getCenter(), b.getEcharge(), b.getVelocity()))))
	 * 
	 * 
	 * @mutates_properties velocity of balls linked with this alpha changes
	 * 		| (...getLinkedBalls()).getVelocity()
	 */
	
	public void hitWall(Rect rect) {
		System.out.print("\n----------------Hitwall---------------------");
		Alpha clone = this.deepClone();
		Point oldGetCenter = getCenter();
		System.out.print("\nvelocity of old balls: " + Arrays.toString((getLinkedBalls().stream().map(b -> b.getVelocity()).toArray())));
//		System.out.print("\nvelocity of old balls + magnetism: " + Arrays.toString((getLinkedBalls().stream().map(b 
//		-> (Vector.magnetSpeed(getCenter(), b.getCenter(), b.getEcharge(), b.getVelocity()))).toArray())));

		velocity = bounceOn(rect);
//		Set<Ball> oldGetLinkedBalls = getLinkedBalls()
//		Point oldGetCenter = getCenter();
		for(Ball ball: getLinkedBalls()) {
			Vector nspeed = Vector.magnetSpeed(this.getCenter(), ball.getCenter(), ball.getEcharge(), ball.getVelocity());
			ball.setVelocity(nspeed);
		}
		//print statements
		System.out.print("\nvelocity of new balls: " + Arrays.toString((getLinkedBalls().stream().map(b -> b.getVelocity()).toArray())));
		System.out.print("\nSize (number of linked balls :" + getLinkedBalls().size());
		
		 
		  boolean boo = getLinkedBalls().stream().allMatch(b1 -> clone.getLinkedBalls().stream().anyMatch(b -> b1.getVelocity().equals( 
		  				Vector.magnetSpeed(oldGetCenter, b.getCenter(), b.getEcharge(), b.getVelocity()))));
		  
		  System.out.print("\nvelocity of old balls + magnetism: " + Arrays.toString((clone.getLinkedBalls().stream().map(b 
					-> (Vector.magnetSpeed(clone.getCenter(), b.getCenter(), b.getEcharge(), b.getVelocity()))).toArray())));
		  
		  
		  System.out.print("\n" + boo);
		  System.out.print("\n" + boo);
		 
		
		  System.out.print("\n--------------------------------------------");
		
		
		
		
	}
	
	/**
	 * @pre | rect != null
	 * @post | (rect.collideWith(getLocation()) == null && result == null) ||
	 *       | (rect.collideWith(getLocation()) != null && getVelocity().product(rect.collideWith(getLocation())) <= 0 && result == null) ||
	 *       | (rect.collideWith(getLocation()) != null && result.equals(getVelocity().mirrorOver(rect.collideWith(getLocation()))))
	 * @inspects this
	 */
	public Vector bounceOn(Rect rect) {
		Vector coldir = rect.collideWith(location);
		if (coldir != null && velocity.product(coldir) > 0) {
			return velocity.mirrorOver(coldir);
		}
		return null;
	}
	
	/**
	 * Move this alpha by the given vector.
	 * 
	 * @pre | v != null
	 * @pre | elapsedTime >= 0
	 * @pre | elapsedTime <= BreakoutState.MAX_ELAPSED_TIME
	 * @post | getLocation().getCenter().equals(old(getLocation()).getCenter().plus(v))
	 * @post | getLocation().getDiameter() == old(getLocation()).getDiameter()
	 * @mutates this
	 */
	public void move(Vector v, int elapsedTime) {
		location = new Circle(getLocation().getCenter().plus(v), getLocation().getDiameter());
	}

	public Color getColor() {
		return Color.white;
	}
	
	/**
	 * Return a clone of this alpha with the given velocity.
	 * 
	 * @inspects this
	 * @creates result
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(v)
	 */
	public Alpha cloneWithVelocity(Vector v) {
		return new Alpha(getLocation(), v);
	}
	
	/**
	 * Transforms this alpha into a ball with velocity and returns it
	 * 
	 * @inspects this
	 * @creates result
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(v)
	 */
	public Ball transformToBallWithVelocity(Vector v) {
		return new NormalBall(getLocation(), v);
	}
	
	
	
	
	/**
	 * Return a clone of this BallState.
	 * 
	 * @inspects this
	 * @creates result
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(getVelocity())
	 */
	public Alpha shallowClone() {
		return cloneWithVelocity(getVelocity());
	}
	
	/**
	 * Return a clone of this Alpha.
	 * 
	 * @inspects this
	 * @creates result
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(getVelocity())
	 * 
	 * Is a deep clone
	 * @post result and this are linked to alphas with same velocity, location and echarge
	 * 		 | (getLinkedBalls().size() == 0) || (
	 * 		 |	getLinkedBalls().stream().allMatch(b1 -> result.getLinkedBalls().stream().anyMatch(b2 -> (b2.getVelocity().equals(b1.getVelocity()))
	 * 		 |											&& (b2.getLocation().getCenter().equals(b1.getLocation().getCenter()))		
	 * 		 | 													)))
	 * 		 |
	 * @post | getLinkedBalls().size() == result.getLinkedBalls().size()
	 */
	public Alpha deepClone() {
//		System.out.print(getLinkedBalls());
		Alpha res = shallowClone();
		for (Ball ball: getLinkedBalls()) {
			Ball cBall = ball.shallowClone();
//			cBall.updateEcharge();
			cBall.linkTo(res);
//		System.out.print(res.getLinkedBalls());
		}
		return res;
	}

	
    
}
	


