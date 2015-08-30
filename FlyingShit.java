//Asteroid Scavenger
//Chris Pugh

//FlyingShit Class is a master class for all the moving objects in the asteroids game
//contains a number of common fields and methods to handle movement, location, drawing, etc.

import java.awt.*;


abstract class FlyingShit {
	
	protected double x;  //position
	protected double y;
	protected double xVec; //velocity
	protected double yVec;
	protected Polygon shape = null;  //the shape of the Object 

	public boolean dead = false;  //is the object flagged for destruction?

	//constructor, all fields are zero or null
	public FlyingShit(){
	  x = y = 0;
	  xVec = yVec = 0;
	}
	
	//constructor, sets position leaves the other fields as 0 or null
	public FlyingShit(int sx, int sy){
		this.x = (double)sx;
  		this.y = (double)sy;
  		this.xVec = 0;
  		this.yVec = 0;
  		this.shape = null;

	}
	
	
	
	//constructor that sets the movement and position but leaves the shape undefined
	public FlyingShit(int X, int Y, double XV, double YV){
		this.x = X;
		this.y = Y;
		this.xVec = XV;
		this.yVec = YV;
	}
	
	
	//constructor, same as above, except the shape is defined with p
	public FlyingShit(int X, int Y, double xv, double yv, Polygon p){
  	  this.x = X;
   	  this.y = Y;
	  this.xVec = xv;
	  this.yVec = yv;
	  this.shape = p;
    }
	
	
	//returns the speed of the flyingshit to two decimal places
	public double getSpeed(){	
	return (double)((int)(Math.sqrt(xVec*xVec + yVec*yVec)*100))/100;}


	//returns the distance from another FlyingShit Object
	public double getDistance(FlyingShit f){
		double a = (this.x - f.getX());
		double b = this.y - f.getY();
		
		return Math.sqrt(a*a + b*b);
	}
	
	
 	//returns the x component of the position of the object
 	public int getX(){
 	  return (int) this.x;
 	}
 	
 	
 	
 	//returns the y component of the position of the object
 	public int getY(){
 	  return (int) this.y;
 	}
 	
 	
 	
 	
	//returns the x component of the object's velocity
 	public double getxVec(){
 	  return this.xVec;
 	}
	
	
	
	//returns the y component of the object's velocity
 	public double getyVec(){
 	  return this.yVec;
 	}
	
	
	
	//mutator to set the velocity to <a, b>
	public void setVelocity(double a, double b){
		this.xVec = a;
		this.yVec = b;
	}
	
	
	
	//displace the object along its movement vector, keep within the boundaries xb and yb
	public final void move(int xb, int yb){
		this.x+= this.xVec;
		this.y+= this.yVec;
		
		if(x > xb)x = 0;
		if(x < 0)x = xb;
		if(y > yb)y = 0;
		if(y < 0)y =yb;
	}
	
	
	
	
	
	//tests if the polygons of this and f contain points of each other
	public boolean intersects (FlyingShit f) throws IntersectionException{
	
		if(f == null)return false;
	
		
		//Polygon a = this.shape; hahaha this was causing bugs and i wondered why. damn pointer-free language :)
		
		Polygon a = new Polygon(this.shape.xpoints,this.shape.ypoints,this.shape.npoints);
		
		for(int i = 0; i < a.npoints;i++){
			a.xpoints[i] += this.x;
			a.ypoints[i] += this.y;
		}
		
		Polygon b = new Polygon(f.shape.xpoints,f.shape.ypoints,f.shape.npoints);
		
		for(int i = 0; i < b.npoints;i++){
			b.xpoints[i] += f.x;
			b.ypoints[i] += f.y;
		}
		
		
		if(a == null || b == null)return false;
		
		
		//throws an exception if one of the shapes is not a closed polygon
		//if using a bullet, call the bullet class hit method
		if(a.npoints < 3 || b.npoints < 3)
		{throw new IntersectionException("Invalid Polygons for collision");}
		
		
		//test all the points of a for inclusion in b
		for(int i = 0; i < a.npoints;i++){
		  if(b.contains(a.xpoints[i], a.ypoints[i]))return true;}
		
		
		//test all the points of b for inclusion in a
		for(int i = 0; i < b.npoints;i++){
		  if(a.contains(b.xpoints[i], b.ypoints[i]))return true;}
		
		
		return false;
		
	}
	
	
	
	//sets the position to tx, ty
	public void setPosition(int tx, int ty){
		this.x = tx;
		this.y = ty;
	}
	
	
	
	//draws the object in the appropriate location on g, with g's current color
	//draws this.shape by drawing each line of the polygon with the coordinates of each point displaced by
	//this.x and this.y	
	public void drawObject(Graphics g){
		
		if(this.shape == null)return;
		
		for(int i  = 0; i < this.shape.npoints;i++){
			g.drawLine((int)this.x+this.shape.xpoints[i%this.shape.npoints],
					   (int)this.y+this.shape.ypoints[i%this.shape.npoints],
					   (int)this.x+this.shape.xpoints[(i+1)%this.shape.npoints],
					   (int)this.y+this.shape.ypoints[(i+1)%this.shape.npoints]);
		}//end for
		
	}//end method



}//end class
