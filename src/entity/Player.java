package entity;

import java.awt.Graphics;

import entity.enemy.Enemy;
import main.GamePanel;
import weapons.*;

public class Player extends Entity{
    public String name;
    // public int x;
    // public int y;
    // public int width;
    // public int height;

    public int hp;
    public int maxHp;
    public int attack;
    public int defense;

    public int damageCooldown;
    private int curMaxDamage;

    public Weapon[] weapons;
    public int weaponCount;

    private float speed;
    private int FPS;

    private GamePanel gamePanel;
    private final int renderMode;

    public Player(String name, int x, int y, int speed, GamePanel gamePanel) {
        super(x, y, 50, 50);
        this.gamePanel = gamePanel;
        this.renderMode = gamePanel.renderMode;
        this.FPS = gamePanel.getFPS();
        this.name = name;
        this.speed = (float)speed * 60 / FPS;
        this.attack = 20;
        this.hp = 100;
        this.maxHp = 100;
        this.defense = 0;
        this.damageCooldown = 0;
        this.weapons = new Weapon[20];
        this.weapons[0] = (Weapon)(new SpinningSword(100, 100, attack, 300, 100, this));
        this.weapons[1] = (Weapon)(new Bow(10, 10, attack, 600, 1, this));
        this.weaponCount = 2;
        System.out.println("Player created at " + x + ", " + y + " with speed " + this.speed + ", FPS " + FPS);
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        for (int i = 0; i < weaponCount; i++) {
            weapons[i].setGamePanel(gamePanel);
        }
    }

    public GamePanel getGamePanel() { return gamePanel; }

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setName(String name) { this.name = name; }
    public void setPos(int x, int y) { this.x = x; this.y = y; }

    public void moveUp() { y -= speed; }
    public void moveDown() { y += speed; }
    public void moveLeft() { x -= speed; }
    public void moveRight() { x += speed; }

    public void update() {
        // System.out.println("\rPlayer at " + x + ", " + y);
        damageCooldown--;
        for (int i = 0; i < weaponCount; i++) {
            weapons[i].update();
        }
        if (damageCooldown > 0) return;
        if (renderMode == 1) {
            curMaxDamage = 0;
            int monsterCount = gamePanel.getMonsterCount();
            Enemy[] monsters = gamePanel.getMonsters();
            for (int i = 0; i < monsterCount; i++) {
                if (monsters[i].x - monsters[i].width / 2 < x + width / 2 && monsters[i].x + monsters[i].width / 2 > x - width / 2 &&
                    monsters[i].y - monsters[i].height / 2 < y + height / 2 && monsters[i].y + monsters[i].height / 2 > y - height / 2) {
                    // curMaxDamage = Math.max(curMaxDamage, monsters[i].attack);
                    colideWith(monsters[i]);
                }
            }
        }
        takeDamage();
    }

    public void colideWith(Enemy enemy) {
        if (damageCooldown > 0) return;
        curMaxDamage = Math.max(curMaxDamage, enemy.attack);
    }

    public void takeDamage() {
        if (curMaxDamage == 0) return;
        damage(curMaxDamage);
        damageCooldown = FPS / 2;
        if (renderMode == 0) curMaxDamage = 0;
    }

    public void damage(int damage) {
        if (damage <= 0) return;
        hp -= damage - defense;
        if (hp < 0) {
            hp = 0;
        }
    }

    /**
     * Draw the weapons and then the player
     * @param g the Graphics object
     */
    public void draw(Graphics g) {
        // System.out.println("Drawing player at " + x + ", " + y);
        int cx = (gamePanel.getWidth() - width) / 2;
        int cy = (gamePanel.getHeight() - height) / 2;
        Weapon[] weapons = this.weapons;
        for (int i = 0; i < weaponCount; i++) {
            weapons[i].draw(g);
        }
        g.setColor(java.awt.Color.BLUE);
        g.drawRect(cx, cy, width, height);
        g.drawString(name, cx, cy - 5);
        
        // Draw health bar
        int healthBarWidth = width;
        int healthBarHeight = 5;
        int healthBarX = cx;
        int healthBarY = cy - healthBarHeight - 15;
        g.setColor(java.awt.Color.RED);
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        g.setColor(java.awt.Color.GREEN);
        g.fillRect(healthBarX, healthBarY, healthBarWidth * hp / maxHp, healthBarHeight);
    }

}
