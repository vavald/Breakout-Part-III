package breakout.radioactivity;

import java.util.HashSet;
import java.util.Set;

import breakout.utils.Circle;
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
	private final int echarge = 1;
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
    public Set<Ball> getLinkedBalls() { return Set.copyOf(linkedBalls); }
    
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
		return echarge;
	}
	

    
    
}
	


