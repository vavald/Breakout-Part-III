package breakout.radioactivity;

import java.awt.Color;

import breakout.BreakoutState;
import breakout.utils.*;
import logicalcollections.LogicalSet;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents the state of a ball in the breakout game.
 * 
 * @invar | getLocation() != null
 * @invar | getVelocity() != null
 * 
 * Invariants to preserve consistency of bidirectional association;
 * the linked alphas are themeselves linked with this ball
 * @invar | getLinkedAlphas() != null
 * @invar | getLinkedAlphas().stream().allMatch(a -> a.getLinkedBalls().contains(this)
 * 		  | 	&& a != null)
 */
public abstract class Ball {
    int eCharge;
    /**
     * @invar | linkedAlphas != null
     * @invar | linkedAlphas.stream().allMatch(a -> a.getLinkedBalls().contains(this)
     * 		  | 	&& a != null)
     * 
     * @representationObject
     * @peerObjects
     */
    HashSet<Alpha> linkedAlphas = new HashSet<>();
    
    /**
     * @post | result != null
     * 
     * @creates | result
     * @peerObjects
     */
    public Set<Alpha> getLinkedAlphas() {return Set.copyOf(linkedAlphas);}
    
    /**
     * @pre | alpha != null
     * @post | getLinkedAlphas().equals(LogicalSet.plus(old(getLinkedAlphas()), alpha))
     * @post | alpha.getLinkedBalls().equals(LogicalSet.plus(old(alpha.getLinkedBalls()), this))
     * 
     * @mutates_properties | getLinkedAlphas(), alpha.getLinkedBalls()
     */
    
    public void linkTo(Alpha alpha) {
    	linkedAlphas.add(alpha);
    	alpha.linkedBalls.add(this);
    }
    
    /**
     * @pre | alpha != null
     * @post | getLinkedAlphas().equals(LogicalSet.minus(old(getLinkedAlphas()), alpha))
     * @post | alpha.getLinkedBalls().equals(LogicalSet.minus(old(alpha.getLinkedBalls()), this))
     * 
     * @mutates_properties | getLinkedAlphas(), alpha.getLinkedBalls()
     */
    
    public void unLink(Alpha alpha) {
    	linkedAlphas.remove(alpha);
    	alpha.linkedBalls.remove(this);
    }
    	
    
    
    
	protected Circle location;
	protected Vector velocity;

	/**
	 * Construct a new ball at a given `location`, with a given `velocity`.
	 * 
	 * @pre | location != null
	 * @pre | velocity != null
	 * 
	 * @mutates | this
	 * 
	 * @post | getLocation().equals(location)
	 * @post | getVelocity().equals(velocity)
	 * 
	 * @post | getLinkedAlphas().isEmpty()
	 */
	public Ball(Circle location, Vector velocity) {
		this.location = location;
		this.velocity = velocity;
	}

	/**
	 * Return this ball's location.
	 */
	public Circle getLocation() {
		return location;
	}

	/**
	 * Return this ball's velocity.
	 */
	public Vector getVelocity() {
		return velocity;
	}

	/**
	 * Check whether this ball collides with a given `rect` and if so, return the
	 * new velocity this ball will have after bouncing on the given rect.
	 * 
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
	 * Check whether this ball collides with a given `rect`.
	 * 
	 * @pre | rect != null
	 * @post | result == ((rect.collideWith(getLocation()) != null) &&
	 *       |            (getVelocity().product(rect.collideWith(getLocation())) > 0))
	 * @inspects this
	 */
	public boolean collidesWith(Rect rect) {
		Vector coldir = rect.collideWith(getLocation());
		return coldir != null && (getVelocity().product(coldir) > 0);
	}

	/**
	 * Move this BallState by the given vector.
	 * 
	 * @pre | v != null
	 * @pre | elapsedTime >= 0
	 * @pre | elapsedTime <= BreakoutState.MAX_ELAPSED_TIME
	 * @post | getLocation().getCenter().equals(old(getLocation()).getCenter().plus(v))
	 * @post | getLocation().getDiameter() == old(getLocation()).getDiameter()
	 * @mutates this
	 */
	public abstract void move(Vector v, int elapsedTime);

	/**
	 * Update the BallState after hitting a block at a given location, taking into account whether the block was destroyed by the hit or not.
	 * 
	 * @pre | rect != null
	 * @pre | collidesWith(rect)
	 * @post | getLocation().equals(old(getLocation()))
	 * @mutates this
	 */
	public abstract void hitBlock(Rect rect, boolean destroyed);

	/**
	 * Update the BallState after hitting a paddle at a given location.
	 * 
	 * @pre | rect != null
	 * @pre | collidesWith(rect)
	 * @pre | paddleVel != null
	 * @post | getLocation().equals(old(getLocation()))
	 * @mutates this
	 */
	public abstract void hitPaddle(Rect rect, Vector paddleVel);

	/**
	 * Update the BallState after hitting a wall at a given location.
	 * 
	 * @pre | rect != null
	 * @pre | collidesWith(rect)
	 * @post | getLocation().equals(old(getLocation()))
	 * @mutates this
	 */
	public abstract void hitWall(Rect rect);

	/**
	 * Return the color this ball should be painted in.
	 * 
	 * @post | result != null
	 * @inspects this
	 */
	public abstract Color getColor();
	
	/**
	 * Return this point's center.
	 * 
	 * @post | getLocation().getCenter().equals(result)
	 * @inspects this
	 */
	public Point getCenter() {
		return getLocation().getCenter();
	}
	
	/**
	 * Return a clone of this BallState with the given velocity.
	 * 
	 * @inspects this
	 * @creates result
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(v)
	 */
	public abstract Ball cloneWithVelocity(Vector v);
	
	/**
	 * Return a clone of this BallState.
	 * 
	 * @inspects this
	 * @creates result
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(getVelocity())
	 */
	public Ball clone() {
		return cloneWithVelocity(getVelocity());
	}
	
//Do not override equal methods in mutable classes when working with Set
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Ball other = (Ball) obj;
//		if (!getVelocity().equals(other.getVelocity()))
//			return false;
//		if (!getLocation().getCenter().equals(other.getLocation().getCenter()))
//			return false;
//		if (getLocation().getDiameter() != other.getLocation().getDiameter())
//			return false;
//		return true;
//	}
//	
//	/**
//	 * Careful: depends on mutable state of this object.
//	 * As a result, Balls must not be modified while they are used as key in a hash set or table. 
//	 * 
//	 * @inspects | this
//	 */
//	@Override
//	public int hashCode() {
//		return Objects.hash(location, velocity);
//	}	
}
