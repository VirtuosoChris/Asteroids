//Asteroid Scavenger
//By Chris Pugh

import java.util.*;
import java.awt.*;

public class UFO extends FlyingShit implements ArmedShip{
	
	public static final int WIDTH = 40;//width of the bounding box
	public static final int HEIGHT = 20; //height of the bounding box
	public static  double SPEED = .8; //speed that a UFO moves
	
	public static final double FIRE_DELAY = 1250; //fires once every 1.5 seconds
	
	protected double lastFired = 0; //the time that the ship last fired
	
	//the list of all bullets fired by UFOs, shared by all UFO objects
	public static final ArrayList<Bullet> UfoBullets = new ArrayList<Bullet>();
	
	//the shape of the UFO bounding box
	public static final int BoundingBoxX[] = {0,WIDTH, WIDTH,0};
	public static final int BoundingBoxY[] = {0,0,HEIGHT, HEIGHT};
	
	
	//same as flyingshit constructor, but assigns a bounding shape to the ufo
	public UFO(int x, int y){
		super(x,y);
		lastFired = System.currentTimeMillis();  //set to current time to give a delay between map start and when they fire
    	this.shape = new Polygon(BoundingBoxX, BoundingBoxY, 4);
	}
		
		
	//get the time the last shot was fired by this ship
	public double timeLastFired(){
		return lastFired;
	}

	
	//sets the last fired time to the current 
	public void newFireTime(){
    	lastFired = System.currentTimeMillis();
	} 





	//returns a new bullet, with position initialized to the UFO's current location
	//zero velocity, however
	public Bullet fire(){
		return new Bullet((int)this.x, (int)this.y,0 ,0);
	}
	
	
	//given a list of FlyingShit, returns the closest
	public FlyingShit closest(ArrayList<FlyingShit> targets){
		
		
		FlyingShit closest = null;
		double dist = 12800;
		double getd = 0;
		
		for(FlyingShit f : targets){
			if((getd = this.getDistance(f)) <= dist){
				closest = f;
				dist = getd;	
			}	
		}
		
		return closest;
		
	}
	
	
	
	//updates the UFO
	//given a list of objects it wants to move towards, it selects the closest and sets the vector in that direction
	//given the list of objects it wants to shoot it shoots the closest, assuming the appropriate amount of time has passed
	public void update(ArrayList<FlyingShit> moveTargets, ArrayList<FlyingShit> shootTargets){
		
		FlyingShit closest;
		    double dist;
		
		
		if(!moveTargets.isEmpty()){
			
		    //closest target to move towards
		    closest = this.closest(moveTargets);
		    dist = this.getDistance(closest);
		
	     	//move in the direction of the closest loot or ship
	       this.setVelocity(SPEED*((closest.getX() - this.getX()) / dist),SPEED*((closest.getY()-this.getY()) / dist));
		}
		
		
		if(!shootTargets.isEmpty()){
		
		//get the closest target to shoot at
		closest = this.closest(shootTargets);
		dist = this.getDistance(closest);
		
		if(System.currentTimeMillis() - lastFired >= FIRE_DELAY){
			
			lastFired = System.currentTimeMillis();
			
			
			Bullet b = this.fire();
			double tempx = Bullet.bulletSpeed*(closest.getX() - this.x)/dist;
			double tempy = Bullet.bulletSpeed*(closest.getY() - this.y)/dist;
		
			tempx +=this.xVec;
			tempy+=this.yVec;
		
			b.setVelocity(tempx,tempy);
		
			UfoBullets.add(b);
		}
		}
		
	}
	
	
	
	//draws the UFO object, with x,y being the upper left hand corner.  This was done for simplicity's sake, and is not 100% accurate
	//to the convention of the other objects, with x,y being the center of the other objects
	public void drawObject(Graphics g){
		
		
		g.setColor(Color.GREEN);
		g.drawOval((int)this.x,(int)this.y,WIDTH,HEIGHT);
		g.drawOval((int)this.x + WIDTH/4,(int)this.y,WIDTH/2,HEIGHT/2);
		
		int dot = 4;
		
		g.setColor(Color.BLUE);
		
		g.drawOval((int)this.x + 5,(int)this.y+10,dot,dot);
		g.drawOval((int)this.x + 18,(int)this.y+15,dot,dot);
		g.drawOval((int)this.x + 30,(int)this.y+10,dot,dot);
		
		
	}

	
	
}
