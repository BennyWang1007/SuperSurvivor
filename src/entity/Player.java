package entity;

import java.awt.*;
import java.util.*;

import entity.monster.Monster;
import main.Game;
import weapons.*;

public class Player extends Entity{
    public String name;
    public int hp;
    public int maxHp;
    public int attack;
    public int defense;
    public float speed;
    public int exp;
    public int level;
    public final int maxLevel = 39;
    // public int[] expTable = {0, 100, 200, 400, 800, 1600, 3200, 6400};
    public int[] expTable = {0, 100, 100, 100, 100, 100, 100, 120, 140, 160, 180, 200, 220, 240, 260, 280, 300, 320, 340, 360, 380, 400, 420, 440, 460, 480, 500, 520, 540, 560, 580, 600, 620, 640, 660, 680, 700, 720, 740, 760, 780, 800};

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

    public <T extends Weapon> T getWeapon(Class<T> weaponType) {
        for (Weapon weapon : weapons) {
            if (weaponType.isInstance(weapon)) {
                return weaponType.cast(weapon);
            }
        }
        return null;
    }

    public Aura getAura() { return getWeapon(Aura.class); }
    public SpinningSword getSpinningSword() { return getWeapon(SpinningSword.class); }
    public Bow getBow() { return getWeapon(Bow.class); }

    public ArrayList<SpinningSword> getSwords() {
        ArrayList<SpinningSword> swords = new ArrayList<>();
        for (Weapon weapon : weapons) {
            if (weapon instanceof SpinningSword) {
                swords.add((SpinningSword) weapon);
            }
        }
        return swords;
    }

    public void addBow() { weapons.add(new Bow(game, 100, 100, 5, 360, 1, this)); }
    public void addSpinningSword() { weapons.add(new SpinningSword(game, 100, 100, 1, 300, 100, this)); }
    public void addAura() { weapons.add(new Aura(game, 200, 200, 0.5f, 100, this)); }

    public void collideWith(Monster monster) {
        if (damageCooldown > 0) return;
        curMaxDamage = Math.max(curMaxDamage, monster.attack);
    }

    public void collideWith(Projectile projectile) {
        hp -= projectile.attack - defense;
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

    public void setAttack(int attack) {
        this.attack = attack;
        for (Weapon weapon : weapons) {
            weapon.setAttack(this.attack);
        }
    }
    public void addAttack(int attack) {
        this.attack += attack;
        for (Weapon weapon : weapons) {
            weapon.setAttack(this.attack);
        }
    }

    private void levelUp() {
        level++;
        maxHp += 10;
        hp = maxHp;
        addAttack(5);
        defense += 2;
        game.levelUp();
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
        g.setFont(g.getFont().deriveFont(12f));
        ((Graphics2D) g).setStroke(new BasicStroke(2));
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
