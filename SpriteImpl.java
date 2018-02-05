import java.awt.*;
import java.awt.geom.*;

/**
	sprites for the game as well as checking game status
	Solves CPSC 1181 homework assignment #6
	@author Jeremy Hilliker
	@author Rosalind Ng
	@version 11-05-2017 
*/

public abstract class SpriteImpl implements Sprite {

	// drawing
	private Shape shape;
	private final Color border;
	private final Color fill;

	// movement
	private float dx, dy;
	private float rot;
	private final Rectangle2D bounds;
	private final boolean isBoundsEnforced;

	protected SpriteImpl(Shape shape, Rectangle2D bounds, boolean boundsEnforced, Color border, Color fill) {
		this.shape = shape;
		this.bounds = bounds;
		this.isBoundsEnforced = boundsEnforced;
		this.border = border;
		this.fill = fill;
	}
	protected SpriteImpl(Shape shape, Rectangle2D bounds, boolean boundsEnforced, Color fill) {
		this(shape, bounds, boundsEnforced, null, fill);
	}

	public Shape getShape() {
		return shape;
	}

	public void setVelocity(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public void setRotation(float rot) {
		this.rot = rot;
	}
	
	/** moves the shape in the frame if the movement of the shape does not
	* cause the shape to be out of bound
	* A method taken from the BoxGameComp class made in the CPSC 1181
	* lecture written by Jeremy Hiliker
	*/
	public void move() {		
		Shape newPos = shape;

		// rotation
		if(rot != 0) {
			int shapeCenterX = shape.getBounds().x+shape.getBounds().width/2;
			int shapeCenterY = shape.getBounds().y+shape.getBounds().height/2;
			Shape rotPos = 
				AffineTransform.getRotateInstance(rot,shapeCenterX,shapeCenterY).createTransformedShape(newPos);
			if(!isBoundsEnforced || isInBounds(bounds, rotPos)) {
				newPos = rotPos;
			}
		}

		Shape afterMove = AffineTransform.getTranslateInstance(dx, dy).createTransformedShape(newPos);
		// stay in bounds if it matters
		if(!isBoundsEnforced || isInBounds(bounds, afterMove)) {
			newPos = afterMove;
		}
		shape = newPos;
	}
	
	/** Checks whether t he shape is out of bound*/
	public boolean isOutOfBounds() {
		// use the intersect method to figure out if the shape intersects
		//the component
		return !shape.intersects(bounds);
	}
	public boolean isInBounds() {
		return isInBounds(bounds, shape);
	}
	/** Return true if the ship is contained within the sprite's 
	* movement bount after the sepcified movement of dx and dy
	* @param bounds the the movement bound
	* @param s the ship whose movement should be restricted to be within the movement bound
	* @param dx the supposed movement in the x direction
	* @param dy the supposed movement in the y direction
	*/
	private static boolean isInBounds(Rectangle2D bounds, Shape s) {
		//return trus if the shape will still be in bound after the supposed movements, or else return false
		return bounds.contains(s.getBounds());
	}
	

	public void draw(Graphics2D g2) {
		// TODO
		//fill the shape first, set color to the fill
		g2.setColor(fill);
		g2.fill(shape);
		//draw the border of the shape, set the color of the border
		g2.setColor(border);
		//change the border, make it a little thicker
		g2.setStroke(new BasicStroke(1.5f));
		g2.draw(shape);
	}

	public boolean intersects(Sprite other) {
		return intersects(other.getShape());
	}
	
	/**Checks to see if the bounding box of the shapes intersects each other
	* @param other the shape to check if it intersects with this one
	*/
	private boolean intersects(Shape other) {
		//if the bounding shape intersect, call the other intersects
		//methods with the area
		if(shape.getBounds().intersects(other.getBounds())) {
			
			//want to check the bounds of their area, because you know it's going to be a rectangle
			return intersects(new Area(shape), new Area(other));
		}
		//if the bounding box of the shape doesn't even intersect,
		//it will be false
		return false;
	}
	/** Checks to see if the area of two shapes intersects each other
	* @param a the area of the first shape to test
	* @param b the area of the second shape to test
	*/
	private static boolean intersects(Area a, Area b) {
		//call the intersect methods, the intersect method of Area,
		//takes a rectangle, so want to convert the ship into a rectangle
		a.intersect(b);
		return !a.isEmpty();
	}
}
