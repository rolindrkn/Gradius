import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;

/**
	Makes a shape 
	Solves CPSC 1181 homework assignment #6
	@author Jeremy Hilliker
	@author Rosalind Ng
	@version 11-05-2017 
*/

public class ShipImpl  extends SpriteImpl implements Ship {

	private final static Color FILL = Color.GREEN;
	private final static Color BORDER = Color.BLACK;

	private final static int HEIGHT = 20;
	private final static int WIDTH = HEIGHT;
	private final static int LASER_LENGTH = 15;
	private final static int LASER_HEIGHT = 1;
	private final static int 	LASER_VELOCITY = 5;
	
	private static int lives;
	public ShipImpl(int x, int y, Rectangle2D moveBounds) {
		super((new Polygon(
			new int[] { x, x+WIDTH, x},
			new int[] { y, y+HEIGHT/2, y+HEIGHT},
			3)), moveBounds , true, BORDER, FILL);	
			
		lives = 3;
	}
	
	 /** makes a laser and shoots*/
	public Sprite shoot(Rectangle2D bounds) {
		Rectangle shipBounds = getShape().getBounds();
		int shipCenterX = shipBounds.x + shipBounds.width/2;
		int shipCenterY = shipBounds.y + shipBounds.height/2;
		Color colour = Color.RED;
		Rectangle2D laserShape = new Rectangle(shipCenterX, shipCenterY, LASER_LENGTH, LASER_HEIGHT);
		Sprite laser = new SpriteImpl(laserShape, bounds, false, colour) {};
		laser.setVelocity(LASER_VELOCITY, 0);
		return laser;
	}
	
}
