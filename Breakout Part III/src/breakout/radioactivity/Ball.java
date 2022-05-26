package breakout.radioactivity;

import java.awt.Color;

import breakout.BreakoutState;
import breakout.utils.*;
import logicalcollections.LogicalSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.lang.Math;

/**
 * Represents the state of a ball in the breakout game.
 * 
 * @invar | getLocation() != null
 * @invar | getVelocity() != null
 * 
 * Invariants for echarge:
 * Betrekking tot teken
 * @invar | (getEcharge() < 0  && getLinkedAlphas().size() % 2 != 0)	||
 * 		  |	(getEcharge() > 0 && getLinkedAlphas().size() % 2 == 0)
 * Betrekking tot abs waarde
 * @invar | Math.abs(getEcharge()) == getLinkedAlphas().stream().mapToInt(a -> a.getLinkedBalls().size()).max().getAsInt() ||
 * 		  | 	(Math.abs(getEcharge()) == 1 && getLinkedAlphas().size() == 0)
 * 
 * 
 * Invariants to preserve consistency of bidirectional association;
 * the linked alphas are themeselves linked with this ball
 * @invar | getLinkedAlphas() != null
 * @invar | getLinkedAlphas().stream().allMatch(a -> a.getLinkedBalls().contains(this)
 * 		  | 	&& a != null)
 */
public abstract class Ball {
    //##
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
    public Set<Alpha> getLinkedAlphas() {
    	return Set.copyOf(linkedAlphas);}
    
    /**
     * @pre | alpha != null
     * @post | getLinkedAlphas().equals(LogicalSet.plus(old(getLinkedAlphas()), alpha))
     * @post | alpha.getLinkedBalls().equals(LogicalSet.plus(old(alpha.getLinkedBalls()), this))
     * @post | getLinkedAlphas().size() == old(getLinkedAlphas()).size() + 1 ||
     * 		 |		(getLinkedAlphas().contains(alpha) && (getLinkedAlphas().size() == old(getLinkedAlphas().size()))) 
     * 
     * @mutates_properties | getLinkedAlphas(), alpha.getLinkedBalls(), 
     * 					   |	(...alpha.getLinkedBalls()).getEcharge()
     */
    
    public void linkTo(Alpha alpha) {
    	linkedAlphas.add(alpha);
    	alpha.linkedBalls.add(this);
    	updateEcharge(); 
    	updateEcharge(alpha);
    }
    
    /**
     * @pre | alpha != null
     * @post | getLinkedAlphas().equals(LogicalSet.minus(old(getLinkedAlphas()), alpha))
     * @post | alpha.getLinkedBalls().equals(LogicalSet.minus(old(alpha.getLinkedBalls()), this))
     * @post | getLinkedAlphas().size() == old(getLinkedAlphas()).size() - 1 ||
     * 		 |		!(getLinkedAlphas().contains(alpha) && (getLinkedAlphas().size() == old(getLinkedAlphas().size()))) 
     * 
     * @mutates_properties | getLinkedAlphas(), alpha.getLinkedBalls(),
     * 					   |	(...alpha.getLinkedBalls()).getEcharge()
     */
    
    public void unLink(Alpha alpha) {
    	linkedAlphas.remove(alpha);
    	alpha.linkedBalls.remove(this);
    	updateEcharge();
    	updateEcharge(alpha); 
    }
   
    /**
     * Invariants for echarge:
     * Betrekking tot teken
     * @invar | (eCharge < 0  && getLinkedAlphas().size() % 2 != 0)	||
     * 		  |	(eCharge > 0 && getLinkedAlphas().size() % 2 == 0)
     * Betrekking tot abs waarde
     * @invar | Math.abs(eCharge) == getLinkedAlphas().stream().mapToInt(a -> a.getLinkedBalls().size()).max().getAsInt() ||
     * 		  | 		(Math.abs(eCharge) == 1 && getLinkedAlphas().size() == 0)
     */

    protected int eCharge = 1;
    /**
     * @invar | location != null
     * @invar | velocity != null
     */
	protected Circle location;
	protected Vector velocity;

	/**
	 * Construct a new ball at a given `location`, with a given `velocity`.
	 * 
	 * @pre | location != null
	 * @pre | velocity != null
	 * 
	 * @mutates this
	 * 
	 * @post | getLocation().equals(location)
	 * @post | getVelocity().equals(velocity)
	 * 
	 * @post | getLinkedAlphas().isEmpty()
	 * @post | getEcharge() == 1
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
	 * Return this ball's eCharge
	 */
	public int getEcharge() {
		return eCharge;
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
	 * 
	 */
	public abstract Ball cloneWithVelocity(Vector v);
	
	public Ball shallowClone() {
		return cloneWithVelocity(getVelocity());
	}
	
	
	/**
	 * Return a clone of this BallState.
	 * 
	 * @inspects this
	 * @creates result
//     * @post | result.getEcharge() == getEcharge()
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(getVelocity())
	 * 
	 * Is a deep clone
	 * @post result and this are linked to alphas with same velocity, location and echarge
	 * 		 | (getLinkedAlphas().size() == 0) || getLinkedAlphas().stream().allMatch(a1 -> result.getLinkedAlphas().stream().anyMatch(a2 -> (a2.getVelocity().equals(a1.getVelocity()))
	 * 		 |													&&  (a2.getEcharge() == a1.getEcharge()) && (a2.getLocation().equals(a1.getLocation()))))
	 * @post | getLinkedAlphas().size() == result.getLinkedAlphas().size()
	 */
	public Ball deepClone() {
		Ball res = shallowClone();
		for (Alpha alpha: getLinkedAlphas()) {
			res.linkTo(alpha.shallowClone());
		}
		return res;
	}
	
	
	/**
	 * 
	 * @mutates this
	 * @post abs value of eCharge changes 
	 * 		| ((Math.abs(getEcharge()) == 1) && (getLinkedAlphas().size() == 0)) ||
	 * 		| Math.abs(getEcharge()) == getLinkedAlphas().stream().mapToInt(a -> a.getLinkedBalls().size()).max().getAsInt()
	 * @post sign of eCharge changes
	 *		| (getEcharge() < 0  && getLinkedAlphas().size() % 2 != 0)	||
     * 		|	(getEcharge() > 0 && getLinkedAlphas().size() % 2 == 0)
     * 			
	 * 
	 */
	public void updateEcharge() {	//Wanneer? als linkTo() of unLink() wordt opgeroepen
		// Waarde update
		if (getLinkedAlphas().size() == 0) {eCharge = 1;}
		else {eCharge = getLinkedAlphas().stream().mapToInt(a -> a.getLinkedBalls().size()).max().getAsInt();}
		// Teken update
		if (getLinkedAlphas().size() % 2 == 0) {
			eCharge = Math.abs(eCharge);
		}
		else {eCharge = - Math.abs(eCharge);}
	}
	
	/**
	 * 
	 * @pre | alpha != null
	 * @mutates_properties | (...alpha.getLinkedBalls()).getEcharge()
	 * 
	 */
	public void updateEcharge(Alpha alpha) {	
		for (Ball ball: alpha.getLinkedBalls()) {
			ball.updateEcharge();
		}
			
	}
}
