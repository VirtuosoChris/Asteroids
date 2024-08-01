//central project class, handles game loop, input, applet functions, etc

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.applet.*;
import java.awt.event.*;
import java.net.*;

public class Asteroids extends JApplet implements Runnable, KeyListener
{
    //used for debugging.  acts as a permanent (invisible) shield. hands off cheater!
    public boolean godmode = false;

    //sound stuff
    boolean sound = true;

    AudioClip brake;
    AudioClip cash;
    AudioClip laser;
    AudioClip warp;
    AudioClip boom;
    AudioClip thrust;
    AudioClip ufolaser;

    //the width and height of the rectangle upon which the game is rendered.  Should be the same as applet width and height
    public static final int BOARD_WIDTH = 800;
    public static final int BOARD_HEIGHT = 600;

    private static final int FPS = 75; //determines how long the delay is between frames

    //every asteroids game needs bullets, asteroids, UFOs, and a ship
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Bullet> bullets;
    private ArrayList<Loot> loot;
    ArrayList<UFO> ufos;

    private Ship playerShip;

    //other necessary variables
    private Image stars;//the game's background image
    private static final int NUMSTARS = 750;//the number of stars to draw in the background
    private Image offScrnBfr = null; //off screen buffer to prevent flickering
    private Graphics og = null;      //graphics context for the offscreen buffer
    Thread t; //thread for the game loop

    private boolean paused = true; //for use with the 'p' key, not the menus
    private boolean mapcleared = false; //a flag as to whether or not all the destructibles and collectibles are off the game board

    //keeps track of when the user died or cleared the map
    //used to give a 5 second delay before returning to the menu
    private double timeCleared = 0;
    private double timeDied = 0;

    //Uh, kind of self explanatory
    Menu menu;

    //flags for whether or not keys are pressed
    private boolean up = false, down = false, left = false, right = false, space = false;

	public static void main(String[] args)
	{
        JFrame frame = new JFrame("Chris Pugh's Asteroids");
        Asteroids applet = new Asteroids();
        frame.add(applet);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.setVisible(true);

		//frame.addKeyListener(applet);

        applet.init();
        applet.start();
    }

    //----------------------------------------------------------------------------------
    //---------------------Asteroids Game Helper Functions------------------------------
    //----------------------------------------------------------------------------------
    //creates and starts the game update thread
    public void startGame()
	{
        t = new Thread(this);
        t.start();
    }

    //given an asteroid and a FlyingShit, spawns two child asteroids with smaller radii, if the
    //decreased radius is not below the minimum
    //the flyingshit is to affect the vector of the children
    public void spawnChildren(Asteroid ast, FlyingShit x) {

        //ice asteroids spawn no children, and dissolve immediately
        if (ast.material.equals(Material.ICE)) {
            return;
        }

        int trad = 0;

        //diamonds degrade slower than other materials
        //their new radius is 5/6 instead of 2/3
        if (ast.material.equals(Material.DIAMOND)) {
            trad = 5 * ast.radius() / 6;
        } else {
            trad = 2 * ast.radius() / 3;
        }

        //if the radius of a fragment is large enough, create them.  otherwise, the rock is completely destroyed
        if (trad >= Asteroid.ASTEROID_MINRADIUS) {
            asteroids.add(new Asteroid(ast.getX(), ast.getY(), ast.getxVec() + x.getxVec() / 2, ast.getyVec() + x.getyVec() / 2, trad, ast.material.material));
            asteroids.add(new Asteroid(ast.getX(), ast.getY(), -(ast.getxVec() + x.getxVec() / 2), -(ast.getyVec() + x.getyVec() / 2), trad, ast.material.material));
        }

    }//end method

    //resets the game variables and generates a new background for a new round
    public void generateRound() {

        Asteroid.maxSpeed += .2;
        UFO.SPEED += .1;

        left = down = up = right = false;

        mapcleared = false;

        if (playerShip != null) {
            playerShip.setPosition(BOARD_WIDTH / 2, BOARD_HEIGHT / 2);
            playerShip.setVelocity(0, 0);
            playerShip.revive();
        }

        Random r = new Random();

        stars = generateStars();

        asteroids = new ArrayList<Asteroid>();
        bullets = new ArrayList<Bullet>();
        loot = new ArrayList<Loot>();
        ufos = new ArrayList<UFO>();

        //there are a certain number of fixed spawn points for objects, and
        //what type of object is random based on the probabilities of the playerShip's mapstats class				
        ArrayList<Point> spawns = new ArrayList<Point>();

        spawns.add(new Point(0, 0));
        spawns.add(new Point(BOARD_WIDTH, 0));
        spawns.add(new Point(BOARD_WIDTH, BOARD_HEIGHT));
        spawns.add(new Point(0, BOARD_HEIGHT));
        //spawns.add(new Point(0,BOARD_HEIGHT/2));
        //spawns.add(new Point(BOARD_WIDTH,BOARD_HEIGHT/2));

        ArrayList<Point> uspawns = new ArrayList<Point>();

        //aliens get their own set of spawn points, for their own safety
        uspawns.add(new Point(100, 100));
        uspawns.add(new Point(BOARD_WIDTH - 100, 100));
        uspawns.add(new Point(BOARD_WIDTH - 100, BOARD_HEIGHT - 100));
        uspawns.add(new Point(100, BOARD_HEIGHT - 100));
        //uspawns.add(new Point(100,BOARD_HEIGHT/2));
        //uspawns.add(new Point(BOARD_WIDTH-100,BOARD_HEIGHT/2));

        for (int i = 0; i < spawns.size(); i++) {

            double theta = r.nextDouble() * (2 * Math.PI);
            double speed = r.nextDouble() * 4;

            int roll = r.nextInt(100);
            //generate a number between 1 and 100, for use with the mapstats probability slices
            //to determine what type of opponent the spawn will get

            int tmp = 0;

            if (roll < (tmp += playerShip.mapstats.rock())) {
                asteroids.add(new Asteroid(
                        (int) spawns.get(i).getX(),
                        (int) spawns.get(i).getY(),
                        (double) speed * Math.cos(theta),
                        (double) speed * Math.sin(theta),
                        Asteroid.ASTEROID_MAXRADIUS,
                        Material.ROCK));
            } else if (roll < (tmp += playerShip.mapstats.ice())) {
                asteroids.add(new Asteroid(
                        (int) spawns.get(i).getX(),
                        (int) spawns.get(i).getY(),
                        (double) speed * Math.cos(theta),
                        (double) speed * Math.sin(theta),
                        Asteroid.ASTEROID_MAXRADIUS,
                        Material.ICE));
            } else if (roll < (tmp += playerShip.mapstats.metal())) {
                asteroids.add(new Asteroid(
                        (int) spawns.get(i).getX(),
                        (int) spawns.get(i).getY(),
                        (double) speed * Math.cos(theta),
                        (double) speed * Math.sin(theta),
                        Asteroid.ASTEROID_MAXRADIUS,
                        Material.IRON));
            } else if (roll < (tmp += playerShip.mapstats.diamond())) {
                asteroids.add(new Asteroid(
                        (int) spawns.get(i).getX(),
                        (int) spawns.get(i).getY(),
                        (double) speed * Math.cos(theta),
                        (double) speed * Math.sin(theta),
                        Asteroid.ASTEROID_MAXRADIUS,
                        Material.DIAMOND));
            } else if (roll < (tmp += playerShip.mapstats.ufo())) {
                ufos.add(new UFO((int) uspawns.get(i).getX(), (int) uspawns.get(i).getY()));
            } else if (roll < (tmp += playerShip.mapstats.darkmatter())) {
                asteroids.add(new Asteroid(
                        (int) spawns.get(i).getX(),
                        (int) spawns.get(i).getY(),
                        (double) speed * Math.cos(theta),
                        (double) speed * Math.sin(theta),
                        Asteroid.ASTEROID_MAXRADIUS,
                        Material.DARK_MATTER));
            } else if (roll < (tmp += playerShip.mapstats.base())) {
                asteroids.add(new Asteroid(
                        (int) spawns.get(i).getX(),
                        (int) spawns.get(i).getY(),
                        (double) speed * Math.cos(theta),
                        (double) speed * Math.sin(theta),
                        Asteroid.ASTEROID_MAXRADIUS,
                        Material.ALIEN_TECH));
            }

        }//end for-spawnpoints

    }//end method

    //draws all the game objects to the backbuffer's Graphics object
    public void drawGame() {

        if (og == null) {
            return;
        }

        og.drawImage(this.stars, 0, 0, null);

        if (asteroids != null) {
            for (FlyingShit o : asteroids) {

                if (o == null || o.dead) {
                    continue;
                }

                o.drawObject(og);

                og.setColor(Color.GREEN);

                //draws the radar numbers on the asteroids
                if (playerShip != null && playerShip.isAlive()) {

                    if ((playerShip.hasUpgrade(Ship.RADAR) && playerShip.getDistance(o) <= Ship.RADAR_DISTANCE) || godmode) {
                        og.drawString("" + o.getSpeed(), o.getX(), o.getY());
                    }
                }

            }
        }

        if (loot != null) {
            for (Loot l : loot) {
                if (l == null || l.dead) {
                    continue;
                }
                l.drawObject(og);
            }
        }

        if (bullets != null) {
            for (FlyingShit o : bullets) {
                if (o == null || o.dead) {
                    continue;
                }
                o.drawObject(og);
            }
        }

        if (playerShip != null && playerShip.isAlive()) {

            playerShip.drawObject(og);
            og.setColor(Color.ORANGE);
            og.drawString("$" + playerShip.getPoints(), 10, 20);
            og.drawString("Speed: " + playerShip.getSpeed(), 10, 35);

            og.drawString("Shield: " + (int) playerShip.shield + "%", BOARD_WIDTH - 100, 20);

            if (playerShip.hasUpgrade(Ship.WARP)) {
                og.drawString("Warp", BOARD_WIDTH - 100, 40);
            }

            if (playerShip.hasUpgrade(Ship.SUPERCHARGE)) {
                og.drawString("Supercharge", BOARD_WIDTH - 100, 60);
            }

            if (playerShip.hasUpgrade(Ship.ARMOR)) {
                og.drawString("Armored", BOARD_WIDTH - 100, 60);
            }

            if (playerShip.hasUpgrade(Ship.RADAR)) {
                og.drawString("Targets Remaining: " + (asteroids.size() + ufos.size()), 10, BOARD_HEIGHT - 20);
            }

        }

        for (UFO ufo : ufos) {
            if (ufo == null || ufo.dead) {
                continue;
            }
            ufo.drawObject(og); //Test UFO
        }

        for (Bullet b : UFO.UfoBullets) {
            if (b == null || b.dead) {
                continue;
            }
            b.drawObject(og);
        }

        if (paused) {
            og.setColor(Color.GREEN);
            og.drawString("GAME PAUSED", BOARD_WIDTH / 2 - 30, BOARD_HEIGHT / 4);

            og.drawString("Up Arrow : Accelerate", BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 4 + 40);
            og.drawString("Left/Right Arrows : Rotate", BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 4 + 60);
            og.drawString("Down Arrow : Brake", BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 4 + 80);
            og.drawString("Space : Fire", BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 4 + 100);

            og.drawString("W : Use Warp Drive", BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 4 + 140);
            og.drawString("S : Activate Shield", BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 4 + 160);

            og.drawString("P : Toggle Pause", BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 4 + 200);
            og.drawString("N : Toggle Sound", BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 4 + 220);
            return;
        }

        if (playerShip != null && !playerShip.isAlive()) {
            og.setColor(Color.GREEN);
            og.drawString("You Crashed!", BOARD_WIDTH / 2 - 25, BOARD_HEIGHT / 2);
            return;
        }

        if (mapcleared) {
            og.setColor(Color.GREEN);
            og.drawString("Map Cleared!", BOARD_WIDTH / 2 - 50, BOARD_HEIGHT / 2);
            og.drawString("+$1000", BOARD_WIDTH / 2 - 30, BOARD_HEIGHT / 2 + 15);
            return;
        }

    }//end method

    //creates a background image containing a random assortment of stars
    public Image generateStars() {

        Random r = new Random();

        Image im = createImage(BOARD_WIDTH, BOARD_HEIGHT);
        Graphics ig = im.getGraphics();

        ig.setColor(Color.BLACK);
        ig.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);

        ig.setColor(Color.BLUE);

        for (int i = 0; i < 1000; i++) {
            ig.setColor(new Color(r.nextInt() % 255));
            ig.fillOval(r.nextInt() % BOARD_WIDTH, r.nextInt() % (BOARD_HEIGHT), 1, 2);
        }

        return im;

    }

    //-------------------------------------------------------------------
    //------------------Game Loop, Run Method----------------------------
    //-------------------------------------------------------------------
    //game update thread
    //if the game is paused just repaint 
    //if a menu is active don't update the game thread at all
    //otherwise move and update all the game objects and their interactions 
    //and update the game state
    public void run() {

        while (true) {

            if (!paused) {

                try {

                    if (!menu.ready) {
                        thrust.stop();
                        continue;
                    }

                    //handle shield consumption
                    if (playerShip.alive) {
                        if (playerShip.shielded) {
                            if (playerShip.shield > 0) {

                                double stmp = Ship.SHIELD_RATE / (double) this.FPS;

                                if (playerShip.hasUpgrade(Ship.EFFICIENCY)) {
                                    stmp /= 2;
                                }

                                playerShip.shield -= stmp;

                                if (playerShip.shield <= 0) {
                                    playerShip.shielded = false;
                                    playerShip.shield = 0;
                                }

                            }
                        }
                    }

                    //list of move targets for UFOS
                    ArrayList<FlyingShit> temp1 = new ArrayList<FlyingShit>();

                    for (FlyingShit o : loot) {
                        temp1.add(o);
                    }

                    temp1.add(playerShip);

                    //list of shoot targets for UFOs
                    ArrayList<FlyingShit> temp2 = new ArrayList<FlyingShit>();
                    temp2.add(playerShip);

                    for (FlyingShit o : asteroids) {
                        temp2.add(o);
                    }

                    //move all the UFOs after updating the AI
                    for (UFO ufo : ufos) {

                        if (ufo == null || ufo.dead) {
                            continue;
                        }

                        //update the AI
                        int tmp = UFO.UfoBullets.size();

                        ufo.update(temp1, temp2);

                        if (UFO.UfoBullets.size() > tmp) {
                            if (ufolaser != null && sound) {
                                ufolaser.play();
                            }
                        }
                        //and apply the new movement vector
                        ufo.move(BOARD_WIDTH, BOARD_HEIGHT);
                    }

                    //update and move the player's ship
                    if (playerShip != null && playerShip.isAlive()) {

                        if (up) {
                            playerShip.accelerate();
                        }

                        if (down) {
                            playerShip.decelerate();
                        }

                        if (left) {
                            playerShip.rotate(-Math.PI / 25);
                        }

                        if (right) {
                            playerShip.rotate(Math.PI / 25);
                        }

                        if (space) {

                            Bullet bull = null;

                            if (bullets != null && playerShip != null) {

                                bull = playerShip.fire();

                                if (bull != null) {

                                    if (sound && laser != null) {
                                        laser.play();
                                    }

                                    bullets.add(bull);
                                }
                            }
                        }

                        playerShip.move(BOARD_WIDTH, BOARD_HEIGHT);

                    }

                    //move all the bullets
                    for (Bullet o : bullets) {
                        if (o == null || o.dead) {
                            continue;
                        }
                        o.move(BOARD_WIDTH, BOARD_HEIGHT);
                    }

                    //move all the UFO bullets
                    for (Bullet b : UFO.UfoBullets) {
                        if (b == null || b.dead) {
                            continue;
                        }
                        b.move(BOARD_WIDTH, BOARD_HEIGHT);
                    }

                    //move all the asteroids
                    for (Asteroid o : asteroids) {
                        if (o == null || o.dead) {
                            continue;
                        }
                        o.move(BOARD_WIDTH, BOARD_HEIGHT);

                        if (o.material.equals(Material.ALIEN_TECH)) {

                            UFO.UfoBullets.add(new Bullet(o.getX(), o.getY(), playerShip.getX() / playerShip.getDistance(o), playerShip.getY() / playerShip.getDistance(o)));
                        }

                    }

                    //move all the loots
                    for (Loot l : loot) {

                        if (l == null || l.dead) {
                            continue;
                        }

                        //if the playership has the tractor beam upgrade set the loots to move towards the ship at double the normal loot speed
                        if (playerShip != null && playerShip.hasUpgrade(Ship.TRACTOR) && playerShip.isAlive()) {

                            double tx = (playerShip.getX() - l.getX()) / playerShip.getDistance(l);
                            double ty = (playerShip.getY() - l.getY()) / playerShip.getDistance(l);

                            tx *= Loot.LOOT_SPEED * 2;
                            ty *= Loot.LOOT_SPEED * 2;

                            l.setVelocity(tx, ty);

                        }

                        l.move(BOARD_WIDTH, BOARD_HEIGHT);

                    }

                    //flag ufo bullets past their prime for destruction
                    if (!UFO.UfoBullets.isEmpty()) {
                        for (int i = 0; i < UFO.UfoBullets.size(); i++) {

                            Bullet o = UFO.UfoBullets.get(i);
                            if (o == null || o.dead) {
                                continue;
                            }

                            if (o.timeElapsed() > (double) Bullet.DURATION) {
                                o.dead = true;
                            }
                        }
                    }

                    //flag bullets past their prime for destruction
                    if (!bullets.isEmpty()) {
                        for (int i = 0; i < bullets.size(); i++) {

                            Bullet o = bullets.get(i);

                            if (o == null || o.dead) {
                                continue;
                            }

                            if (o.timeElapsed() > (double) Bullet.DURATION) {
                                o.dead = true;
                            }
                        }
                    }

                    //flag loots past their prime for destruction 
                    //give the player an opportunity for a "victory lap" 
                    //to pick up that last loot on the map if all the asteroids are gone
                    if (!asteroids.isEmpty() || !ufos.isEmpty()) {
                        for (int i = 0; i < loot.size(); i++) {

                            Loot o = loot.get(i);
                            if (o == null || o.dead) {
                                continue;
                            }

                            if (o.timeElapsed() > (double) Loot.DURATION) {
                                o.dead = true;
                            }
                        }
                    }

                    ///////////////////////////
	      
	      try {

                        //destroy rocks hit by player bullets
                        //destroy the bullet once it hits something
                        for (int i = 0; i < asteroids.size(); i++) {

                            for (int j = 0; j < bullets.size(); j++) {

                                Asteroid ast = asteroids.get(i);

                                if (ast == null || ast.dead) {
                                    continue;
                                }
                                //if the asteroid is already dead pay no further heed to it

                                Bullet x = bullets.get(j);

                                if (x == null) {
                                    return;
                                }

                                if (!x.dead && x.hit(ast)) {

                                    //    bulletFlag.add(j);
                                    //    asteroidFlag.add(i);
                                    x.dead = true;
                                    ast.dead = true;

                                    if (sound && boom != null) {
                                        boom.play();
                                    }

                                    spawnChildren(ast, x);

                                    //rock drops nothing
                                    if (ast.material.material != Material.ROCK) {
                                        loot.add(new Loot(ast, ast.material));
                                    }
                                }
                            }

                        }

                        //destroy ufos hit by player bullets
                        //destroy the bullet when it hits
                        for (UFO u : ufos) {

                            if (u == null || u.dead) {
                                continue;
                            }

                            for (Bullet b : bullets) {

                                if (b == null || b.dead) {
                                    continue;
                                }

                                if (b.hit(u)) {
                                    b.dead = true;
                                    u.dead = true;

                                    if (sound && boom != null) {
                                        boom.play();
                                    }

                                    loot.add(new Loot(u, new Material(Material.ALIEN_TECH)));

                                }
                            }
                        }

                        //destroy rocks hit by ufo bullets
                        //destroy the bullet once it hits something
                        for (int i = 0; i < asteroids.size(); i++) {

                            for (int j = 0; j < UFO.UfoBullets.size(); j++) {

                                Bullet x = UFO.UfoBullets.get(j);
                                Asteroid ast = asteroids.get(i);

                                if (ast == null || ast.dead) {
                                    break;
                                }
                                if (x == null || x.dead) {
                                    continue;
                                }

                                if (x.hit(ast)) {
                                    //bulletFlag2.add(j);
                                    //asteroidFlag.add(i);

                                    if (sound && boom != null) {
                                        boom.play();
                                    }

                                    x.dead = true;
                                    ast.dead = true;

                                    spawnChildren(ast, x);

                                    //rock drops nothing, other materials drop their loot
                                    if (ast.material.material != Material.ROCK) {
                                        loot.add(new Loot(ast, ast.material));
                                    }
                                }
                            }

                        }

                        if (playerShip != null && playerShip.isAlive()) {

                            //kill player ship if hit by UFO.  UFO survives and floats there as a taunt
                            for (UFO o : ufos) {

                                if (o == null || o.dead) {
                                    continue;
                                }

                                if (o.intersects(playerShip)) {

                                    if (!godmode && !playerShip.shielded) {

                                        if (!playerShip.hasUpgrade(Ship.ARMOR)) {

                                            playerShip.die();

                                            if (sound && boom != null) {
                                                boom.play();
                                            }

                                            timeDied = System.currentTimeMillis();

                                            break;
                                        } else {
                                            //more likely than not, the player will still die, and lose $250
                                            //it's up to whether or not the collision is still true the next frame though, so there's a 
                                            //chance
                                            playerShip.removeUpgrade(Ship.ARMOR);
                                        }

                                    } else {//player shield can kill a ufo

                                        if (sound && boom != null) {
                                            boom.play();
                                        }

                                        o.dead = true;

                                        loot.add(new Loot(o, new Material(Material.ALIEN_TECH)));

                                        break;
                                    }

                                }
                            }

                        }//end if-alive

                        //kill player ship if hit by ufo bullet
                        if (playerShip != null && playerShip.isAlive()) {
                            for (Bullet b : UFO.UfoBullets) {

                                if (b == null || b.dead) {
                                    continue;
                                }

                                if (b.hit(playerShip)) {

                                    if (!godmode && !playerShip.shielded) {
                                        if (!playerShip.hasUpgrade(Ship.ARMOR)) {
                                            if (sound && boom != null) {
                                                boom.play();
                                            }

                                            b.dead = true;
                                            playerShip.die();
                                            timeDied = System.currentTimeMillis();
                                            break;
                                        } else {
                                            playerShip.removeUpgrade(Ship.ARMOR);
                                        }

                                    }

                                }
                            }
                        }

                        //kill player ship if hit by rock
                        if (playerShip != null && playerShip.isAlive()) {
                            for (Asteroid o : asteroids) {

                                if (o == null || o.dead) {
                                    continue;
                                }

                                if (playerShip.intersects(o)) {

                                    spawnChildren(o, playerShip);

                                    if (sound && boom != null) {
                                        boom.play();
                                    }

                                    o.dead = true; //even if we're shielded break the rock
                                    if (o.material.material != Material.ROCK) {
                                        loot.add(new Loot(o, o.material));
                                    }

                                    if (!godmode && !playerShip.shielded) {

                                        if (!playerShip.hasUpgrade(Ship.ARMOR)) {
                                            playerShip.die();
                                            timeDied = System.currentTimeMillis();
                                            break;
                                        } else {
                                            playerShip.removeUpgrade(Ship.ARMOR);
                                        }

                                    }

                                }
                            }
                        }

                        //if a UFO collides with an asteroid kill them both.
                        for (Asteroid o : asteroids) {

                            if (o == null || o.dead) {
                                continue;
                            }

                            for (UFO u : ufos) {

                                if (u == null || u.dead) {
                                    continue;
                                }

                                if (u.intersects(o)) {

                                    if (sound && boom != null) {
                                        boom.play();
                                    }

                                    o.dead = true;
                                    u.dead = true;

                                    spawnChildren(o, u);

                                    loot.add(new Loot(u, new Material(Material.ALIEN_TECH)));

                                    if (!o.material.equals(Material.ROCK)) {
                                        loot.add(new Loot(o, o.material));
                                    }

                                }

                            }
                        }

                        //if the player touches loot, "get" it and add the points to the ship's score
                        for (Loot l : loot) {

                            if (l == null || l.dead) {
                                continue;
                            }

                            if (playerShip != null && playerShip.isAlive()) {
                                if (playerShip.intersects(l)) {

                                    if (sound && cash != null) {
                                        cash.play();
                                    }

                                    l.dead = true;
                                    playerShip.addPoints(l.getValue());
                                }
                            }

                            for (UFO o : ufos) {

                                if (o == null || o.dead) {
                                    continue;
                                }

                                if (o.intersects(l) && !o.dead) {
                                    l.dead = true;

                                    if (sound && cash != null) {
                                        cash.play();
                                    }
                                }
                            }

                        }

                    }//end try
                    catch (Exception e) {
                        System.out.println("Exception: In destroy-object section!" + System.currentTimeMillis());
                    }

                    //Remove all the moribund game objects from their ArrayLists
                    for (Asteroid a : asteroids) {
                        if (a != null && a.dead) {
                            asteroids.remove(a);
                        }
                    }

                    for (Bullet b : bullets) {
                        if (b != null && b.dead) {
                            bullets.remove(b);
                        }
                    }

                    for (Bullet b : UFO.UfoBullets) {
                        if (b != null && b.dead) {
                            UFO.UfoBullets.remove(b);
                        }
                    }

                    for (UFO ufo : ufos) {
                        if (ufo != null && ufo.dead) {
                            ufos.remove(ufo);
                        }
                    }

                    for (Loot l : loot) {
                        if (l != null && l.dead) {
                            loot.remove(l);
                        }
                    }

                    //if the map has been cleared and 5 seconds have passed return to the main menu
                    if (mapcleared && System.currentTimeMillis() - timeCleared >= 5000) {
                        menu.addMainMenu();
                    }

                    //if the player is dead and five seconds have passed give them the death prompt
                    if (playerShip.isAlive() == false && System.currentTimeMillis() - timeDied >= 5000) {
                        up = down = left = right = space = false;
                        menu.addDeathMenu();
                    }

                    //is the map cleared?
                    if (loot.size() == 0 && asteroids.size() == 0 && ufos.size() == 0) {
                        if (!mapcleared) { //if the map is already cleared we don't need to do anything
                            mapcleared = true;
                            playerShip.addPoints(1000);
                            timeCleared = System.currentTimeMillis();
                        }
                    }

                }//end try
                catch (ConcurrentModificationException e) {
                    System.out.println("Concurrent modification exception" + System.currentTimeMillis());
                } catch (Exception e) {
                    System.out.println("Exception in Run" + System.currentTimeMillis());
                }

            }//end if-!paused then

            boolean a = true;

            while (a) {

                try {
                    this.drawGame();
                    a = false;
                } catch (ConcurrentModificationException e) {
                    System.out.println("Conc Mod Exception in Run" + System.currentTimeMillis());
                } catch (Exception e) {
                    System.out.println("Another exception in RUN" + System.currentTimeMillis());
                }
            }//drawgame sometimes throws a conc mod exception--catch it and repaint the scene if it happens

            this.repaint();
            //pause for the refresh time
            try {
                Thread.sleep(1000 / FPS);
            } catch (InterruptedException e) {
                System.out.println("Interrupted Exception" + System.currentTimeMillis());
            }

        }//end while-true

    }//end method

    //------------------------------------------------------------------
    //-----------------KEYLISTENER METHODS------------------------------
    //------------------------------------------------------------------
    //handle keyboard input from the user
    public void keyPressed(KeyEvent k) {

        Random r = new Random();

        if (k == null) {
            return;
        }

        if (k.getKeyChar() == 'N' || k.getKeyChar() == 'n') {
            sound = !sound;

            if (!sound) {
                thrust.stop();
            }
            if (sound && up) {
                thrust.loop();
            }
        }

        if (k.getKeyChar() == 'p' || k.getKeyChar() == 'P') {
            paused = !paused;
            return;
        }

        if (paused || !playerShip.isAlive()) {
            return; //if we're paused or dead don't let the user cheat
        }
        //if the user presses w and has a warp drive, use it and dispose
        if (k.getKeyChar() == 'w' || k.getKeyChar() == 'W') {

            if (playerShip.hasUpgrade(Ship.WARP)) {
                playerShip.setPosition(r.nextInt(BOARD_WIDTH), r.nextInt(BOARD_HEIGHT));
                playerShip.removeUpgrade(Ship.WARP);

                if (sound && warp != null) {
                    warp.play();
                }
            }

            return;
        }

        if (k.getKeyChar() == 's' || k.getKeyChar() == 'S')
        {
            if (playerShip.shield > 0) {
                playerShip.shielded = true;
            }
            return;
        }

        if (k.getKeyCode() == KeyEvent.VK_UP)
        {
            if (!up && sound)
            {
                if (thrust != null){ thrust.loop(); }
            }
            up = true;
            return;
        }

        if (k.getKeyCode() == KeyEvent.VK_DOWN) {

            if (!down)
            {
                if (playerShip.getSpeed() > .5) {

                    if (sound && brake != null)
                    {
                        brake.play();
                    }
                }
            }

            down = true;
            return;
        }

        if (k.getKeyCode() == KeyEvent.VK_LEFT)
        {
            left = true;
            return;
        }

        if (k.getKeyCode() == KeyEvent.VK_RIGHT)
        {
            right = true;
            return;
        }

        if (k.getKeyCode() == KeyEvent.VK_SPACE) {
            space = true;
            return;
        }

    }//end method

    public void keyReleased(KeyEvent k) {

        if (k.getKeyChar() == 's' || k.getKeyChar() == 'S') {
            playerShip.shielded = false;
            return;
        }

        if (k.getKeyCode() == KeyEvent.VK_UP) {
            up = false;
            thrust.stop();
            return;
        }

        if (k.getKeyCode() == KeyEvent.VK_DOWN) {
            down = false;
            brake.stop();
            return;
        }

        if (k.getKeyCode() == KeyEvent.VK_LEFT) {
            left = false;
            return;
        }

        if (k.getKeyCode() == KeyEvent.VK_RIGHT) {
            right = false;
            return;
        }

        if (k.getKeyCode() == KeyEvent.VK_SPACE) {
            space = false;
            return;
        }

    }//end method

    //This method is a *stub*.  You can help Wikipe-- err never mind
    public void keyTyped(KeyEvent k) {
    }

    //------------------------------------------------------------------------
    //-------------------APPLET EVENT METHODS---------------------------------
    //------------------------------------------------------------------------
    //when the applet is created
    //set the font, create a backbuffer for drawing, and create a ship for the player to use
    public void init()
	{
		addKeyListener(this); // Add this class as a KeyListener
        setFocusable(true); // Ensure the applet is focusable

        try
        {
            brake = getAudioClip(new URL(getCodeBase(), "brake.au"));
            laser = getAudioClip(new URL(getCodeBase(), "laser.au"));
            cash = getAudioClip(new URL(getCodeBase(), "cashregister.au"));
            warp = getAudioClip(new URL(getCodeBase(), "warp.au"));
            boom = getAudioClip(new URL(getCodeBase(), "EXPLODE.au"));
            thrust = getAudioClip(new URL(getCodeBase(), "thrust.au"));
            ufolaser = getAudioClip(new URL(getCodeBase(), "ufolaser.au"));
        }
		catch (Exception e)
		{
            System.out.println("Error loading Sounds" + System.currentTimeMillis());
        }

        offScrnBfr = createImage(BOARD_WIDTH, BOARD_HEIGHT);
        og = offScrnBfr.getGraphics();

        og.setFont(new Font("Times", Font.PLAIN, 14));

        playerShip = new Ship(BOARD_WIDTH / 2, BOARD_HEIGHT / 2);

        generateRound();
    }

    //begin running the program--add the main menu and start the game loop thread
    public void start()
	{
        menu = new Menu(BOARD_WIDTH, this, playerShip);

        menu.addMainMenu();

        //i like this sound, and it's a nice way to let the user know the game is loaded and ready to go
        if (warp != null)
		{
            warp.play();
        }

        startGame();
    }//end method

    //does a bit of cleanup when the applet is stopped
    public void stop() {

        thrust.stop();

        removeKeyListener(this);

        if (menu != null) {
            menu.clearMenu();
        }

        if (t != null) {
            t.stop();
            t = null;
        }

    }

    //nothing to do here.
    public void destroy() {

    }

    //draws the offscreen buffer to the screen, unless the menu is out.  
    //in that case call the super's paint function to make sure that the menu is always redrawn
    public void paint(Graphics g) {

        if (g == null) {
            return;
        }
        if (offScrnBfr == null) {
            return;
        }
        if (!menu.ready) {
            super.paint(g);
            return;
        }
        g.drawImage(offScrnBfr, 0, 0, null);

    }//end method

}//end class
