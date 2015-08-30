//Asteroid Scavenger
//Chris Pugh

//class for bullets, which are shot both by the player's ship and the UFOs in asteroids

import java.awt.*;

public class Bullet extends FlyingShit {
	
//constants
public static final double DURATION = 1000;//how long does the bullet last?
public static double bulletSpeed = 5; //bullet's speed


private int bulletDiam= 3;//the diamater of a bullet

private double timeCreated=0; //the time a bullet instance was created, used to kill bullets after DURATION time.



//constructor that takes and sets the position and the velocity
//sets the shape to a single point
//sets the time created variable to the system time
public Bullet(int X, int Y, double XV, double YV){
	super(X, Y, XV, YV);
	
	int xp[] = {X};
	int yp[] = {Y};
	
	this.shape = new Polygon(xp,yp ,1);
	
	this.timeCreated = System.currentTimeMillis();
}

	


		
//draws a red oval at the bullet's position, on g
public void drawObject(Graphics g){
  g.setColor(Color.RED);	
  g.fillOval((int)this.x - this.bulletDiam, (int)this.y-this.bulletDiam, bulletDiam, bulletDiam);
}





//returns milliseconds since the bullet was created
public double timeElapsed(){
	return System.currentTimeMillis()-this.timeCreated;
}



//a method that is similar to .intersects of FlyingShit, but 
//when dealing with bullets .intersects throws an IntersectionException
//so you should call .hit on the bullet object
public boolean hit (FlyingShit f) {

  if(f == null)return false;
  
  if(f instanceof Bullet) return false; //bullets dont intersect
  
  	//create a translated version of the shape
  
  	Polygon b = new Polygon(f.shape.xpoints,f.shape.ypoints,f.shape.npoints);
	
		
	for(int i = 0; i < b.npoints;i++){
		b.xpoints[i] += f.x;
		b.ypoints[i] += f.y;
	}
		
  //if the shape contains the location of the bullet
  return b.contains(this.x, this.y);	
}


}//end class
