//Asteroid Scavenger
//By Chris Pugh

//This class defines and manages the menus for Asteroid Scavenger

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class Menu implements ActionListener{


//information strings for each upgrade/equipment for the buy menu

//consumables
public static final String warpinfos = "Single use disposable warp drive.  Warps to a random location on the board.  Emergency use only! $"+Ship.UPGRADECOST[Ship.WARP];
public static final String shieldinfos = "Diesel powered energy shield. Activate to disentegrate anything that touches your ship. ";  
public static final String superchargeinfos = "Single use powerup. Shots keep going when they hit things, and are twice as fast. $"+Ship.UPGRADECOST[Ship.SUPERCHARGE];
public static final String lifeinfos = "Remember to save some money in case you die, as you can buy an extra life for $"+Ship.LIFECOST;
public static final String armorinfos = "Protects your ship, but only lasts one hit.  Cheaper and easier than the shield.  But beware of debris! $"+Ship.UPGRADECOST[Ship.ARMOR];

//upgrades
public static final String scannerinfos = "Higher chance to find areas with more expensive encounters. $"+Ship.UPGRADECOST[Ship.SCANNER];
public static final String radarinfos = "When in range, the speed of an asteroid will be displayed.  Match speed or detect dark matter asteroids. $"+Ship.UPGRADECOST[Ship.RADAR];
public static final String efficiencyinfos = "You fire twice as fast, and half your shield's energy consumption. $"+Ship.UPGRADECOST[Ship.EFFICIENCY];
public static final String accelinfos = "This upgrade will allow your ship to accelerate and decelerate faster. $"+Ship.UPGRADECOST[Ship.ACCEL];
public static final String tractorinfos = "Loot drops will move in the direction of your ship instead of in the direction the asteroid was moving in. $"+Ship.UPGRADECOST[Ship.TRACTOR];


public static final String quittext = "\n"+
"Thank you for playing Asteroid Scavenger by Chris Pugh.\n"+
"This game was a side project in my freshman year of college circa 2007.\n"+
"It was originally a Java Applet and has been modified to fit your screen.";

public boolean ready = false; //is the menu finished so the game can continue?

Ship playerShip; //the ship this menu responds to 
JApplet j; //the applet on which the menu acts and displays upon

private int shieldcost = 0;

//***The three menu panels for the Asteroids game
JPanel buyMenu;
JPanel deathMenu;
JPanel mainMenu;
//***

//BUY MENU fields
JLabel money;

JRadioButton armorinfo;
JRadioButton warpinfo;
JRadioButton shieldinfo;
JRadioButton superchargeinfo;
JRadioButton scannerinfo;
JRadioButton radarinfo;
JRadioButton energyinfo;
JRadioButton accelinfo;
JRadioButton tractorinfo;
	
JRadioButton buyarmor;		
JRadioButton buywarp;
JRadioButton buyshield;
JRadioButton buysupercharge;
JRadioButton buyscanner;
JRadioButton buyradar;
JRadioButton buyenergy;
JRadioButton buyaccel;
JRadioButton buytractor;

JTextField armor;	
JTextField warp;
JTextField shield;
JTextField supercharge;
JTextField scanner;
JTextField radar;
JTextField energy;
JTextField accel;
JTextField tractor;

JTextField info;
JButton go;

JRadioButton done;

//Death Menu Buttons
JTextField deathprompt;
JRadioButton quitopt;
JRadioButton quit2;
JRadioButton continueopt;
JButton go2; //a copy of "go" for the death menu

//to unselect shit on the menus when we're done
JRadioButton ghost;
JRadioButton ghost2;
JRadioButton ghost3;

JTextArea mapstats;

JRadioButton reject;
JRadioButton accept;
JRadioButton shop;
JButton go3;
	



//handles the pressing of the "go" buttons on the menus 
//if a radio button is selected the appropriate actions are taken
public void actionPerformed(ActionEvent E){
  
  if(buyarmor.isSelected()){
  	buyItem(Ship.ARMOR);return;
  }
  
  if(armorinfo.isSelected()){
  	info.setText(armorinfos);
  	return;
  }
  
  if(shop.isSelected()){
  	addBuyMenu();
  	return;
  }
  
  if(done.isSelected()){
  	addMainMenu();
  	return;
  }
  
  
  if(reject.isSelected()){
  	playerShip.mapstats.generate();
  	mapstats.setText(""+playerShip.mapstats);
  	return;
  }
  
  
  if(continueopt.isSelected()){
  
  	playerShip.revive();
  	playerShip.addPoints(-1000);
  	addMainMenu();
  	return;
  }
  
  if(quitopt.isSelected() || quit2.isSelected()){

  	clearMenu();
  	
  	JTextArea quit = new JTextArea(quittext);
  	quit.setEditable(false);
  	quit.setLineWrap(true);
  	quit.setWrapStyleWord(true);
  	
  	j.add(quit);
  	
  	return;
  }
  
  
  if(radarinfo.isSelected()){
  	info.setText(radarinfos);
  	return;
  }
  
  if(tractorinfo.isSelected()){
  	info.setText(tractorinfos);
  	return;
  }
  
  if(warpinfo.isSelected()){
  	info.setText(warpinfos);
  	return;
  }
  
  if(shieldinfo.isSelected()){
  	info.setText(shieldinfos 
  		+ "$"
  			+shieldcost
  				+ " to recharge " 
  					+ (int)((double)100-(double)playerShip.shield) 
  						+ "%");
  	return;
  }
  
  if(scannerinfo.isSelected()){
  	info.setText(scannerinfos);
  	return;
  }
  
  if(energyinfo.isSelected()){
  	info.setText(efficiencyinfos);
  	return;
  }
  
  if(superchargeinfo.isSelected()){
  	info.setText(superchargeinfos);
  	return;
  }
  
  
  if(buyradar.isSelected()){
  	
  	if(buyItem(Ship.RADAR)){
  		buyMenu.remove(buyradar);}
  	
  	return;
  }
  	
  if(buywarp.isSelected()){
  	buyItem(Ship.WARP);
  	return;
  }
  
  if(buyshield.isSelected()){

  	if(playerShip.hasMoney(shieldcost)){
  	  
  	  if(playerShip.shield < 100){
  	  		
  	  		playerShip.addPoints(-shieldcost);
  	  		playerShip.shield = 100;
  	  		//playerShip.giveUpgrade(item);
  	  		info.setText("Purchase Successful!");
  	  		money.setText("You have $"+playerShip.getPoints());
  	  		
  	  		shieldcost = (int)(((double)100-(double)playerShip.shield)/(double)100*(double)Ship.UPGRADECOST[Ship.SHIELD]);
  	  	
  	  	}else{info.setText("Shield already full!");}
  	  
  	}else{
  		info.setText("Not enough cash!");
  	}
  	
  	
  	return;}
  
  
  
  if(buysupercharge.isSelected()){
  	buyItem(Ship.SUPERCHARGE);
  	return;
  }
  
  if(buyscanner.isSelected()){
 
  	if(buyItem(Ship.SCANNER)){
  		buyMenu.remove(buyscanner);
  	}
  	//playerShip.mapstats.upgrade();
  	return;
  }
    
  if(buyenergy.isSelected()){	
  		if(buyItem(Ship.EFFICIENCY)){
  			buyMenu.remove(buyenergy);
  		}
  		return;
  	}
  	
  	
  if(buyaccel.isSelected()){
  	
  	if(buyItem(Ship.ACCEL)){
		buyMenu.remove(buyaccel);		
  	}
  	
  	return;
  }
  
  
  
  if(buytractor.isSelected()){
  	
  	if(buyItem(Ship.TRACTOR)){
  		buyMenu.remove(buytractor);
  	};
  	
  	return;
  }
 
  
  if(accept.isSelected()){
  	
  	((Asteroids)j).generateRound();
  	
  	this.ready = true;
  	
  	j.addKeyListener((Asteroids)j);
  	
  	clearMenu();
  	
  	return;
  }
  
  
  if(accelinfo.isSelected()){
  	info.setText(accelinfos);
  	return;}
  
  
}



//helper function for the buy menu, deals with requests for playerShip buying upgrades or items
public boolean buyItem(int item){
	
	if(playerShip.hasMoney(Ship.UPGRADECOST[item])){
  	  
  	  if(!playerShip.hasUpgrade(item)){
  	  		
  	  		playerShip.addPoints(-Ship.UPGRADECOST[item]);
  	  		playerShip.giveUpgrade(item);
  	  		info.setText("Purchase Successful!");
  	  		money.setText("You have $"+playerShip.getPoints());
  	  		return true;
  	  	}else{info.setText("You already have that!");
  	  	return false;}
  	  
  	}else{
  		info.setText("Not enough cash!");
  		return false;
  	}	

}




//takes the width of the applet, the applet, and the ship that the menu will deal with
//creates all the objects the menu will need and adds them to the appropriate panes
//don't read this constructor, It'll make you cross-eyed
public Menu(int BOARD_WIDTH, JApplet j, Ship p){
	
	//sets the playership and the applet
	playerShip = p;
	this.j = j;
	
	//don't run the game loop while in a menu
	ready = false;
	
	ghost = new JRadioButton("Don't see this button");
	ghost.setVisible(false);
	
	ghost2 = new JRadioButton("Don't see this button");
	ghost2.setVisible(false);
	
	ghost3 = new JRadioButton("I like pie");
	ghost3.setVisible(false);
	
	
	//Create Main Menu
	
	mainMenu = new JPanel(new FlowLayout());
	
	ButtonGroup b2 = new ButtonGroup();
	
	JTextField title = new JTextField("Asteroid Scavenger", BOARD_WIDTH);
	title.setEditable(false);
	title.setHorizontalAlignment(SwingConstants.CENTER);
	title.setVisible(true);
	
	JTextField bl = new JTextField("", BOARD_WIDTH);
	bl.setEditable(false);
	bl.setHorizontalAlignment(SwingConstants.CENTER);
	bl.setVisible(true);
	
	JTextField bl2 = new JTextField("", BOARD_WIDTH);
	bl2.setEditable(false);
	bl2.setHorizontalAlignment(SwingConstants.CENTER);
	bl2.setVisible(true);
	//JTextField c = new JTextField("", BOARD_WIDTH);
	
	mapstats = new JTextArea(""+playerShip.mapstats);
	mapstats.setEditable(false);
	mapstats.setVisible(true);
	//mapstats.setHorizontalAlignment(SwingConstants.CENTER);
	
	accept = new JRadioButton("Accept this map");
	accept.setVisible(true);

	reject = new JRadioButton("Search again");
	reject.setVisible(true);
	
	quit2 = new JRadioButton("Quit Game");
	quit2.setVisible(true);
	
	shop = new JRadioButton("Buy Upgrades and Equipment");
	shop.setVisible(true);
	
	b2.add(ghost3);
	b2.add(shop);
	b2.add(quit2);
	b2.add(reject);
	b2.add(accept);
	
	
	go3 = new JButton("Go");
	go3.addActionListener(this);
	go3.setVisible(true);
	
	mainMenu.add(title);
	
	mainMenu.add(mapstats);
	
	mainMenu.add(bl);
	 
	mainMenu.add(accept);
	
	mainMenu.add(reject);
		
	mainMenu.add(shop);
		
	mainMenu.add(quit2);
	
	mainMenu.add(ghost3);
	
	mainMenu.add(bl2);
		
	mainMenu.add(go3);
		
	mainMenu.setVisible(true);
	
	
	go = new JButton("Go");
	go.addActionListener(this);
	go.setVisible(true);
	
	go2 = new JButton("Go");
	go2.addActionListener(this);
	go2.setVisible(true);
	
	
	//****CREATE THE DEATH MENU****
	//creates objects, sets visible, and adds them to the panel
	deathMenu = new JPanel(new FlowLayout());
	
	quitopt = new JRadioButton("Quit");
	continueopt = new JRadioButton("Continue");
	deathprompt = new JTextField("Death Prompt", BOARD_WIDTH);
	deathprompt.setEditable(false);
	deathprompt.setHorizontalAlignment(SwingConstants.CENTER);
	//deathprompt.setLineWrap(true);
    //deathprompt.setWrapStyleWord(true);
    
	deathprompt.setVisible(true);
	continueopt.setVisible(true);
	quitopt.setVisible(true);

	
	ButtonGroup b = new ButtonGroup();
	b.add(continueopt);
	b.add(quitopt);
	b.add(ghost2);
	deathMenu.add(deathprompt);
	deathMenu.add(continueopt);
	deathMenu.add(quitopt);
	deathMenu.add(ghost2);
	deathMenu.add(go2);
    
    
    //*****CREATE THE BUY MENU*****
    //creates objects, sets visible, and adds them to the panel
	buyMenu = new JPanel(new FlowLayout());
	
	armorinfo = new JRadioButton("Info");
	warpinfo = new JRadioButton("Info");
	shieldinfo = new JRadioButton("Info");
	superchargeinfo = new JRadioButton("Info");
	scannerinfo = new JRadioButton("Info");
	radarinfo = new JRadioButton("Info");
	energyinfo = new JRadioButton("Info");
	accelinfo = new JRadioButton("Info");
	tractorinfo = new JRadioButton("Info");
		
	buyarmor = new JRadioButton("Buy");	
	buywarp = new JRadioButton("Buy");
	buyshield = new JRadioButton("Buy");
	buysupercharge = new JRadioButton("Buy");
	buyscanner = new JRadioButton("Buy");
	buyradar = new JRadioButton("Buy");
	buyenergy = new JRadioButton("Buy");
	buyaccel = new JRadioButton("Buy");
	buytractor = new JRadioButton("Buy");
	
	armor = new JTextField("Armor", BOARD_WIDTH);
	armor.setHorizontalAlignment(SwingConstants.CENTER);
	armor.setEditable(false);
	
	warp = new JTextField("Warp", BOARD_WIDTH);
	warp.setHorizontalAlignment(SwingConstants.CENTER);
	warp.setEditable(false);
		
	shield = new JTextField("Recharge Shield", BOARD_WIDTH);
	shield.setHorizontalAlignment(SwingConstants.CENTER);
	shield.setEditable(false);
	
	supercharge = new JTextField("Supercharge",BOARD_WIDTH);
	supercharge.setHorizontalAlignment(SwingConstants.CENTER);
	supercharge.setEditable(false);
	
	scanner = new JTextField("Upgrade Scanners", BOARD_WIDTH);
	scanner.setHorizontalAlignment(SwingConstants.CENTER);
	scanner.setEditable(false);
	
	radar = new JTextField("Matter Detector",BOARD_WIDTH);
	radar.setHorizontalAlignment(SwingConstants.CENTER);
	radar.setEditable(false);
	
	energy = new JTextField("Upgrade Energy Efficiency",BOARD_WIDTH);
	energy.setHorizontalAlignment(SwingConstants.CENTER);
	energy.setEditable(false);
	
	accel = new JTextField("Improved Thrusters",BOARD_WIDTH);
	accel.setHorizontalAlignment(SwingConstants.CENTER);
	accel.setEditable(false);
	
	tractor = new JTextField("Tractor Beam",BOARD_WIDTH);
	tractor.setHorizontalAlignment(SwingConstants.CENTER);
	tractor.setEditable(false);
	
	
	
	done = new JRadioButton("Done");

	info = new JTextField(lifeinfos, BOARD_WIDTH);
	info.setEditable(false);
	info.setHorizontalAlignment(SwingConstants.CENTER);
	//info.setLineWrap(true);
	//info.setWrapStyleWord(true);
	
	money = new JLabel("You have $"+0);
	
	warpinfo.setVisible(true);
	shieldinfo.setVisible(true);
	superchargeinfo.setVisible(true);
	scannerinfo.setVisible(true);
	radarinfo.setVisible(true);
	energyinfo.setVisible(true);
	accelinfo.setVisible(true);
	tractorinfo.setVisible(true);
	armorinfo.setVisible(true);
	
	buyarmor.setVisible(true);	
	buywarp.setVisible(true);
	buyshield.setVisible(true);
	buysupercharge.setVisible(true);
	buyscanner.setVisible(true);
	buyradar.setVisible(true);
	buyenergy.setVisible(true);
	buyaccel.setVisible(true);
	buytractor.setVisible(true);
	
	armor.setVisible(true);
	warp.setVisible(true);
	shield.setVisible(true);
	supercharge.setVisible(true);
	scanner.setVisible(true);
	radar.setVisible(true);
	energy.setVisible(true);
	accel.setVisible(true);
	tractor.setVisible(true);
	done.setVisible(true);
	info.setVisible(true);
	go.setVisible(true);
	money.setVisible(true);
	
	money.setText("You have $"+playerShip.getPoints());
	
	ButtonGroup buttons = new ButtonGroup();
	
	buttons.add(buyarmor);
	buttons.add(armorinfo);
	buttons.add(buywarp);
	buttons.add(buyshield);
	buttons.add(buysupercharge);
	buttons.add(buyscanner);
	buttons.add(buyradar);
	buttons.add(buyenergy);
	buttons.add(buyaccel);
	buttons.add(buytractor);
	buttons.add(warpinfo);
	buttons.add(shieldinfo);
	buttons.add(superchargeinfo);
	buttons.add(scannerinfo);
	buttons.add(radarinfo);
	buttons.add(energyinfo);
	buttons.add(accelinfo);
	buttons.add(tractorinfo);
	buttons.add(done);
	buttons.add(ghost);

	
	buyMenu.add(money);
	buyMenu.add(armor);
	buyMenu.add(buyarmor);
	buyMenu.add(armorinfo);
	buyMenu.add(warp);
	buyMenu.add(buywarp);
	buyMenu.add(warpinfo);
    buyMenu.add(shield);
    buyMenu.add(buyshield);
    buyMenu.add(shieldinfo);
	//buyMenu.add(supercharge);
	//buyMenu.add(buysupercharge);
	//buyMenu.add(superchargeinfo);
	buyMenu.add(scanner);
	buyMenu.add(buyscanner);
	buyMenu.add(scannerinfo);
	buyMenu.add(radar);
	buyMenu.add(buyradar);
	buyMenu.add(radarinfo);
	buyMenu.add(energy);
	buyMenu.add(buyenergy);
	buyMenu.add(energyinfo);
	buyMenu.add(accel);
	buyMenu.add(buyaccel);
	buyMenu.add(accelinfo);
	buyMenu.add(tractor);
	buyMenu.add(buytractor);
	buyMenu.add(tractorinfo);

	buyMenu.add(info);
	
	buyMenu.add(done);
	buyMenu.add(go);

	
	
}



//adds the death quit/continue prompt to the applet
public void addDeathMenu(){
	j.removeKeyListener((Asteroids)j);
	
	ready = false;
	
	clearMenu();
	
	boolean bool = playerShip.getPoints() >= Ship.LIFECOST;
	
	if(bool){	
		deathprompt.setText("You have crashed your ship.  You can spend $"+Ship.LIFECOST+" to have it repaired or retire with your $"+playerShip.getPoints());	
	}else{
		deathprompt.setText("You have crashed your ship.  Unfortunately, you don't have the money for repairs, and you must retire into obscurity!");
	}
	
	continueopt.setVisible(bool);
	
	
	
	j.add(deathMenu);
	deathMenu.setVisible(true);
	
	
	//j.setVisible(true);
//	j.repaint();
	
}



//adds the buy menu to the applet
public void addBuyMenu(){
	
	shieldcost = (int)(((double)100-(double)playerShip.shield)/(double)100*(double)Ship.UPGRADECOST[Ship.SHIELD]);
	
	j.removeKeyListener((Asteroids)j);
	
	money.setText("You have $"+playerShip.getPoints());
	
	ready = false;
	
	clearMenu();
	
	j.add(buyMenu);
	buyMenu.setVisible(true);
	
  //j.setVisible(true);
	//j.repaint();
	
}



//sets the visibility of all the menu components to false and removes them
public void clearMenu(){
	
	ghost.setSelected(true);
	ghost2.setSelected(true);
	ghost3.setSelected(true);
	
	buyMenu.setVisible(false);
	deathMenu.setVisible(false);
	mainMenu.setVisible(false);
	
	j.remove(buyMenu);
	j.remove(deathMenu);
	j.remove(mainMenu);
	
	
 }
 
 
 
//adds the Main menu to the applet
public void addMainMenu(){
	
	playerShip.mapstats.generate();
	mapstats.setText(""+playerShip.mapstats);
	
	j.removeKeyListener((Asteroids)j);
	
	ready = false;
	
	clearMenu();
	
	j.add(mainMenu);
	mainMenu.setVisible(true);
	
  //j.setVisible(true);
	//j.repaint();
	
} 




}		