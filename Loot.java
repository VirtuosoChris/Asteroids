//Asteroid Scavenger
//Christopher Pugh


//this is the class for the floating dollar signs that targets in the game drop, and which give the player money when picked up

import java.awt.*;


public class Loot extends FlyingShit {
	
	
	protected double timeCreated; //the time the object was created
	protected Material material;  //what type of material is the loot made of?  Affects color, and value when picked up.
	
	public static final double LOOT_SPEED = .1; //the speed which loots move by default
	public static final int LOOT_SIZE = 20; //size of the bounding box for Loots
	
	public static final double DURATION  = 4500; //the lifetime of a Loot on the game board
	
	//the shape of the bounding box.  will be automatically translated in the FlyingShit "intersects" method
	public static final int BoundingBoxX[] = {-LOOT_SIZE,LOOT_SIZE, LOOT_SIZE, -LOOT_SIZE};
	public static final int BoundingBoxY[] = {0,0,2*LOOT_SIZE, 2*LOOT_SIZE};
	
	
	
	//constructor which takes a FlyingShit object and a material
	//the loot material is set to be equal to m, and 
	//the location and velocity is copied from the FlyingShit object
	public Loot(FlyingShit f, Material m){
		this.x = f.getX();
		this.y = f.getY();
		
		this.xVec = LOOT_SPEED*(f.getxVec()/f.getSpeed());
		this.yVec = LOOT_SPEED*(f.getyVec()/f.getSpeed());
		
		material = m;
		
		timeCreated = System.currentTimeMillis();
		
		this.shape = new Polygon(BoundingBoxX, BoundingBoxY, 4);
    }
	
	
	
	//constructor which leaves the velocity as 0
	public Loot(double X, double Y, Material m){
		this.x = X;
		this.y = Y;
		this.xVec = this.yVec = 0;
		material = m;
		
		timeCreated = System.currentTimeMillis();
		
		this.shape = null;
		
		this.shape = new Polygon(BoundingBoxX, BoundingBoxY, 4);
	}
	
	
	
	
	//how long has elapsed since the object was created?
	public double timeElapsed(){
		return System.currentTimeMillis() - timeCreated;
	}
	
	
	//what color is the loot?
	public Color getColor(){
	 return this.material.getColor();
	}
	
	
	//how much is the loot worth?
	public int getValue(){
	  return this.material.getValue();
	}	
	
	
	//given a Graphics object g, draws a little colored dollar sign in the appropriate location to 
	//represent the loot
	public void drawObject(Graphics g){
		g.setColor(this.getColor());
		g.drawString("$",(int)this.x, (int)this.y+LOOT_SIZE);	
	}
	
}
