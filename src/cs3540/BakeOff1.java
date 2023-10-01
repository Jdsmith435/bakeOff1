package cs3540;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.Frame;
import java.awt.Point;
import java.awt.MouseInfo;

import processing.awt.PSurfaceAWT;
import processing.core.PApplet;

public class BakeOff1 extends PApplet {
	// when in doubt, consult the Processsing reference:
	// https://processing.org/reference/
	// The argument passed to main must match the class name
	public static void main(String[] args) {
		// Tell processing what class we want to run.
		PApplet.main("cs3540.BakeOff1");
	}

	int margin = 200; // set the margin around the squares
	final int padding = 50; // padding between buttons and also their width/height
	final int buttonSize = 40; // padding between buttons and also their width/height
	ArrayList<Integer> trials = new ArrayList<Integer>(); // contains the order of buttons that activate in the test
	int trialNum = 0; // the current trial number (indexes into trials array above)
	int startTime = 0; // time starts when the first click is captured
	int finishTime = 0; // records the time of the final click
	int hits = 0; // number of successful clicks
	int misses = 0; // number of missed clicks
	Robot robot; // initialized in setup

	int numRepeats = 3; // sets the number of times each button repeats in the test	
	
	boolean targetHovered = false;

	/**
	 * https://processing.org/reference/settings_.html#:~:text=The%20settings()%20method%20runs,commands%20in%20the%20Processing%20API.
	 */
	public void settings() {
		size(700, 700);
	}

	/**
	 * // https://processing.org/reference/setup_.html
	 */
	public void setup() {
		// noCursor(); // hides the system cursor if you want
		noStroke(); // turn off all strokes, we're just using fills here (can change this if you
					// want)
		textFont(createFont("Arial", 16)); // sets the font to Arial size 16
		textAlign(CENTER);
		frameRate(60); // normally you can't go much higher than 60 FPS.
		ellipseMode(CENTER); // ellipses are drawn from the center (BUT RECTANGLES ARE NOT!)
		// rectMode(CENTER); //enabling will break the scaffold code, but you might find
		// it easier to work with centered rects

		try {
			robot = new Robot(); // create a "Java Robot" class that can move the system cursor
		} catch (AWTException e) {
			e.printStackTrace();
		}
		
		// ===DON'T MODIFY MY RANDOM ORDERING CODE==
		for (int i = 0; i < 16; i++) // generate list of targets and randomize the order
			// number of buttons in 4x4 grid
			for (int k = 0; k < numRepeats; k++)
				// number of times each button repeats
				trials.add(i);

		Collections.shuffle(trials); // randomize the order of the buttons
		System.out.println("trial order: " + trials); // print out order for reference

		surface.setLocation(0, 0);// put window in top left corner of screen (doesn't always work)
	}

	public void draw() {
		background(255); // set background white
		  
		if (trialNum >= trials.size()) // check to see if test is over
		{
			background(0);
			float timeTaken = (finishTime - startTime) / 1000f;
			float penalty = constrain(((95f - ((float) hits * 100f / (float) (hits + misses))) * .2f), 0, 100);
			fill(255); // set fill color to white
			// write to screen (not console)
			text("Finished!", width / 2, height / 2);
			text("Hits: " + hits, width / 2, height / 2 + 20);
			text("Misses: " + misses, width / 2, height / 2 + 40);
			text("Accuracy: " + (float) hits * 100f / (float) (hits + misses) + "%", width / 2, height / 2 + 60);
			text("Total time taken: " + timeTaken + " sec", width / 2, height / 2 + 80);
			text("Average time for each button: " + nf((timeTaken) / (float) (hits + misses), 0, 3) + " sec", width / 2,
					height / 2 + 100);
			text("Average time for each button + penalty: "
					+ nf(((timeTaken) / (float) (hits + misses) + penalty), 0, 3) + " sec", width / 2,
					height / 2 + 140);
			return; // return, nothing else to do now test is over
		}
		
		// Change bg color to red if user's mouse deviates outside the button
		Rectangle bounds = getButtonLocation(trials.get(trialNum));
		if (trialNum > 0 &&
				!((mouseX > bounds.x && mouseX < bounds.x + bounds.width) 
				&& (mouseY > bounds.y && mouseY < bounds.y + bounds.height))) {
			
			if (targetHovered) background(255, 200, 200);
		}

		// Draw an arrow from curr box to next box
		if(trialNum < trials.size() - 1){
			Rectangle bound = getButtonLocation(trials.get(trialNum));
			Rectangle bound2 = getButtonLocation(trials.get(trialNum+1));
	
			float x1 = bound.x+20;
			float y1 = bound.y+20;
			float x2 = bound2.x;
			float y2 = bound2.y; 
	
			stroke(0, 0, 255);
			strokeWeight(3); 
			drawArrow(x1, y1, x2, y2);
			
		}
		
		// Draw an arrow from mouse to current target
		if (trialNum > 0) {
			Rectangle currBoxLoc = getButtonLocation(trials.get(trialNum));
			stroke(200);
			strokeWeight(3);
			drawArrow(mouseX, mouseY, currBoxLoc.x, currBoxLoc.y);
		}
		
		for (int i = 0; i < 16; i++)// for all button
			drawButton(i); // draw button
		
		fill(0); // set fill color to black
		text((trialNum + 1) + " of " + trials.size(), 40, 20); // display what trial the user is on
		
		// Draw column numbers
		for (int i = 0; i < 4; i++) {
			Rectangle buttonLoc = getButtonLocation(i);
			text(i + 1, (float) buttonLoc.getCenterX(), buttonLoc.y - 40);
		}		
	}

	public void mousePressed() // test to see if hit was in target!
	{
		if (trialNum >= trials.size()) // check if task is done
			return;

		if (trialNum == 0) // check if first click, if so, record start time
			startTime = millis();

		if (trialNum == trials.size() - 1) // check if final click
		{
			finishTime = millis();
			// write to terminal some output:
			System.out.println("we're all done!");
		}

		Rectangle bounds = getButtonLocation(trials.get(trialNum));

		// check to see if cursor was inside button
		if ((mouseX > bounds.x && mouseX < bounds.x + bounds.width)
				&& (mouseY > bounds.y && mouseY < bounds.y + bounds.height)) // test to see if hit was within bounds
		{
			System.out.println("HIT! " + trialNum + " " + (millis() - startTime)); // success
			hits++;
			targetHovered = false;
		} else {
			System.out.println("MISSED! " + trialNum + " " + (millis() - startTime)); // fail
			misses++;
		}

		trialNum++; // Increment trial number
	}

	// probably shouldn't have to edit this method
	public Rectangle getButtonLocation(int i) // for a given button ID, what is its location and size
	{
		int x = (i % 4) * (padding + buttonSize) + margin;
		int y = (i / 4) * (padding + buttonSize) + margin;

		return new Rectangle(x, y, buttonSize, buttonSize);
	}

	// you can edit this method to change how buttons appear
	public void drawButton(int i) {
		Rectangle bounds = getButtonLocation(i);

		if (trials.get(trialNum) == i) // see if current button is the target
			if ((mouseX > bounds.x && mouseX < bounds.x + bounds.width)
					&& (mouseY > bounds.y && mouseY < bounds.y + bounds.height)) {
				fill(0, 255, 0); // if so, fill cyan
				targetHovered = true;
			} else {
				fill(0, 100, 0); // if so, fill cyan
				
			}
		else if (trialNum < trials.size() - 1 && trials.get(trialNum+1) == i)
			fill(100, 100, 100); // dark grey for next up target
		else
			fill(200); // if not, fill gray

		rect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	public void mouseMoved() {
		// can do stuff everytime the mouse is moved (i.e., not clicked)
		// https://processing.org/reference/mouseMoved_.html
	}

	public void mouseDragged() {
		// can do stuff everytime the mouse is dragged
		// https://processing.org/reference/mouseDragged_.html
	}

	public void keyPressed() {
		// Fancy way to get the window frame info from the PApplet
		Frame windowFrame = ((PSurfaceAWT.SmoothCanvas)((PSurfaceAWT)surface).getNative()).getFrame();
		Point windowLocation = windowFrame.getLocation();
		int nativeMouseY = MouseInfo.getPointerInfo().getLocation().y; // Just preserve the user's current y location
		
		switch (keyCode) {
		case '1':
		case '2':
		case '3':
		case '4':
			// Position the user's mouse in the selected column
			int colX = (int)getButtonLocation((keyCode - 49)).getCenterX();
			int nativeColX = windowLocation.x + colX;// Get columns's native y location on user screen
			robot.mouseMove(nativeColX, nativeMouseY);
			break;
		}
	}
	
	private void drawArrow(float x1, float y1, float x2, float y2) {
		line(x1, y1, x2, y2);
	
		// Calculate the angle of the line
		float angle = atan2(y2 - y1, x2 - x1);
	  
		// Calculate the position of the arrowhead
		float arrowSize = 10;
		float arrowX = x2 - cos(angle - PI/6) * arrowSize;
		float arrowY = y2 - sin(angle - PI/6) * arrowSize;
		
		// Draw the arrowhead
		triangle(x2, y2, arrowX, arrowY, x2 - cos(angle + PI/6) * arrowSize, y2 - sin(angle + PI/6) * arrowSize);
	
		line(x1, y1, x2, y2);
		
		stroke(0, 0, 0);
		strokeWeight(0); 
	}
}
