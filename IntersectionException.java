//Asteroid Scavenger
//Christopher Pugh

//an exception thrown when one of the polygons in an Asteroids game's collision test has fewer than 3 points

class IntersectionException extends Exception{
	
	public IntersectionException(String s){
		super(s);
	}
	
}
