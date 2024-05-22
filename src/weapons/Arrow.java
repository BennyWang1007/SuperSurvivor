package weapons;

import java.awt.*;
import java.util.Set;
import java.util.TreeSet;

import entity.enemy.Enemy;

public class Arrow extends Projectile {

    private float speed; // pixels per frame
    private Enemy target = null;
    private float targetX, targetY;
    private int maxBounce;
    
    // private Function<Enemy, Void> attackOn;
    private Set<Integer> hitEnemyIds;

    public Arrow(float x, float y, int width, int height, float speed, Weapon weapon) {
        super(x, y, width, height, 60, weapon);
        this.speed = speed;
        // attackOn = weapon::attackOn;
        hitEnemyIds = new TreeSet<Integer>();
        maxBounce = 3;
    }

    public void update() {
        monsterCount = weapon.gamePanel.getMonsterCount();
        monsters = weapon.gamePanel.getMonsters();
        // System.out.println(target);
        if (target == null) {
            findTarget();
        } else {
            targetX = target.x;
            targetY = target.y;
            float dist = (float)Math.sqrt((targetX - x) * (targetX - x) + (targetY - y) * (targetY - y));
            // System.out.print("dist: " + dist + ", speed: " + speed);
            if (dist < speed) {
                x = targetX;
                y = targetY;
            } else {
                x += (targetX - x) / dist * speed;
                y += (targetY - y) / dist * speed;
            }
        }
        collisionCheck();
    }

    public void attackOn(Enemy monster) {
        // System.out.println("Arrow at " + x + ", " + y + " hits monster at " + monster.x + ", " + monster.y);
        if (hitEnemyIds.contains(monster.id)) return;
        monster.damage(weapon.attack);
        hitEnemyIds.add(monster.id);
        maxBounce--;
        if (maxBounce == 0) toDestroy = true;
        findTarget();
    }

    // find the nearest unhit target
    public void findTarget() {
        float minDist = Float.POSITIVE_INFINITY, dist;
        Enemy curTarget = target;
        for (int i = 0; i < monsterCount; i++) {
            if (hitEnemyIds.contains(monsters[i].id)) {
                continue;
            }
            dist = (float)Math.sqrt((monsters[i].x - x) * (monsters[i].x - x) + (monsters[i].y - y) * (monsters[i].y - y));
            if (dist < minDist) {
                minDist = dist;
                target = monsters[i];
                // hitEnemyIds.add(target.id);
            }
        }
        if (curTarget != null && target == curTarget) toDestroy = true;
    }

    public void draw(Graphics g) {
        int cx = (int)Math.round((x - weapon.owner.x + weapon.gamePanel.getWidth() - width) / 2);
        int cy = (int)Math.round((y - weapon.owner.y + weapon.gamePanel.getHeight() - height) / 2);
        g.setColor(Color.BLACK);
        g.fillOval((int)cx - width / 2, (int)cy - height / 2, width, height);

        // draw hitbox
        g.setColor(Color.RED);
        g.drawRect((int)cx - width / 2, (int)cy - height / 2, width, height);
    }


    
}
