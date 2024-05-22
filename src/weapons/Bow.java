package weapons;

import java.awt.Color;
import java.awt.Graphics;

import entity.Player;
import entity.enemy.Enemy;

public class Bow extends Weapon {

    private float arrowSpeed; // pixels per second
    private float cooldownTime; // in seconds

    private Arrow[] arrows;
    private int arrowCount;

    public Bow(int width, int height, int attack, float arrowSpeed, float cooldownTime, Player owner) {
        super(width, height, attack, owner);
        this.arrowSpeed = arrowSpeed;
        this.cooldownTime = cooldownTime;
        arrows = new Arrow[100];
        arrowCount = 0;
    }

    public void update() {
        playerX = (int)Math.round(owner.x);
        playerY = (int)Math.round(owner.y);
        // System.out.println("current arrow count: " + arrowCount);
        monsters = gamePanel.getMonsters();
        monsterCount = gamePanel.getMonsterCount();
        for (int i = 0; i < arrowCount; i++) {
            arrows[i].update();
            if (arrows[i].toDestroy) {
                removeProjectile(i);
                i--;
            }
        }
        if (cooldownTime > 0) {
            cooldownTime -= 1.0 / FPS;
        } else if (monsterCount > 0) {
            arrows[arrowCount] = new Arrow(playerX, playerY, 10, 10, (float)arrowSpeed / FPS, this);
            arrowCount++;
            cooldownTime = 1f;
        }
    }

    public void loadAnimation() {};

    public void draw(Graphics g) {
        int cx = (gamePanel.getWidth() - width) / 2;
        int cy = (gamePanel.getHeight() - height) / 2;

        // draw a triangle
        g.setColor(Color.RED);
        g.fillPolygon(new int[]{cx - 10, cx + 10, cx}, new int[]{cy + 10, cy + 10, cy - 10}, 3);
        for (int i = 0; i < arrowCount; i++) {
            arrows[i].draw(g);
        }
    }

    public void attackOn(Enemy enemy) {
        // TODO
    }

    public void removeProjectile(Arrow arrow) {
        for (int i = 0; i < arrowCount; i++) {
            if (arrows[i] == arrow) {
                arrows[i] = arrows[arrowCount - 1];
                arrowCount--;
                return;
            }
        }
    }

    public void removeProjectile(int index) {
        if (index < 0 || index >= arrowCount) { return; }
        arrows[index] = arrows[arrowCount - 1];
        arrowCount--;
    }
    
}
