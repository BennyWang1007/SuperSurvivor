package weapons;

import java.awt.Graphics;
import java.util.HashMap;

import entity.Entity;
import entity.enemy.Enemy;


public abstract class Projectile extends Entity {

    protected Weapon weapon;
    protected float cooldownTime; // in seconds
    protected HashMap<Integer, Integer> attackCooldowns;
    public boolean toDestroy = false;
    protected int FPS;

    protected Enemy[] monsters;
    protected int monsterCount;

    public Projectile(float x, float y, int width, int height, int FPS, Weapon weapon) {
        super(x, y, width, height);
        this.weapon = weapon;
        this.FPS = FPS;
    }

    public void collisionCheck() {
        monsterCount = weapon.gamePanel.getMonsterCount();
        monsters = weapon.gamePanel.getMonsters();
        for (int i = 0; i < monsterCount; i++) {
            if (monsters[i].x - monsters[i].width / 2 < x + width / 2 && monsters[i].x + monsters[i].width / 2 > x - width / 2 &&
                monsters[i].y - monsters[i].height / 2 < y + height / 2 && monsters[i].y + monsters[i].height / 2 > y - height / 2) {
                attackOn(monsters[i]);
            }
        }
    }

    abstract public void update();
    abstract public void attackOn(Enemy monster);
    abstract public void draw(Graphics g);
    
}
