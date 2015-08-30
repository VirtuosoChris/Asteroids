//Asteroid Scavenger
//By Chris Pugh


import java.awt.*;

class Ship extends FlyingShit implements ArmedShip{

//constants
public static double FIRE_DELAY = 250; //default fire delay for a triangle ship
public static final int RADAR_DISTANCE = 250; //distance at which the radar upgrade begins to display the asteroid speeds

protected static final int shipRadius = 5; //measurement used to generate the ship polygon

//default accel and decel variables
private static double N_DECEL = .9;
private static double N_ACCEL = .05;

//upgraded accel and decel variables
private static double U_DECEL = .7;
private static double U_ACCEL = .1;


//fields
private double angle = 0; //in radians
private int score = 0; //in dollars
private double lastFired = 0; //time the ship last fired, used to calculate delay before next shot
private double maxSpeed = 10.0; //ship's top speed

public boolean alive = true; //kinda self explanatory?  seperate from flyingshit .dead field because of tacked on design. 
//sorry

public MapStats mapstats;

//possession flags for purchasable upgrades
protected final boolean upgrades[] = {false, false,false,false,false,false,false,false,false};

public static final int UPGRADECOST[] = {250,1000,500,10000,10000,25000,15000,5000,500};  //prices for purchasable upgrades

public static final int LIFECOST = 1000; //the cost for repairs to the player's ship

//lookup values for each of the purchasable ship upgrades
public static final int WARP = 0;
public static final int SHIELD = 1;
public static final int SUPERCHARGE = 2;
public static final int SCANNER = 3;
public static final int RADAR = 4;
public static final int EFFICIENCY = 5;
public static final int ACCEL = 6;
public static final int TRACTOR = 7;
public static final int ARMOR = 8;

public static final int NUM_UPGRADES = 9; //how many upgrades ARE there?

public static final int SHIELD_RATE =40; //how fast the shield is used up, in percent per second

public double shield = 50;  //shield energy in % left

public boolean shielded = false; // does the ship have a shield around it? (is the user holding the S key)


//ship constructor, sets the player ship at sx, sy, initializes the angle of rotation (from the nose at PI/2) to zero, 
//creates the shape polygon, and sets the starting velocity to zero.
public Ship(int sx, int sy){

  super(sx, sy);
  
  mapstats = new MapStats(this);
 
  this.angle = 0;
			
  this.shape = makeSprite(0.0);
  
  this.score = 1000;//just enough for an extra life
		
  this.alive = true; //just 'cause I'm generous
}//end constructor



//returns a bullet fired from the ship's nose, in the current direction the ship is facing, with Bulletspeed velociry in that 
//direction combined with the velocity of the ship
public Bullet fire(){

  double a = -Bullet.bulletSpeed*Math.cos((this.getAngle() + Math.PI/2));;
  double b = -Bullet.bulletSpeed*Math.sin((this.getAngle() + Math.PI/2));

  double ztmp = Ship.FIRE_DELAY;
  if(this.hasUpgrade(Ship.EFFICIENCY)){
  	ztmp/=2;
  }
	    
  if(System.currentTimeMillis()-this.timeLastFired() >= ztmp){

    this.newFireTime();
	  	
    return new Bullet(this.noseX(), this.noseY(),this.getxVec()+a , this.getyVec()+b);
	  	  
  }
  
  return null;

}


//what we do when the ship dies--flag as dead, stop its movement
public void die(){

	this.xVec = 0;
	this.yVec = 0;
	alive = false;
}


//self explanatory
public void revive(){
	alive = true;
}


//this one too
public boolean isAlive(){
	return alive;
}


//does this ship object have the upgrade with lookup value x?
public boolean hasUpgrade(int x){
	
	if(x<0||x>=NUM_UPGRADES)return false;
	return upgrades[x];
	
}


//gives the ship object the upgrade with lookup value x
public void giveUpgrade(int x){
	upgrades[x]=true;
}

//gives the ship object the upgrade with lookup value x
public void removeUpgrade(int x){
	upgrades[x]=false;
}



//functions to return coordinates of the nose of the ship
public int noseX(){
 return this.shape.xpoints[0]+(int)this.x;
}

public int noseY(){
 return this.shape.ypoints[0]+(int)this.y;
}



//returns a version of the ship sprite rotated "rad" radians
public static Polygon makeSprite(double radians){

 //the coordinates for the ship sprite, unrotated. used for getting the radius for rotation and as a reference
 final int xcoords[] = {0,shipRadius, -shipRadius};
 final int ycoords[] = {-shipRadius*2,shipRadius, shipRadius};

	
 int rotX[] = new int[3];
 int rotY[] = new int[3];
 
 double radius = 0;
 double theta = 0;
 
 radius = Math.sqrt((xcoords[0]*xcoords[0]) + (ycoords[0]*ycoords[0]));
 	
 rotX[0] = (int)(radius*Math.cos(radians+3*Math.PI/2));
 rotY[0] = (int)(radius*Math.sin(radians+3*Math.PI/2));
 	
 radius = Math.sqrt((xcoords[1]*xcoords[1]) + (ycoords[1]*ycoords[1]));
 	
 rotX[1] = (int)(radius*Math.cos(radians+Math.PI/4));
 rotY[1] = (int)(radius*Math.sin(radians+Math.PI/4));
 	
 radius = Math.sqrt((xcoords[2]*xcoords[2]) + (ycoords[2]*ycoords[2]));
 	
 rotX[2] = (int)(radius*Math.cos(radians+3*Math.PI/4));
 rotY[2] = (int)(radius*Math.sin(radians+3*Math.PI/4));
 	
 return new Polygon(rotX,rotY, rotX.length);

}


//does the player have at least "dollars" points?
public boolean hasMoney(int dollars){
	return this.score >= dollars;
}




//adds p to the total number of points
public void addPoints(int p){
	this.score+=p;
}


//get the time the last shot was fired by this ship
public double timeLastFired(){
	return lastFired;
}


//sets the last fired time to the system's current time
public void newFireTime(){
	lastFired = System.currentTimeMillis();
}



//returns the number of points associated with this ship
public int getPoints(){
	return this.score;
}



//rotates the ship by a.
public void rotate(double a){
	this.angle += a;
}



//reduces the current speed by the ship's deceleration rate
public void decelerate(){
	
	double decel = Ship.N_DECEL;
	if(hasUpgrade(Ship.ACCEL)){
	decel = Ship.U_DECEL;
	}
	
	this.xVec*=decel;
	this.yVec*=decel;
}



//returns the angle of rotation from pi/2 (nose pointing up) in radians
public double getAngle(){
return this.angle;}



//accelerates the ship based on the variable accel and the angle the ship's nose is facing
public void accelerate(){
	
	double accel = Ship.N_ACCEL;
	  if(hasUpgrade(Ship.ACCEL)){
	  accel = Ship.U_ACCEL;
	}
	
	this.xVec-=accel*Math.cos(this.angle+(Math.PI/2));
	this.yVec-=accel*Math.sin(this.angle+(Math.PI/2));
	
	double a = this.getSpeed();
	
	if(a > maxSpeed){
		this.xVec/=a;
		this.yVec/=a;
		this.xVec*=maxSpeed;
		this.yVec*=maxSpeed;
	}
}



//set's the ship object's position to <px,py>
public void setPosition(double px, double py){
	this.x = px;
	this.y = py;
}



//draws the rotated and translated ship in blue
public void drawObject(Graphics g){
  	
  	g.setColor(Color.GREEN);
  	
  	int  SHIELDWIDTH = 10;
  	
  	if(this.shielded)
  	g.drawOval((int)this.x-SHIELDWIDTH, (int)this.y-SHIELDWIDTH, SHIELDWIDTH*2, SHIELDWIDTH*2);
  	
  	
  	g.setColor(Color.BLUE);
  	
  	this.shape = makeSprite(this.angle);
  	
  	super.drawObject(g);
  	
 }//end method


}//end class
