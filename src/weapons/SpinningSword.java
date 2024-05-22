package weapons;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Iterator;

import entity.Player;
import entity.enemy.Enemy;


public class SpinningSword extends Weapon{
    
    private float degreePerSecond;
    private float distance;
    private float degree;
    private final int randerMode;

    public SpinningSword(int width, int height, int attack, float degreePerSecond, float distance, Player owner){
        super(width, height, attack, owner);
        this.degreePerSecond = degreePerSecond;
        this.distance = distance;
        this.degree = 0;
        readImage("res/sword_900.png");
        randerMode = owner.getGamePanel().renderMode;
        cooldownTime = 0.5f;
    }

    public void update() {
        playerX = (int)Math.round(owner.x);
        playerY = (int)Math.round(owner.y);
        degree += degreePerSecond / FPS;
        if (degree >= 360) { degree -= 360; }

        offsetX = (int)(Math.cos(Math.toRadians(degree)) * distance);
        offsetY = (int)(Math.sin(Math.toRadians(degree)) * distance);
        x = playerX + offsetX;
        y = playerY + offsetY;
        
        if (randerMode == 1) {
            collisionCheck();
        }
        decreaseCooldowns();
    }

    public void decreaseCooldowns() {
        // remove cooldowns <= 0
        Iterator<Integer> iterator = attackCooldowns.keySet().iterator();
        while (iterator.hasNext()) {
            int id = iterator.next();
            attackCooldowns.put(id, attackCooldowns.get(id) - 1);
            if (attackCooldowns.get(id) <= 0) {
                iterator.remove();
            }
        }
    }

    public void attackOn(Enemy enemy) {
        if (attackCooldowns.containsKey(enemy.id)) {
            return;
        }
        enemy.damage(attack);
        attackCooldowns.put(enemy.id, FPS);
    }

    public void draw(Graphics g) {
        // System.out.println("Drawing weapon");
        int cx = (int)Math.round(x - width / 2.0 - playerX + owner.getGamePanel().getWidth() / 2.0);
        int cy = (int)Math.round(y - height / 2.0 - playerY + owner.getGamePanel().getHeight() / 2.0);
        AffineTransform at = AffineTransform.getTranslateInstance(cx, cy);
        at.rotate(Math.toRadians(degree), image.getWidth() / 2, image.getHeight() / 2);
        ((Graphics2D) g).drawImage(image, at, null);

        // draw hitbox
        g.setColor(Color.RED);
        ((Graphics2D) g).drawRect(cx, cy, width, height);
    }

    public void loadAnimation() {};
}