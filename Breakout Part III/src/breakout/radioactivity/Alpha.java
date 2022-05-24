package breakout.radioactivity;

import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

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
    	//System.out.print(linkedBalls);
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
	 * @param location the location to set
	 */
	public void setLocation(Circle location) {
		this.location = location;
	}

	/**
	 * @param velocity the velocity to set
	 */
	public void setVelocity(Vector velocity) {
		this.velocity = velocity;
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
	 * 
	 * @param rect
	 */
	public void hitWall(Rect rect) {
		velocity = bounceOn(rect);
		for(Ball ball:this.getLinkedBalls()) {
			Vector nspeed = Vector.magnetSpeed(this.getCenter(), ball.getCenter(), this.eCharge, ball.getVelocity());
			ball.setVelocity(nspeed);
		}
	}
	
	/**
	 * 
	 * @param rect
	 * @return
	 */
	public Vector bounceOn(Rect rect) {
		Vector coldir = rect.collideWith(location);
		if (coldir != null && velocity.product(coldir) > 0) {
			return velocity.mirrorOver(coldir);
		}
		return null;
	}
	
	/**
	 * 
	 * @param v
	 * @param elapsedTime
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
	 * Return a clone of this BallState.
	 * 
	 * @inspects this
	 * @creates result
	 * @post | result.getLocation().equals(getLocation())
	 * @post | result.getVelocity().equals(getVelocity())
	 */
	public Alpha clone() {
		return cloneWithVelocity(getVelocity());
	}
	
	

    
    
}
	


