package monsters;

import java.awt.Graphics;

// import weapons.*;
import main.*;

public class Monster {
    public String name;
    public int x; // center x
    public int y; // center y
    public int width;
    public int height;
    public int id = 0;

    private Player player;

    public int hp;
    public int maxHp;
    public int attack;
    public int defense;
    private int speed;

    public Monster(String name, int x, int y, int hp, int attack, int speed, Player player) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = hp;
        this.attack = attack;
        this.speed = speed;
        this.player = player;
        width = 50;
        height = 50;
    }

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public void setName(String name) { this.name = name; }
    public void setId(int id) { this.id = id; }

    public void moveUp() { y -= speed; }
    public void moveDown() { y += speed; }
    public void moveLeft() { x -= speed; }
    public void moveRight() { x += speed; }

    public void move(int dx, int dy) {
        x += dx;
        y += dy;
    }

    public void update() {
        // TODO
        update(player.x, player.y);
    }

    public void updateRandom() {
        int dx = (int)(Math.random() * speed * 2) - speed;
        int dy = (int)(Math.random() * speed * 2) - speed;
        move(dx, dy);
    }

    public void update(int x, int y) {
        // move towards player
        int dx, dy;
        if (this.x < x) { dx = speed; }
        else if (this.x > x) { dx = -speed; }
        else { dx = 0; }
        if (this.y < y) { dy = speed; }
        else if (this.y > y) { dy = -speed; }
        else { dy = 0; }
        move(dx, dy);
    }

    public boolean isDead() { return hp <= 0; }

    public boolean damage(int damage) {
        // System.out.println(name + " took " + damage + " damage");
        hp -= damage;
        return isDead();
    }

    public void draw(Graphics g) {
        // System.out.println("Drawing monster at " + x + ", " + y);
        // int cx = x - width / 2;
        // int cy = y - height / 2;
        int cx = x - width / 2 - player.x + player.getGamePanel().getWidth() / 2;
        int cy = y - height / 2 - player.y + player.getGamePanel().getHeight() / 2;
        g.drawRect(cx, cy, width, height);
        g.fillRect(cx, cy, width, height);
        // g.drawString(name, x, y - 5);
        // use id instead of name
        g.drawString(Integer.toString(id), cx, cy - 5);

        // draw health bar, red and green, above the monster
        int healthBarWidth = width;
        int healthBarHeight = 5;
        int healthBarX = cx;
        int healthBarY = cy - healthBarHeight - 15;
        g.setColor(java.awt.Color.RED);
        g.fillRect(healthBarX, healthBarY, healthBarWidth, healthBarHeight);
        g.setColor(java.awt.Color.GREEN);
        g.fillRect(healthBarX, healthBarY, healthBarWidth * hp / maxHp, healthBarHeight);
        g.setColor(java.awt.Color.BLACK);

    }

}
