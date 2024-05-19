package weapons;

import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;

import entity.*;
import entity.enemy.Enemy;
import main.*;

public abstract class Weapon extends Entity{

    // protected int x; // center x
    // protected int y; // center y
    // protected int width;
    // protected int height;

    protected int offsetX;
    protected int offsetY;
    protected int playerX;
    protected int playerY;
    protected int FPS;
    protected GamePanel gamePanel;

    protected int attack;
    protected HashMap<Integer, Integer> attackCooldowns;
    
    protected BufferedImage image;
    // private BufferedImage[] images; // for animation
    private BufferedImage originalImage;
    

    protected Player owner;
    protected Enemy[] monsters; // maybe consider using an arraylist + hashmap to search by id
    protected int monsterCount;

    protected float cooldownTime; // in seconds

    public Weapon(int width, int height, int attack, Player owner) {
        super(0, 0, width, height);
        // x = 0;
        // y = 0;
        // this.width = width;
        // this.height = height;
        this.attack = attack;
        this.owner = owner;
        playerX = 0;
        playerY = 0;
        // loadAnimation();
        monsterCount = 0;
        attackCooldowns = new HashMap<Integer, Integer>();
        this.gamePanel = owner.getGamePanel();
        update();
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        FPS = gamePanel.getFPS();
    }

    public void readImage(String imageName) {
        Image img = null;
        try {
            img = ImageIO.read(new File(imageName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        originalImage.getGraphics().drawImage(img, 0, 0, width, height, null);
        image = originalImage;
    }

    public void setSize(int width, int height) {
        originalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        originalImage.getGraphics().drawImage(image, 0, 0, width, height, null);
    }

    public void collisionCheck() {
        // System.out.println("x: " + x + ", y: " + y + ", width: " + width + ", height: " + height);
        monsterCount = gamePanel.getMonsterCount();
        monsters = gamePanel.getMonsters();
        for (int i = 0; i < monsterCount; i++) {
            if (monsters[i].x - monsters[i].width / 2 < x + width / 2 && monsters[i].x + monsters[i].width / 2 > x - width / 2 &&
                monsters[i].y - monsters[i].height / 2 < y + height / 2 && monsters[i].y + monsters[i].height / 2 > y - height / 2) {
                attackOn(monsters[i]);
            }
        }
    }

    public void colideWith(Enemy enemy) {
        if (attackCooldowns.containsKey(enemy.id)) {
            return;
        }
        attackCooldowns.put(enemy.id, FPS);
        enemy.damage(attack);
    }

    public abstract void update();
    public abstract void attackOn(Enemy enemy);
    public abstract void draw(Graphics g);
    public abstract void loadAnimation();
}
