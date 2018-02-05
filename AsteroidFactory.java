import java.awt.*;
import java.awt.Color;
import java.awt.geom.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.*;

/**
	Produces Asteroids
	Solves CPSC 1181 homework assignment #6
	@author Jeremy Hilliker
	@author Rosalind Ng
	@version 10-28-2017 
*/
public class AsteroidFactory {

	private final static int ASTEROID_SIZE_MIN = 15;
	private final static int ASTEROID_SIZE_MAX = 50;
	private final static float ASTEROID_VEL_MIN = 1;
	private final static float ASTEROID_VEL_MAX = 4;
	private final static int NPOS = 9;
	
	private final static AsteroidFactory instance = new AsteroidFactory();

	private static Rectangle startBounds;
	private static Rectangle moveBounds;

	private AsteroidFactory() {}

	public static AsteroidFactory getInstance() {
		return instance;
	}

	public void setStartBounds(Rectangle r) {
		startBounds = r;
	}
	/** Set the mbound of the asteroid by taking the union of the provided
	* bounds and the start bounds, and making it 1 pixel wider
	* @param r the provided bound that's to be added to the startbound
	*/
	public void setMoveBounds(Rectangle r) {
		moveBounds = r.union(startBounds);
		moveBounds.grow(1, 1);
	}
	
	/**create new Asteroids that has a random height and width*/
	public Asteroid makeAsteroid() {
		int size = random(ASTEROID_SIZE_MIN, ASTEROID_SIZE_MAX);
		int xPos = random(startBounds.x, startBounds.x+startBounds.width);
		int yPos = random(startBounds.y, startBounds.y+startBounds.height);
		float xVel = random(ASTEROID_VEL_MIN, ASTEROID_VEL_MAX);
		float yVel = random(-ASTEROID_VEL_MIN, ASTEROID_VEL_MIN);
		float rot = (float) random(-Math.PI*2/60, Math.PI*2/60);
		final int NPOS = 9;
		//randomly place the asteroid in the startBounds
		int[] xPoints = {
			xPos-size/5, xPos, xPos+size/2, 
			xPos + size, xPos + size, xPos + size, 
			xPos + size/2, xPos, xPos-size/5
			};
		int[] yPoints = {
			yPos + size/5, yPos, yPos,
			yPos + size/5, yPos + 3*size/5, yPos + 2*size/3,
			yPos + size, yPos + size*4/5, yPos + size/5
			};
		
		return new AsteroidImpl(new Polygon(xPoints, yPoints, NPOS), xVel, yVel, rot); 
	}
	
	private static int random(int min, int max) {
		if(max-min == 0) { return min; }
		ThreadLocalRandom rand = ThreadLocalRandom.current();
		return min + rand.nextInt(max + 1);
	}

	private static float random(double min, double max) {
		ThreadLocalRandom rand = ThreadLocalRandom.current();
		return (float) rand.nextDouble(min, max);
	}

	private static class AsteroidImpl extends SpriteImpl implements Asteroid {
		private final static Color COLOR = Color.DARK_GRAY;
		
		public AsteroidImpl(Shape s, float xVel, float yVel, float r) {
			//call the supper constructor by passing in the shape and the bound
			//Ellipse2D.Float roid = new Ellipse2D.Float(x, y, w, h);
			super(s, moveBounds, false, new Color(128,247,59), Color.BLACK);
			//make the temp variables for the velocity of the asteroids
			//set the velocity of the Asteroids
			setVelocity(-xVel, yVel);
			setRotation(r);
		}
	}
}
