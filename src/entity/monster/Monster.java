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

public abstract class Monster extends Entity {
    protected String name;
    public int id = 0;
    protected int hp;
    protected int maxHp;
    protected int defense;
    protected float speed; // pixels per second

    public int attack;
    public int exp;
    
    protected Player player;
    protected ArrayList<DamageReceive> damageReceived = new ArrayList<DamageReceive>();

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

    public boolean isDead() { return hp <= 0; }

    public void damage(int damage) {
        hp -= damage;
        damageReceived.add(new DamageReceive(damage, Game.FPS)); // show damage received for 3 seconds
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

    abstract public void update();
    abstract public void draw(Graphics g);
}
