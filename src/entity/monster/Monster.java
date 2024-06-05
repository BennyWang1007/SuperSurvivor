package entity.monster;

import java.awt.Graphics;
import java.util.ArrayList;

import entity.*;
import main.Game;

class DamageReceive {
    public int damage;
    public int time; // frames last
    public DamageReceive(int damage, int time) {
        this.damage = damage;
        this.time = time;
    }
}

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
    public ArrayList<DamageReceive> damageReceived = new ArrayList<DamageReceive>();

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
        float distance = (float)Math.hypot(x - this.x, y - this.y);
        if (distance < 1) {
            dx = 0;
            dy = 0;
        } else {
            dx = speed * (x - this.x) / distance;
            dy = speed * (y - this.y) / distance;
        }
        dx /= Game.FPS;
        dy /= Game.FPS;
        move(dx, dy);
    }

    public boolean isDead() { return hp <= 0; }

    public void damage(int damage) {
        hp -= damage;
        damageReceived.add(new DamageReceive(damage, Game.FPS)); // show damage received for 3 seconds
    }

    public void draw(Graphics g) {
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);

        int cx = (int)Math.round(screenX - width/2.0);
        int cy = (int)Math.round(screenY - height/2.0);
        g.setColor(java.awt.Color.BLACK);
        g.setFont(g.getFont().deriveFont(12f));
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

    public void drawDamageReceived(Graphics g) {
        int screenX = game.translateToScreenX(x);
        int screenY = game.translateToScreenY(y);
        // show damage received at right-up of the health bar
        int cx = (int)Math.round(screenX + width/2.0 - 15);
        int cy = (int)Math.round(screenY - height/2.0 - 15);
        g.setColor(java.awt.Color.RED);
        g.setFont(g.getFont().deriveFont(10.0f));
        for (int i = 0; i < damageReceived.size(); i++) {
            // TODO: maybe add an animation to show the damage received
            float alpha = (float)damageReceived.get(i).time / Game.FPS;
            cy -= (int)(10 * alpha);
            g.setColor(new java.awt.Color(255, 0, 0, (int)(255 * alpha)));
            g.drawString(Integer.toString(damageReceived.get(i).damage), cx, cy - 10 * i);
        }
        damageReceived.removeIf(d -> d.time-- <= 0);
    }
}
