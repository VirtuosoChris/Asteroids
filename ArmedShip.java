//Asteroid Scavenger
//Chris Pugh

//an interface for a ship that can fire bullets at a certain rate
//implemented by Ship and UFO in the game

interface ArmedShip {

public void newFireTime();

public double timeLastFired();

public Bullet fire();

}
