//Chris Pugh
//Asteroid Scavenger

//a class that generates and stores probabilities for an Asteroids map

import java.util.*;

public class MapStats {
	
	private int[] stats; //the probabilities for each type of object to appear on the map
	
	private static final int MINCHANCE = 10; //minimum chance of finding a certain material
	 
	Ship playerShip;  //used for upgrade-related affectations of slices
	
	
	
	//constructor that takes in a Ship, and generates a default set of probabilities
	public MapStats(Ship p){
		this.playerShip = p;
		stats = new int[7];
		generate();
	}
	
	
	
	//methods to return the probability slice of a particular material
	
	public int rock(){
		return stats[0];
	}
	
	public int ice(){
		return stats[1];
	}
	
	public int metal(){
		return stats[2];
	}
	
	public int diamond(){
		return stats[3];
	}
	
	public int ufo(){
		return stats[4];
	}
	
	public int darkmatter(){
		return stats[5];
	}
	
	public int base(){
		return stats[6];
	}
	
	
	
	
	//generates the size of the "slices" for map generation out of 100, starting with the least valuable
	public void generate(){
		Random r = new Random();
	
	
		int chance = 100 - (stats.length * MINCHANCE); 
			
		//when the actual map is generated, each type of encounter is assigned a "slice" of 0-99 to correspond to, 
		//and a random number in this range determines what appears at that spawn point
		//each encounter type gets a minimum slice size, and whatever portion of 100 that is left when this has been done
		//is randomly assigned to the encounter types in order of least valuable to most valuable
		//a random number in the range of zero to the size of the remainder is generated for each encounter type, and the
		//size of the remainder decreases afterwards
		
		//int temp = 100 - (stats.length * MINCHANCE); //max of a 100% slice, temp gets smaller with each probability generated
		
		//divide the pie up among the encounter types
		for(int i = 0; i < stats.length; i++){
		  
		  stats[i] = r.nextInt(chance);
		  
		  //if the ship has a scanner half the chance of getting ice or rock, 
		  //increasing the chances of getting higher encounters
		  if(i < 2 && playerShip.hasUpgrade(Ship.SCANNER)){
		  	stats[i]/=4;
		  	//System.out.println("Scanner reduces to"+stats[i]);
		  }
		  
		  chance -= stats[i];	
		}
		
		//if there's anything left over, add it to rock
		stats[0] +=chance;
		
		//add the minimum chance to each type of encounter
		for(int i = 0; i < stats.length; i++){
			stats[i] += MINCHANCE;
		}
		
		//if the player ship doesn't come with the radar, don't allow dark matter encounters, because it wouldn't be fair
		//instead split its chance up among all the other types and add the remainder to rock
		if(!playerShip.hasUpgrade(Ship.RADAR)){
			int tmp = stats[5];
			
			stats[0]+= tmp%5;
			stats[5] = 0;
			
			for(int i = 0; i < stats.length;i++){
				if(i == 5)continue;
				stats[i] += tmp/5;
			}
			
			
			
		}
		
	}
	
	
	
	public String toString(){
		return "Chance to find: "+
			"\nValueless Rocks: "+stats[0]+"%"+
			//"nothing" removed all six spawn points will create something
				"\nIce: "+stats[1]+"%"+
					"\nMetals: "+stats[2]+"%"+
						"\nDiamond: "+stats[3]+"%"+
							"\nUFO: "+stats[4]+"%"+
								"\nDark Matter: "+stats[5]+"%";	
	}
	
	
	
}
