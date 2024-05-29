package entity.monster;

import java.awt.Graphics;

import entity.*;
import main.Game;

public class Monster extends Entity {
    public String name;
    public int id = 0;

    private Player player;

    public int hp;
    public int maxHp;
    public int attack;
    public int defense;
    public float speed; // pixels per second
    public int exp;

    public Monster(Game game, String name, int x, int y, int hp, int attack, int speed, int exp, Player player) {
        super(game, x, y, 50, 50);
        this.name = name;
        this.hp = hp;
        this.maxHp = hp;
        this.attack = attack;
        this.speed = speed * 60;
        this.exp = exp;
        this.player = player;
    }

    public void setId(int id) { this.id = id; }

    public void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    public void update() {
        // TODO
        update(player.x, player.y);
    }

    public void update(float x, float y) {
        // move towards player
        float dx, dy;
        if (this.x < x) { dx = speed; }
        else if (this.x > x) { dx = -speed; }
        else { dx = 0; }
        if (this.y < y) { dy = speed; }
        else if (this.y > y) { dy = -speed; }
        else { dy = 0; }
        dx /= Game.FPS;
        dy /= Game.FPS;
        move(dx, dy);
    }

    public boolean isDead() { return hp <= 0; }

    public void damage(int damage) {
        hp -= damage;
    }

    public void draw(Graphics g) {
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);

        int cx = (int)Math.round(screenX - width/2.0);
        int cy = (int)Math.round(screenY - height/2.0);
        g.setColor(java.awt.Color.BLACK);
        g.drawRect(cx, cy, width, height);
        g.fillRect(cx, cy, width, height);
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

        // draw hitbox
        g.setColor(java.awt.Color.RED);
        g.drawRect(cx, cy, width, height);
    }

}
