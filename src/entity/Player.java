package entity;

import java.awt.Graphics;
import java.util.HashSet;
import java.util.Set;

import entity.monster.Monster;
import main.Game;
import weapons.*;

public class Player extends Entity{
    public String name;
    public int hp;
    public int maxHp;
    public int attack;
    public int defense;
    private float speed;
    public int exp;
    public int level;
    public final int maxLevel = 7;
    public int[] expTable = {0, 100, 200, 400, 800, 1600, 3200, 6400};

    public int damageCooldown;
    private int curMaxDamage;

    private Set<Weapon> weapons;

    public Player(Game game, String name, int x, int y, int speed) {
        super(game, x, y, 50, 50);
        this.name = name;
        this.speed = speed;
        this.attack = 20;
        this.hp = 500;
        this.maxHp = 500;
        this.defense = 0;
        this.exp = 0;
        this.level = 1;
        this.damageCooldown = 0;
        this.weapons = new HashSet<>();
    }

    public void moveUp() { move(x, (float)(y - speed * Game.DELTA_TIME)); }
    public void moveDown() { move(x, (float)(y + speed * Game.DELTA_TIME)); }
    public void moveLeft() { move((float)(x - speed * Game.DELTA_TIME), y); }
    public void moveRight() { move((float)(x + speed * Game.DELTA_TIME), y); }

    public void move(float x, float y) {
        if (game.isValidPosition(x, y)) {
            this.x = x;
            this.y = y;
        }
    }

    public void update() {
        // System.out.println("\rPlayer at " + x + ", " + y);
        damageCooldown--;
        weapons.forEach(Weapon::update);
        if (damageCooldown > 0) return;
        takeDamage();
        if (level < maxLevel && exp >= expTable[level]) {
            exp -= expTable[level];
            levelUp();
        }
    }

    public Set<Weapon> getWeapons() { return weapons; }

    public void collideWith(Monster monster) {
        if (damageCooldown > 0) return;
        curMaxDamage = Math.max(curMaxDamage, monster.attack);
    }

    private void takeDamage() {
        if (curMaxDamage == 0) return;
        damage(curMaxDamage);
        damageCooldown = Game.FPS / 2;
        curMaxDamage = 0;
    }

    private void damage(int damage) {
        if (damage <= 0) return;
        hp -= damage - defense;
        if (hp < 0) {
            hp = 0;
        }
    }

    private void levelUp() {
        // TODO: level up player
        level++;
        maxHp += 10;
        hp = maxHp;
        attack += 5;
        defense += 2;
        System.out.println("player.level: " + level);
        // for testing
        if (level == 2) {
            Weapon weapon = new Bow(game, 100, 100, attack * 3, 150, 1, this);
            weapons.add(weapon);
        } else if (level == 3) {
            Weapon weapon = new SpinningSword(game, 100, 100, attack, 300, 100, this);
            weapons.add(weapon);
        } else {
            for(Weapon weapon : weapons) {
                if(weapon instanceof Aura) {
                    weapon.levelUp();
                }
            }
        }
    }

    public void addExp(int exp) {
        this.exp += exp;
    }

    /**
     * Draw the weapons and then the player
     * @param g the Graphics object
     */
    public void draw(Graphics g) {
        // System.out.println("Drawing player at " + x + ", " + y);
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);
        int drawX = (screenX - width/2);
        int drawY = (screenY - height/2);
        weapons.forEach(weapon -> weapon.draw(g));
        g.setColor(java.awt.Color.BLUE);
        g.drawRect(drawX, drawY, width, height);
        g.drawString(name, drawX, drawY - 5);
        
        // Draw health bar
        int healthBarWidth = width;
        int healthBarHeight = 5;
        int healthBarX = drawX;
        int healthBarY = drawY - healthBarHeight - 15;
        g.setColor(java.awt.Color.RED);
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        g.setColor(java.awt.Color.GREEN);
        g.fillRect(healthBarX, healthBarY, healthBarWidth * hp / maxHp, healthBarHeight);
    }

}
