//Asteroid Scavenger
//by Chris Pugh

//defines the different materials used in the game.
//This is important in determining asteroid behavior, and loot behavior

import java.awt.*;


public class Material {
	
	
	//types of materials
	public static final int ICE = 0; //smallest, or one hit kills
	public static final int IRON  = 1; //same as rock, but drops stuff
	public static final int DIAMOND = 2; //uh...
	public static final int ALIEN_TECH = 3; //comes from UFOs
	public static final int DARK_MATTER = 4; //asteroid is black (effectively invisible)
											//can detect by the speed number with the radar upgrade or by watching the stars closely
	public static final int ROCK = 5; //filler asteroids, no valuable materials just there to piss you off

	//public static final int ANOMALY = 666;//coming soon to a game near you

	//colors of the materials
	public static final Color Material_Colors[] = {Color.BLUE, Color.RED, Color.WHITE, Color.GREEN,Color.DARK_GRAY, Color.GRAY}; //colors for each
	
	//monetary values of the materials
	public static final int Material_Values[] = {200,100,125,500,200,0}; //scores for each
	
	//0 drops for rock
	//200 single drop for ice
	//700 posible for metal'
	//7875 possible for diamond -- actually the most difficult and most expensive tbh
	//500 single for ufo
	//1400 possible for dark matter
	
	
	public int material;
	
	//takes an integer, intended to be one of the class constants for types of materials.  Then the material object "is"
	//that material
	public Material(int m){
		if(m<0 || m > Material_Colors.length)
			m=ROCK;
		material=m;
	}
	
	
	//returns the color that the material object should be displayed in
	public Color getColor(){
		return Material_Colors[this.material];
	}
	
	
	//gets the monetary value of the material object
	public int getValue(){
		return Material_Values[this.material];
	}
	
	
	//takes an integer paramater.  One of the class constants is intended to be passed in.
	//if the material object is the same material as the lookup value m, then the values are equal
	public boolean equals(int m){
		return (m == this.material);
	}
	
	
	//returns a string containing the name of the material object's material type
	public String toString(){
		String[] a = {"Ice", "Iron", "Diamond", "Alien Tech", "Dark Matter"};
		return a[this.material];
	}
	
}
