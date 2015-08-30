//Asteroid Scavenger
//Chris Pugh

//The class for the little floating rocks the game is named after.

import java.awt.*;
import java.util.*;

class Asteroid extends FlyingShit{
  
  
  //class constants, the properties of all asteroids
  public static final int ASTEROID_VERTICES = 12;
  public static final int ASTEROID_MAXRADIUS = 25;
  public static final int ASTEROID_MINRADIUS = 8;
  public static final double ASTEROID_MAXSPEED = 10;

  //public static final double maxSpeed = 7.0;
  public static double maxSpeed = .6;
  public static final double minSpeed = .2;

  //fields, in addition to the flyingshit fields
  public Material material; //what is the asteroid made of?
  
  public int astRad; ////the maximum radius of the asteroid-- the larger this is the higher chance there is 
  //of generating a large asteroid (see generateShape() below
  
  
  //constructor.  generates a shape for the asteroid and sets the fields to the 
  //desired paramaters
  public Asteroid(int X, int Y, double xv, double yv,int radius,int mat){
	
	super(X, Y, xv, yv);
	

	this.astRad = radius;
	if(this.astRad > ASTEROID_MAXRADIUS)this.astRad = ASTEROID_MAXRADIUS;
	if(this.astRad < ASTEROID_MINRADIUS)this.astRad = ASTEROID_MINRADIUS;
	
	this.shape = generateShape();
	
	//if(mat == Material.ALIEN_TECH)mat = Material.ROCK;//asteroids don't have ufo tech
	this.material = new Material(mat);
	
	double a = this.getSpeed();
	
	if(a > maxSpeed){
		this.xVec/=a;
		this.yVec/=a;
		this.xVec*=maxSpeed;
		this.yVec*=maxSpeed;
	}
	
	if(a < minSpeed){
		this.xVec/=a;
		this.yVec/=a;
		this.xVec*=minSpeed;
		this.yVec*=minSpeed;
	}
	
  }
  
  
 
  
  
  //generates a new shape for an asteroid
  public Polygon generateShape(){
  	
  	 Random r = new Random();
	 int xpoints[] = new int[ASTEROID_VERTICES];
	 int ypoints[] = new int[ASTEROID_VERTICES];

	 int tmp;
	 double ftmp;
	  
	 	
	 //initialize the arrays for position
	 for(int j = 0; j< ASTEROID_VERTICES;j++){
	 	xpoints[j] = 0;
	 	ypoints[j] = 0;
	 }
	 	
	 
	 //now generate the vertices.  For each subdivision of a circle, generate the point
	 //of a circle with a random radius-- since each radius is different, we get an irregular shaped asteroid
	 for(int j = 0;j<ASTEROID_VERTICES;j++){
	 	
	 	tmp = (r.nextInt()%this.astRad/2)+this.astRad;
	 	
	 
	 	ftmp = j*((Math.PI/(ASTEROID_VERTICES/2)));
	 	
	 	xpoints[j] += (int)tmp*Math.cos(ftmp);
	 	ypoints[j] += (int)tmp*Math.sin(ftmp);
	 
	 }
	
	return new Polygon(xpoints, ypoints, ASTEROID_VERTICES);
  }
  
  
  
  
  //returns astRad
  public int radius(){
  	return this.astRad;
  }
  
  
  
  //draws the asteroid in the appropriate color (based on material)
  //draws in appropriate location 
  public void drawObject(Graphics g){
  
  	g.setColor(this.material.getColor());
  	
  	if(this.material.equals(Material.DARK_MATTER))
  		g.setColor(Color.BLACK);//dark matter is invisible
  	
  	super.drawObject(g);
  	
  }
	
}//end class
