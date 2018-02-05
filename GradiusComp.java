import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.stream.Stream;
import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.geom.Rectangle2D;
import java.awt.geom.*;
import java.util.stream.Collectors;

/**
	Component for the game 
	Solves CPSC 1181 homework assignment #6
	@author Jeremy Hilliker
	@author Rosalind Ng
	@version 10-28-2017 
*/

@SuppressWarnings("serial")
public class GradiusComp extends JComponent {

	private final static int GAME_TICK = 1000 / 60;
	private final static int ASTEROID_MAKE_TICK = 1000/5;

	private final static int SHIP_INIT_X = 10;
	private final static int SHIP_INIT_Y = Gradius.HEIGHT/3;
	private final static int SHIP_VEL_BASE = 2;
	private final static int SHIP_VEL_FAST = 4;
	
	private Rectangle asteroidsStartBound;
	private Ship ship;
	private Timer[] gameTick = new Timer[2];
	private Collection<Asteroid> roids;
	private Collection<Sprite> lasers;
	private boolean gameOver = false;
	/** Constructor of the class: makes a timer, adds the key listener 
	* to the component, makes a HashSet*/
	public GradiusComp() {
		// the game clock
		gameTick[0] = new Timer(GAME_TICK, ae -> update());
		// making asteroids
		gameTick[1] = new Timer(ASTEROID_MAKE_TICK, ae -> makeAsteroid());
		
		ShipKeyListener skl = new ShipKeyListener();
		addKeyListener(skl);
		
		roids = new HashSet<Asteroid>();
		lasers = new HashSet<Sprite>();
	}
	
	public void update() {
		//add the requestFocusInWindow()  to ensure that the listener
		//always receives key events when window is active
		requestFocusInWindow();

		// move the things
		ship.move();
		roids.parallelStream()
			.forEach(Asteroid::move);
		lasers.parallelStream().forEach(Sprite::move);

		// remove the ones off screen
		roids.removeIf(r -> r.isOutOfBounds());
		lasers.removeIf(l -> l.isOutOfBounds());
		
		// find lasers and asteroids that hit each other
		Collection<Sprite> matchLasers = lasers.parallelStream().filter(l -> roids.parallelStream()
			.anyMatch(r -> r.intersects(l))).collect(Collectors.toSet());
		Collection<Asteroid> matchAsteroids = roids.parallelStream().filter(r -> lasers.parallelStream()
			.anyMatch(l -> l.intersects(r))).collect(Collectors.toSet());
		// remove them
		lasers.removeAll(matchLasers);
		roids.removeAll(matchAsteroids);

		//check to see if the asteroids intersect with the ship
		if(roids.parallelStream().anyMatch(r -> ship.intersects(r))) {
			gameOver = true;
			//stop the timers
			stop();
		};
		
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintComponent(g2);
	}
	
	private void paintComponent(Graphics2D g2) {

		ship.draw(g2);
		// lasers
		lasers.stream()
			.forEach(l -> l.draw(g2));
		// asteroids
		roids.stream()
			.forEach(r -> r.draw(g2));
		
		if(gameOver) {
			g2.setColor(Color.RED);
			g2.setFont(new Font(null, 1, 100));
			g2.drawString("Game Over", Gradius.HEIGHT/4, Gradius.HEIGHT/2);
		}
	}
	
	/** The function that is called at the start of the game, initializes the  ship,
	* starts the timer, sets the startBounds for new Asteroids */
	public void start() {
		Rectangle compBounds = getBounds();
		
		ship = new ShipImpl(SHIP_INIT_X, SHIP_INIT_Y, compBounds);
		
		// start bound of newly constructed Asteroid
		AsteroidFactory.getInstance().setStartBounds(
			new Rectangle(compBounds.width, compBounds.y, 0, compBounds.height));
		// set the moveBounds of the newly constructed Asteroid
		AsteroidFactory.getInstance().setMoveBounds(compBounds);
		
		//start the timers
		Stream.of(gameTick).forEach(Timer::start);
	}	
	
	/** Stops the game and print out a message, gameOver */
	public void stop() {
		// stop timers
		Stream.of(gameTick).forEach(Timer::stop);
		
	}
	
	/** makes an asteroid, add it to the collection */
	private void makeAsteroid() {
		roids.add(AsteroidFactory.getInstance()
				.makeAsteroid());
	}
	
	/** makes a laser, add it to the collection */
	private void makeLaser() {
		lasers.add(ship.shoot( getBounds()));
	}
	
	/** The key listener of the game*/
	private class ShipKeyListener extends KeyAdapter {

		private boolean up;
		private boolean down;
		private boolean left;
		private boolean right;
				
		@Override
		public void keyPressed(KeyEvent e) {
			//if the spacebar is pressed, want to shoot the cannon
			
	 		switch(e.getKeyCode()) {
				case KeyEvent.VK_SPACE:
				case KeyEvent.VK_ENTER:
					makeLaser();
					break;
				default: 
					//call the function setVelocity
					setVelocity(e);
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			// call function setVelocity
			setVelocity(e);
		}
		
		/** gets sets the velocity of the shape's movements
		* A method taken from the BoxGameComp class made in the CPSC 1181
		* lecture written by Jeremy Hiliker
		* @param e the event whih indicates a key has been pressed 
		* 	on the keyboard
		*/
		public void setVelocity(KeyEvent e) {
			//make the directions variables
			int dy = 0;
			int dx = 0;
			int velocity;
			//get the direction using a helper method
			getDirection(e);
			
			//determine if shift is pressed, if it is then the velocity will be more
			velocity = e.isShiftDown() ? SHIP_VEL_FAST  : SHIP_VEL_BASE;
			
			//now set the dy and dx
			if(up && !down) {
				dy = -velocity;
			} else if(down && !up) {
				dy = velocity;
			}
			if(left && !right) {
				dx = -velocity;
			} else if(right && !left) {
				dx = velocity;
			}
			//once the direction velocity has been set,
			//invoke the method to make the ship move in the SpriteImpl class
			ship.setVelocity(dx,dy);
		}
		/** determines the direction the shape should move based on the keystroke
		* A method taken from the BoxGameComp class made in the CPSC 1181
		* lecture written by Jeremy Hiliker
		* @param e the event which indicates a key has been pressed 
		* 	on the keyboard
		*/
		private void getDirection(KeyEvent e) {
			//make a boolean to know if the key is pressed 
			final boolean state;
			//or not using a switch case
			switch(e.getID()) {
				//if it is pressed then set the state to true
				case KeyEvent.KEY_PRESSED:
					state = true;
					break;
				//if it's released then change the state to false
				case KeyEvent.KEY_RELEASED:
					state = false;
					break;
				//need to give default or else won't compile
				default: return;
			}
			
			//get the direction of the keys
			switch(e.getKeyCode()) {
				//if a, left key or left key on num keypad is pressed
				case KeyEvent.VK_A:
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_KP_LEFT:
					left = state;
					break;
				//do same with right
				case KeyEvent.VK_D:
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_KP_RIGHT:
					right = state;
					break;
				//do same with up
				case KeyEvent.VK_W:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_KP_UP:
					up = state;
					break;
				//do same with down
				case KeyEvent.VK_S:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_KP_DOWN:
					down = state;
			}
		}
	};
}
